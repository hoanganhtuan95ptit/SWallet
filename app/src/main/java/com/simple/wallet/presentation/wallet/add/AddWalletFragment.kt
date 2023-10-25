package com.simple.wallet.presentation.wallet.add

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.adapter.MultiAdapter
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

        setupBottomSheet()
        setupRecyclerView()

        observeData()
    }


    private fun setupBottomSheet() {

        binding ?: return

        val behavior = behavior ?: return

        val bottomSheet = bottomSheet ?: return

        val coordinator = coordinator ?: return


        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        bottomSheet.doOnLayout {

            behavior.peekHeight = coordinator.height
        }

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
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

        binding.recyclerView.itemAnimator = null
    }

    private fun observeData() = with(viewModel) {

        viewItemList.observe(viewLifecycleOwner) {

            adapter?.submitList(it)
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