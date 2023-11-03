package com.simple.wallet.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.postValue
import com.simple.state.ResultState
import com.simple.state.doSuccess
import com.simple.wallet.DP_16
import com.simple.wallet.domain.entities.Url
import com.simple.wallet.domain.usecases.url.QueryUseCase
import com.simple.wallet.presentation.browser.adapters.UrlViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

class SearchViewModel(
    private val queryUseCase: QueryUseCase
) : BaseViewModel() {


    private var job: Job? = null


    @VisibleForTesting
    val urlState: LiveData<ResultState<List<Url>>> = MediatorLiveData()

    @VisibleForTesting
    val urlList: LiveData<List<Url>> = combineSources(urlState) {

        val urlState = urlState.get()

        urlState.doSuccess {

            postValue(it)
        }
    }

    val urlViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(urlList) {

        urlList.getOrEmpty().map {

            UrlViewItem(it, paddingLeft = DP_16, paddingRight = DP_16).refresh()
        }.let {

            postValue(it)
        }
    }


    fun query(query: String) = viewModelScope.launch(handler + Dispatchers.IO) {

        val list = queryUseCase.execute(QueryUseCase.Param(query))

        if (isActive) urlState.postValue(ResultState.Success(list))
    }.apply {

        job?.cancel()

        job = this
    }
}