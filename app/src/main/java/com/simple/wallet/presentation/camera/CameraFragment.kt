package com.simple.wallet.presentation.camera

import android.Manifest
import android.os.Bundle
import android.util.Size
import android.view.Surface.ROTATION_0
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.simple.adapter.MultiAdapter
import com.simple.coreapp.ui.base.fragments.BaseViewModelFragment
import com.simple.coreapp.ui.dialogs.OptionFragment.Companion.KEY_REQUEST
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.extentions.allPermissionsGranted
import com.simple.coreapp.utils.extentions.doOnHeightStatusChange
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.observeLaunch
import com.simple.coreapp.utils.extentions.observeQueue
import com.simple.coreapp.utils.extentions.resize
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setVisible
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.setNavigationResult
import com.simple.wallet.DATA
import com.simple.wallet.PARAM_ACTION
import com.simple.wallet.databinding.FragmentCameraBinding
import com.simple.wallet.domain.entities.scan.ScanInputType
import com.simple.wallet.presentation.adapters.MessageAdapter
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf


internal class CameraFragment : BaseViewModelFragment<FragmentCameraBinding, CameraViewModel>() {


    private val action: String by lazy {

        arguments?.getString(PARAM_ACTION) ?: ""
    }

    private val keyRequest: String by lazy {

        arguments?.getString(KEY_REQUEST) ?: ""
    }


    private val permissionResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        val granted = permissions.entries.all {
            it.value
        }

        if (granted) {

            requestPermissionCamera()
        } else {

//            showConfirmPermission()
        }
    }

//    private val settingResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { permissions ->
//
//        requestPermissionCamera()
//    }


    private var infoAdapter by autoCleared<MultiAdapter>()

    private var actionAdapter by autoCleared<MultiAdapter>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBack()
        setupInfo()
        setupAction()
        setupOverlay()
        setupStatusBar()
        setupTabLayout()

        observeData()
    }

    override fun getParameter(): ParametersDefinition {

        return { parametersOf(action) }
    }


    private fun setupInfo() {

        val binding = binding ?: return

        val messageAdapter = MessageAdapter()

        infoAdapter = MultiAdapter(messageAdapter).apply {

            setRecyclerView(binding.recInfo)
        }
    }

    private fun setupAction() {

        val binding = binding ?: return

        val messageAdapter = MessageAdapter()

        actionAdapter = MultiAdapter(messageAdapter).apply {

            setRecyclerView(binding.recAction)
        }
    }

    private fun setupBack() {

        val binding = binding ?: return

        binding.ivBack.setDebouncedClickListener {

            dismiss()
        }
    }

    private fun setupOverlay() {

        val binding = binding ?: return

//        binding.overlayView.resize(1280, 2560)

        binding.spaceDrag.viewTreeObserver.addOnGlobalLayoutListener {

            binding.overlayView.updatePadding(top = binding.spaceInfo.bottom, bottom = binding.root.height - binding.spaceDrag.top)
        }
    }

    private fun setupStatusBar() = doOnHeightStatusChange { heightStatusBar ->

        val binding = binding ?: return@doOnHeightStatusChange

        binding.statusBar.resize(height = heightStatusBar)
    }

    private fun setupTabLayout() {

        val binding = binding ?: return

        val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

                viewModel.updateTabIndex(binding.tabLayout.selectedTabPosition)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        }

        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }

    private fun observeData() = with(viewModel) {

        tabIndex.observeQueue(viewLifecycleOwner) {

            val binding = binding ?: return@observeQueue

            binding.overlayView.setQrcode(inputTypeList.getOrEmpty()[it] == ScanInputType.Qrcode)
        }

        inputTypeList.observeLaunch(viewLifecycleOwner) { typeList ->

            val binding = binding ?: return@observeLaunch

            val tabLayout = binding.tabLayout

            if (typeList.size == 1) {

                tabLayout.setVisible(true)
                return@observeLaunch
            }

            typeList.map {

                it.name
            }.forEach {

                tabLayout.addTab(tabLayout.newTab().setText(it), false)
            }

            requestPermissionCamera()
        }

        infoViewItemList.observeQueue(viewLifecycleOwner) {

            infoAdapter?.submitList(it)
        }

        actionViewItemList.observeQueue(viewLifecycleOwner) {

            actionAdapter?.submitList(it)
        }

        processState.observe(viewLifecycleOwner) {

            if (it.size == 1) {

                setNavigationResult(keyRequest, bundleOf(DATA to it.first()))
                dismiss()
            }
        }
    }

    //
//    private fun showConfirmPermission() = navigator.showConfirm(this, false,
//        title = getString(R.string.scan_title_permission), message = getString(R.string.scan_message_permission),
//        negative = getString(R.string.back), positive = getString(R.string.scan_action_go_setting), onNegativeClick = {
//
//            activity?.finish()
//        }, onPositiveClick = {
//
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            intent.data = Uri.fromParts("package", requireActivity().packageName, null)
//            settingResult.launch(intent)
//        })
//
    private fun requestPermissionCamera() {

        if (requireContext().allPermissionsGranted(REQUIRED_PERMISSIONS.toList())) {

            startCamera()
        } else {

            permissionResult.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            val binding = binding ?: return@addListener

            val cameraProvider = cameraProviderFuture.get()

            val size = Size(binding.overlayView.width, binding.overlayView.height)

            val sizeMin = listOf(size.width, size.height, 600).min()

            val selector = ResolutionSelector.Builder()
                .setResolutionStrategy(ResolutionStrategy(Size(sizeMin, sizeMin), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER))
                .build()

            val preview = Preview.Builder()
                .setTargetRotation(ROTATION_0)
                .setResolutionSelector(selector)
                .build()

            preview.setSurfaceProvider(binding.preview.surfaceProvider)

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetRotation(ROTATION_0)
                .setResolutionSelector(selector)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { image ->

                if (viewModel.tabIndex.value == null) {

                    binding.tabLayout.getTabAt(0)?.select()
                }

                val points = binding.overlayView.getPoint()

                viewModel.process(image, size, points)
            }

            cameraProvider.unbindAll()

            kotlin.runCatching {
                cameraProvider.bindToLifecycle(viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

class CameraProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/camera"
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return CameraFragment().apply {

            arguments = bundleOf(*params.toList().toTypedArray())
        }
    }
}
