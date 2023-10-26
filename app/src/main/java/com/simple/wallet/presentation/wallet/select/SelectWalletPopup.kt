package com.simple.wallet.presentation.wallet.select

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.adapter.MultiAdapter
import com.simple.coreapp.ui.base.fragments.BaseViewModelSheetFragment
import com.simple.coreapp.ui.dialogs.OptionFragment.Companion.KEY_REQUEST
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.extentions.doOnHeightNavigationChange
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.setNavigationResult
import com.simple.wallet.DATA
import com.simple.wallet.DP_32
import com.simple.wallet.databinding.PopupListBinding
import com.simple.wallet.presentation.wallet.select.adapters.SelectWalletAdapter
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf

class SelectWalletPopup : BaseViewModelSheetFragment<PopupListBinding, SelectWalletViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        setupRecyclerView()

        observeData()
    }

    override fun getParameter(): ParametersDefinition {

        return { parametersOf(arguments?.getString(WALLET_ID) ?: "", arguments?.getString(IS_SUPPORT_ALL_WALLET)?.toBooleanStrictOrNull() ?: true) }
    }

    private fun setupBottomSheet() {

        val binding = binding ?: return

        val bottomSheet = bottomSheet ?: return

        val behavior = BottomSheetBehavior.from(bottomSheet)

        val bottomSheetParent = bottomSheet.parent as ViewGroup

        bottomSheet.doOnLayout {

            behavior.peekHeight = (bottomSheetParent.parent as View).height
        }

        doOnHeightNavigationChange {

            binding.recyclerView.updatePadding(bottom = it + DP_32)
        }

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val selectWalletAdapter = SelectWalletAdapter { view, item ->

            setNavigationResult(arguments?.getString(KEY_REQUEST) ?: "", bundleOf(DATA to item.data))

            dismiss()
        }

        adapter = MultiAdapter(selectWalletAdapter).apply {

            setRecyclerView(binding.recyclerView)
        }
    }

    private fun observeData() = with(viewModel) {

        walletViewItemList.observe(viewLifecycleOwner) {

            adapter?.submitList(it)
        }
    }

    companion object {

        private const val WALLET_ID = "walletId"

        private const val IS_SUPPORT_ALL_WALLET = "isSupportAllWallet"
    }
}

class SelectWalletProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/select-wallet"
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return SelectWalletPopup().apply {

            Log.d("tuanha", "provideFragment: $params")
            arguments = bundleOf(*params.toList().toTypedArray())
        }
    }
}