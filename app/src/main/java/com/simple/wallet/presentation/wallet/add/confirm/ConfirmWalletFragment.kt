package com.simple.wallet.presentation.wallet.add.confirm

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.simple.coreapp.ui.base.fragments.BaseViewModelFragment
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.coreapp.utils.extentions.observeLaunch
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.TextRes
import com.simple.coreapp.utils.extentions.text.TextWithTextColorAttrColor
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.updateMargin
import com.simple.navigation.NavigationProvider
import com.simple.state.doSuccess
import com.simple.state.isSuccess
import com.simple.wallet.DP_32
import com.simple.wallet.R
import com.simple.wallet.databinding.FragmentConfirmWalletBinding
import com.simple.wallet.presentation.wallet.add.AddWalletViewModel
import com.simple.wallet.presentation.wallet.add.import.ImportWalletFragment
import com.simple.wallet.utils.exts.uppercaseFirst


class ConfirmWalletFragment : BaseViewModelFragment<FragmentConfirmWalletBinding, AddWalletViewModel>() {


    override val viewModel: AddWalletViewModel by lazy {
        getViewModelGlobal(AddWalletViewModel::class)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = binding ?: return

        binding.tvFinish.setDebouncedClickListener {

            dismiss()
        }

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            binding.tvFinish.updateMargin(bottom = heightNavigationBar + DP_32)
            binding.lottieAnimationView.updateMargin(top = heightStatusBar)
        }

        observerData()
    }

    private fun observerData() = with(viewModel) {

        addWalletStateEvent.observeLaunch(viewLifecycleOwner) { event ->

            val state = event.getContentIfNotHandled() ?: return@observeLaunch

            val binding = binding ?: return@observeLaunch

            state.doSuccess {

                binding.tvTitle.setText(TextRes(R.string.message_add_wallet_congratulations, TextWithTextColorAttrColor(it.name.uppercaseFirst().toText(), com.simple.coreapp.R.attr.colorTrue)))

                binding.tvCaption.setText(it.addressMap.toList().first().first)

                binding.lavCongratulations.setAnimation(R.raw.anim_congratulations)
            }

            binding.tvFinish.isSelected = state.isSuccess()
            binding.tvFinish.isClickable = state.isSuccess()
        }
    }
}


class ConfirmWalletProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/confirm-wallet"
    }

    override fun provideListScope(deepLink: String): List<Class<*>> {

        return listOf(Activity::class.java, ImportWalletFragment::class.java)
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return ConfirmWalletFragment()
    }
}