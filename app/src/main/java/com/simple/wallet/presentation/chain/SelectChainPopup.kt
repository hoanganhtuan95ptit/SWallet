package com.simple.wallet.presentation.chain

import android.os.Bundle
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
import com.simple.wallet.presentation.chain.adapters.SelectChainAdapter
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf

class SelectChainPopup : BaseViewModelSheetFragment<PopupListBinding, SelectChainViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        setupRecyclerView()

        observeData()
    }

    override fun getParameter(): ParametersDefinition {

        return { parametersOf(arguments?.getString(CHAIN_ID)?.toLongOrNull() ?: 0L, arguments?.getString(IS_SUPPORT_ALL_CHAIN)?.toBooleanStrictOrNull() ?: true) }
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

        val selectChainAdapter = SelectChainAdapter { view, item ->

            setNavigationResult(arguments?.getString(KEY_REQUEST) ?: "", bundleOf(DATA to item.data))

            dismiss()
        }

        adapter = MultiAdapter(selectChainAdapter).apply {

            setRecyclerView(binding.recyclerView)
        }
    }

    private fun observeData() = with(viewModel) {

        chainViewItemList.observe(viewLifecycleOwner) {

            adapter?.submitList(it)
        }
    }

    companion object {

        private const val CHAIN_ID = "chainId"

        private const val IS_SUPPORT_ALL_CHAIN = "isSupportAllChain"
    }
}

class SelectChainProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/select-chain"
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return SelectChainPopup().apply {

            arguments = bundleOf(*params.toList().toTypedArray())
        }
    }
}