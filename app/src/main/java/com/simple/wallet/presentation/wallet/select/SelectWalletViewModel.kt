package com.simple.wallet.presentation.wallet.select

import androidx.lifecycle.LiveData
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.usecases.wallet.GetAllWalletUseCase
import com.simple.wallet.presentation.wallet.select.adapters.SelectWalletViewItem

class SelectWalletViewModel(
    private val walletId: String,
    private val isSupportAllWallet: Boolean,

    private val getAllWalletUseCase: GetAllWalletUseCase
) : BaseViewModel() {

    val walletList: LiveData<List<Wallet>> = liveData {

        postDifferentValueIfActive(getAllWalletUseCase.execute(GetAllWalletUseCase.Param(isSupportAllWallet)))
    }

    val walletViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(walletList) {

        walletList.getOrEmpty().map {

            SelectWalletViewItem(it).refresh(walletId)
        }.let {

            postDifferentValueIfActive(it)
        }
    }
}