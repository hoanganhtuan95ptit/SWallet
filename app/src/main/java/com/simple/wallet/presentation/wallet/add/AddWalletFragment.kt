package com.simple.wallet.presentation.wallet.add

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.simple.adapter.MultiAdapter
import com.simple.bottomsheet.CustomBottomSheetDialog
import com.simple.coreapp.ui.adapters.SpaceAdapter
import com.simple.coreapp.ui.base.fragments.BaseViewModelSheetFragment
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.wallet.databinding.PopupListBinding
import com.simple.wallet.presentation.adapters.HeaderAdapter
import com.simple.wallet.presentation.wallet.add.adapters.OptionAdapter

class AddWalletFragment : BaseViewModelSheetFragment<PopupListBinding, AddWalletViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()


    override val viewModel: AddWalletViewModel by lazy {
        getViewModelGlobal(AddWalletViewModel::class)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (dialog as? CustomBottomSheetDialog)?.postponeEnterTransition()

        setupRecyclerView()

        observeData()
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val spaceAdapter = SpaceAdapter()

        val headerAdapter = HeaderAdapter()

        val optionAdapter = OptionAdapter { view, item ->

            offerDeepLink(item.id)
            dismiss()
        }

        adapter = MultiAdapter(
            spaceAdapter,
            headerAdapter,
            optionAdapter,
        ).apply {

            setRecyclerView(binding.recyclerView)
        }
    }

    private fun observeData() = with(viewModel) {

        viewItemList.observe(viewLifecycleOwner) {

            adapter?.submitList(it)

            (dialog as? CustomBottomSheetDialog)?.startPostponedEnterTransition()
        }
    }
}

class AddWalletProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/add-wallet"
    }

    override fun provideScope(deepLink: String): Class<*> {

        return Activity::class.java
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return AddWalletFragment()
    }
}