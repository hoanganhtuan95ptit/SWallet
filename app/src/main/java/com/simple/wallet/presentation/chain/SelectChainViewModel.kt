package com.simple.wallet.presentation.chain

import androidx.lifecycle.LiveData
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.usecases.GetAllChainUseCase
import com.simple.wallet.presentation.chain.adapters.SelectChainViewItem

class SelectChainViewModel(
    private val chainId: Long,
    private val isSupportAllChain: Boolean,

    private val getAllChainUseCase: GetAllChainUseCase
) : BaseViewModel() {

    val chainList: LiveData<List<Chain>> = liveData {

        postDifferentValueIfActive(getAllChainUseCase.execute(GetAllChainUseCase.Param(isSupportAllChain)))
    }

    val chainViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(chainList) {

        chainList.getOrEmpty().map {

            SelectChainViewItem(it).refresh(chainId)
        }.let {

            postDifferentValueIfActive(it)
        }
    }
}