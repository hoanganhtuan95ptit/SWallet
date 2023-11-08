package com.simple.wallet.presentation.home.asset.token

import android.os.Bundle
import android.view.View
import com.simple.adapter.MultiAdapter
import com.simple.coreapp.ui.base.fragments.BaseViewModelFragment
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.extentions.getViewModel
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.coreapp.utils.extentions.observeLaunch
import com.simple.wallet.databinding.FragmentListBinding
import com.simple.wallet.presentation.home.HomeViewModel
import com.simple.wallet.presentation.home.asset.token.adapters.TokenAssetAdapter

class TokenAssetFragment : BaseViewModelFragment<FragmentListBinding, TokenAssetViewModel>() {

    override val viewModel: TokenAssetViewModel by lazy {

        getViewModelGlobal(TokenAssetViewModel::class)
    }

    private val homeViewModel: HomeViewModel by lazy {

        getViewModel(requireParentFragment(), HomeViewModel::class)
    }


    private var adapter by autoCleared<MultiAdapter>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = binding ?: return

        val tokenAssetAdapter = TokenAssetAdapter { view, item ->

        }

        adapter = MultiAdapter(tokenAssetAdapter).apply {

            binding.recyclerView.setHasFixedSize(true)
            setRecyclerView(binding.recyclerView)
        }

        observeData()
        observeHomeData()
    }

    private fun observeData() = with(viewModel) {

        tokenAssetTotal.observe(viewLifecycleOwner){

            homeViewModel.updateTokenAssetTotal(it)
        }

        listViewItemEvent.observeLaunch(viewLifecycleOwner) { event ->

            val it = event.peekContent()

            adapter?.submitList(it)
        }
    }

    private fun observeHomeData() = with(homeViewModel) {

        chain.observe(viewLifecycleOwner) {

            viewModel.updateChain(it)
        }

        wallet.observe(viewLifecycleOwner) {

            viewModel.updateWallet(it)
        }
    }

    companion object {

        fun newInstance() = TokenAssetFragment()
    }
}