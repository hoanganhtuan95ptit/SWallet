package com.simple.wallet.presentation.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.simple.adapter.MultiAdapter
import com.simple.coreapp.ui.base.fragments.BaseViewModelFragment
import com.simple.coreapp.ui.dialogs.OptionFragment.Companion.KEY_REQUEST
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.getSerializableOrNull
import com.simple.coreapp.utils.extentions.doOnHeightStatusChange
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.TextImage
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.updateMargin
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.navigation.utils.ext.setNavigationResultListener
import com.simple.wallet.DATA
import com.simple.wallet.DP_8
import com.simple.wallet.PARAM_ACTION
import com.simple.wallet.PAYLOAD_PAIR
import com.simple.wallet.PAYLOAD_SLIDE
import com.simple.wallet.R
import com.simple.wallet.databinding.FragmentHomeBinding
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.entities.scan.CameraOutputType
import com.simple.wallet.domain.entities.scan.ScanData
import com.simple.wallet.presentation.home.adapters.CategoryAdapter
import com.simple.wallet.utils.exts.encodeUrl
import com.simple.wallet.utils.exts.imageDisplay
import com.simple.wallet.utils.exts.nameDisplay
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation

class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, HomeViewModel>() {


    private var categoryAdapter by autoCleared<MultiAdapter>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategory()
        setupStatusBar()

        observeData()
    }

    private fun setupCategory() {

        val binding = binding ?: return

        binding.appbar.addOnOffsetChangedListener { _, verticalOffset ->

            binding.swipeRefreshLayout.isEnabled = (verticalOffset == 0)
        }

        val adapter = CategoryAdapter()

        categoryAdapter = MultiAdapter(adapter).apply {

            setRecyclerView(binding.recyclerView, GridLayoutManager(requireContext(), 4))
        }

    }

    private fun setupStatusBar() {

        val binding = binding ?: return

        binding.ivCurrency.setImage(R.drawable.img_bitcoin_2.toImage(), SketchFilterTransformation())

        binding.ivBackground.setImage(R.drawable.img_activity.toImage(), BlurTransformation(25, 3))

        val keyRequestChain = "KEY_REQUEST_CHAIN"

        setNavigationResultListener(keyRequestChain) { _, result ->

            val data = result.getSerializableOrNull<Chain>(DATA) ?: return@setNavigationResultListener

            viewModel.updateChain(data)
        }

        binding.tvChain.setDebouncedClickListener {

            offerDeepLink("/select-chain?$KEY_REQUEST=$keyRequestChain&chainId=${viewModel.chain.value?.id}")
        }


        val keyRequestWallet = "KEY_REQUEST_WALLET"

        setNavigationResultListener(keyRequestWallet) { _, result ->

            val data = result.getSerializableOrNull<Wallet>(DATA) ?: return@setNavigationResultListener

            viewModel.updateWallet(data)
        }

        binding.tvWallet.setDebouncedClickListener {

            offerDeepLink("/select-wallet?$KEY_REQUEST=$keyRequestWallet&walletId=${viewModel.chain.value?.id}&isSupportAllWallet=true")
        }


        val keyRequestScan = "KEY_REQUEST_SCAN"

        setNavigationResultListener(keyRequestScan) { _, result ->

            val data = result.getSerializableOrNull<ScanData>(DATA) ?: return@setNavigationResultListener

            if (data.outputType == CameraOutputType.WALLET_CONNECT) {

                offerDeepLink("/wallet-connect?$PAYLOAD_PAIR=${data.text.encodeUrl()}&$PAYLOAD_SLIDE=${Request.Slide.ANOTHER_DEVICE.value}")
            }
        }

        binding.ivScan.setDebouncedClickListener {

            offerDeepLink("/camera?$KEY_REQUEST=$keyRequestScan&$PARAM_ACTION=scan")
        }


        binding.ivAddWallet.setDebouncedClickListener {

            offerDeepLink("/add-wallet")
        }

        doOnHeightStatusChange {

            binding.vHeader.updateMargin(top = it)
        }
    }

    private fun observeData() = with(viewModel) {

        chain.observe(viewLifecycleOwner) {

            val binding = binding ?: return@observe

            binding.tvChain.setText(listOf("  ".toText(), TextImage(R.drawable.img_down_on_background_24dp, DP_8), " ".toText()).toText(""))

            binding.ivChain.setImage(it.imageDisplay)
        }

        wallet.observe(viewLifecycleOwner) {

            val binding = binding ?: return@observe

            binding.tvWallet.setText(listOf(it.nameDisplay, "  ".toText(), TextImage(R.drawable.img_down_on_background_24dp, DP_8), " ".toText()).toText(""))

            binding.ivWallet.setImage(it.imageDisplay)
        }

        categoryViewItemList.observe(viewLifecycleOwner) {

            categoryAdapter?.submitList(it)
        }
    }
}