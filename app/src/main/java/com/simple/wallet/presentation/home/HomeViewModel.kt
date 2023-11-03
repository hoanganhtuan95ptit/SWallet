package com.simple.wallet.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.wallet.domain.entities.Category
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.presentation.home.adapters.CategoryViewItem

class HomeViewModel : BaseViewModel() {

    val wallet: LiveData<Wallet> = liveData {

        postDifferentValueIfActive(Wallet.ALL)
    }

    val chain: LiveData<Chain> = liveData {

        postDifferentValueIfActive(Chain.ALL)
    }

    val categoryList: LiveData<List<Category>> = MediatorLiveData<List<Category>>().apply {

        val list = arrayListOf<Category>()

        list.add(Category(id = Category.Id.TRANSFER.value, deeplink = "https://wallet.krystal.app/send"))
        list.add(Category(id = Category.Id.D_APP.value, deeplink = "https://www.google.com/"))
        list.add(Category(id = Category.Id.SWAP.value, deeplink = "https://app.uniswap.org/swap"))
        list.add(Category(id = Category.Id.CROSS_SWAP.value, deeplink = "https://wallet.krystal.app/cross-chain-swap"))

        postDifferentValue(list)
    }

    val categoryViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(categoryList) {

        categoryList.getOrEmpty().map {

            CategoryViewItem(it).refresh()
        }.let {

            postDifferentValueIfActive(it)
        }
    }

    fun updateChain(data: Chain) {

        chain.postDifferentValue(data) { old, new ->
            old?.id == new.id
        }
    }

    fun updateWallet(data: Wallet) {

        wallet.postDifferentValue(data) { old, new ->
            old?.id == new.id
        }
    }
}