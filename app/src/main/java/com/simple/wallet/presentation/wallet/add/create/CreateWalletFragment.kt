package com.simple.wallet.presentation.wallet.add.create

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.simple.coreapp.ui.base.fragments.BaseViewModelFragment
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.updateMargin
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.wallet.DP_32
import com.simple.wallet.databinding.FragmentCreateWalletBinding
import com.simple.wallet.presentation.wallet.add.AddWalletViewModel

class CreateWalletFragment : BaseViewModelFragment<FragmentCreateWalletBinding, CreateWalletViewModel>() {


    private val addWalletViewModel: AddWalletViewModel by lazy {
        getViewModelGlobal(AddWalletViewModel::class)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTitle()
        setupCreate()
        setupEditText()

        observeData()
    }

    private fun setupTitle() {

        val binding = binding ?: return

        binding.ivBack.setOnClickListener {

            dismiss()
        }

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            binding.ivBack.updateMargin(top = heightStatusBar)
            binding.tvCreate.updateMargin(bottom = heightNavigationBar + DP_32)
        }
    }

    private fun setupCreate() {

        val binding = binding ?: return

        binding.tvCreate.isSelected = true
        binding.tvCreate.setDebouncedClickListener(anim = true) {

            addWalletViewModel.createWallet(binding.etWalletName.text.toString())

            dismiss()

            offerDeepLink("/confirm-wallet")
        }
    }

    private fun setupEditText() {

        val binding = binding ?: return

        binding.etWalletName.doAfterTextChanged {

            binding.tvCreate.alpha = if (binding.tvCreate.isClickable) 1f else 0.6f
        }
    }

    private fun observeData() = with(viewModel) {

    }
}

class CreateWalletProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/create-wallet"
    }

    override fun provideScope(deepLink: String): Class<*> {

        return Activity::class.java
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return CreateWalletFragment()
    }
}