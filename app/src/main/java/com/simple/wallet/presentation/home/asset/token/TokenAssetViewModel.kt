package com.simple.wallet.presentation.home.asset.token

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.simple.adapter.LoadingViewItem
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.Event
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.state.ResultState
import com.simple.state.isStart
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.usecases.AssetSyncUseCase
import com.simple.wallet.presentation.CurrencyViewModel
import com.simple.wallet.presentation.home.asset.token.adapters.TokenAssetViewItem
import org.jetbrains.annotations.VisibleForTesting
import java.math.BigDecimal

class TokenAssetViewModel(
    private val assetSyncUseCase: AssetSyncUseCase
) : CurrencyViewModel() {


    private var timePost = 0L

    private val itemLoading = listOf(
        LoadingViewItem(R.layout.item_token_asset_loading),
        LoadingViewItem(R.layout.item_token_asset_loading),
        LoadingViewItem(R.layout.item_token_asset_loading)
    )


    val chain: LiveData<Chain> = MediatorLiveData()

    val wallet: LiveData<Wallet> = MediatorLiveData()


    @VisibleForTesting
    val tokenState: LiveData<ResultState<Triple<Float, Chain, List<Token>>>> = combineSources(chain, wallet) {

        assetSyncUseCase.execute(AssetSyncUseCase.Param(chain.get(), wallet.get())).collect {

            postDifferentValue(it)
        }
    }


    @VisibleForTesting
    val listViewItem: LiveData<List<ViewItemCloneable>> = combineSources(tokenState, currency) {

        val tokenState = tokenState.get()

        if (tokenState.isStart()) {

            postDifferentValue(itemLoading)
            return@combineSources
        }

        if (tokenState !is ResultState.Success) {

            postValue(emptyList())
            return@combineSources
        }


        val list = arrayListOf<ViewItemCloneable>()

        val comparator = Comparator<TokenAssetViewItem> { p0, p1 ->

            if (p0.balanceByUsd < p1.balanceByUsd) {
                1
            } else if (p0.balanceByUsd > p1.balanceByUsd) {
                -1
            } else if (p0.data.tag.level < p1.data.tag.level) {
                1
            } else if (p0.data.tag.level > p1.data.tag.level) {
                -1
            } else if (p0.data.symbol < p1.data.symbol) {
                1
            } else if (p0.data.symbol > p1.data.symbol) {
                -1
            } else {
                0
            }
        }

        val tokenList = tokenState.data.third.map {

            TokenAssetViewItem(it).refresh(currency.get())
        }.let {

            it.sortedWith(comparator)
        }


        list.addAll(tokenList)


        postDifferentValueIfActive(list)
    }


    val listViewItemEvent: LiveData<Event<List<ViewItemCloneable>>> = combineSources(listViewItem) {

        Log.d("tuanha", "listViewItemEvent: ")
        val event = Event(listViewItem.get())
        event.hasBeenHandled = !this.hasActiveObservers()

        kotlinx.coroutines.delay(350 - (System.currentTimeMillis() - timePost))

        postValue(event)

        timePost = System.currentTimeMillis()
    }


    val tokenAssetTotal: LiveData<BigDecimal> = combineSources(listViewItem) {

        val listViewItem = listViewItem.getOrEmpty()

        if (listViewItem.isNotEmpty() && !listViewItem.any { it is TokenAssetViewItem }) {

            return@combineSources
        }

        listViewItem.filterIsInstance<TokenAssetViewItem>().sumOf { it.balanceByUsd }.let {

            Log.d("tuanha", "tokenAssetTotal: $it")
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