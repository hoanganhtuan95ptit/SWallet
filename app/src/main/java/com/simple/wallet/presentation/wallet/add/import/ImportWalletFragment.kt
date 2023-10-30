package com.simple.wallet.presentation.wallet.add.import

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.ui.base.fragments.BaseViewModelFragment
import com.simple.coreapp.ui.dialogs.OptionFragment.Companion.KEY_REQUEST
import com.simple.coreapp.utils.AppException
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.getSerializableOrNull
import com.simple.coreapp.utils.extentions.clear
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.coreapp.utils.extentions.haveText
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.setVisible
import com.simple.coreapp.utils.extentions.text
import com.simple.coreapp.utils.extentions.text.TextImage
import com.simple.coreapp.utils.extentions.text.TextRes
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.updateMargin
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.navigation.utils.ext.setNavigationResultListener
import com.simple.state.ResultState
import com.simple.state.isSuccess
import com.simple.state.toSuccess
import com.simple.wallet.DATA
import com.simple.wallet.DP_24
import com.simple.wallet.DP_32
import com.simple.wallet.PARAM_ACTION
import com.simple.wallet.R
import com.simple.wallet.databinding.FragmentImportWalletBinding
import com.simple.wallet.domain.entities.scan.ScanData
import com.simple.wallet.presentation.wallet.add.AddWalletViewModel

class ImportWalletFragment : BaseViewModelFragment<FragmentImportWalletBinding, ImportWalletViewModel>() {


    private val scanString: String? by lazy {
        arguments?.getString(SCAN_STRING)
    }


    private var clipboard by autoCleared<ClipboardManager>()

    private var onPrimaryClipChangedListener by autoCleared<ClipboardManager.OnPrimaryClipChangedListener>()


    private val addWalletViewModel: AddWalletViewModel by lazy {
        getViewModelGlobal(AddWalletViewModel::class)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onDetach() {
        super.onDetach()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = binding ?: return

        clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        onPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {

            binding.tvPaste.setVisible(clipboard?.haveText() == true)
        }

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            binding.ivBack.updateMargin(top = heightStatusBar)
            binding.tvContinue.updateMargin(bottom = heightNavigationBar + DP_32)
        }
    }

    override fun onViewReady() {
        super.onViewReady()

        setupUI()

        observerData()
    }

    override fun onResume() {
        super.onResume()

        val binding = binding ?: return

        binding.tvPaste.post {

            binding.tvPaste.setVisible(clipboard?.haveText() == true)
        }

        clipboard?.addPrimaryClipChangedListener(onPrimaryClipChangedListener)
    }

    override fun onPause() {
        super.onPause()

        clipboard?.removePrimaryClipChangedListener(onPrimaryClipChangedListener)
    }

    private fun setupUI() {

        val binding = binding ?: return

        binding.edtKey.doAfterTextChanged {

            binding.ivClear.isVisible = it?.isNotEmpty() == true

            viewModel.updateInputKey(it.toString())
        }


        binding.ivBack.setOnClickListener {

            dismiss()
        }

        binding.ivClear.setOnClickListener {

            binding.edtKey.setText("")
        }

        binding.tvContinue.setDebouncedClickListener(anim = true) {

            addWalletViewModel.importWallet("wallet 1", binding.edtKey.text.toString(), viewModel.walletType.get().toSuccess()!!.data)

            dismiss()

            offerDeepLink("/confirm-wallet")
        }


        setNavigationResultListener(REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET) { _, b ->

            val result: ScanData = b.getSerializableOrNull(DATA) ?: return@setNavigationResultListener

            binding.edtKey.setText(result.text)
        }

        binding.ivScan.setDebouncedClickListener {

            offerDeepLink("/camera?$KEY_REQUEST=$REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET&$PARAM_ACTION=scan")
        }


        binding.tvPaste.setOnClickListener {

            binding.edtKey.setText(clipboard?.text() ?: "")

            clipboard?.clear()
        }

        binding.tvPaste.setText(listOf(TextImage(R.drawable.ic_copy_accent_24dp, DP_24), TextRes(R.string.action_paste)).toText(" "))
    }

    private fun observerData() = with(viewModel) {

        walletType.observe(viewLifecycleOwner) {

            val binding = binding ?: return@observe

            binding.tvError.setVisible(it is ResultState.Failed && (it.cause !is AppException || it.cause.asObject<AppException>().code != ImportWalletViewModel.ImportWalletErrorCode.EMPTY))

            binding.tvContinue.isSelected = it.isSuccess()
            binding.tvContinue.isClickable = it.isSuccess()
        }

        scanString?.let {

            val binding = binding ?: return@let

            binding.edtKey.setText(it)
        }
    }

    companion object {

        const val SCAN_STRING = "SCAN_STRING"

        const val REQUEST_CONFIRM_WALLET = "REQUEST_CONFIRM_WALLET"

        const val REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET = "REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET"

        fun newInstance(scanString: String = "", requestKey: String? = ""): ImportWalletFragment = ImportWalletFragment().apply {

            arguments = Bundle().apply {

                putString(SCAN_STRING, scanString)
                putString(KEY_REQUEST, requestKey)
            }
        }
    }
}

class ImportWalletProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/import-wallet"
    }

    override fun provideScope(deepLink: String): Class<*> {

        return Activity::class.java
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return ImportWalletFragment.newInstance()
    }
}
