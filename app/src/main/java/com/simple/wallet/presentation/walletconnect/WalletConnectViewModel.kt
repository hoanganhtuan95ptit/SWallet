package com.simple.wallet.presentation.walletconnect

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.extentions.Event
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.toEvent
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.usecases.walletconnect.ApproveRequestUseCase
import com.simple.wallet.domain.usecases.walletconnect.GetConnectAsyncUseCase
import com.simple.wallet.domain.usecases.walletconnect.GetRequestAsyncUseCase
import com.simple.wallet.domain.usecases.walletconnect.RejectRequestUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WalletConnectViewModel(
    private val rejectRequestUseCase: RejectRequestUseCase,
    private val approveRequestUseCase: ApproveRequestUseCase,

    private val getRequestAsyncUseCase: GetRequestAsyncUseCase,
    private val getConnectAsyncUseCase: GetConnectAsyncUseCase
) : BaseViewModel() {

    @VisibleForTesting
    val request: LiveData<Request> = liveData {

        getRequestAsyncUseCase.execute().collect {

            postValue(it)
        }
    }

    val requestEvent: LiveData<Event<Request>> = request.toEvent()


    @VisibleForTesting
    val requestConnect: LiveData<Request> = liveData {

        getConnectAsyncUseCase.execute().collect {

            postValue(it)
        }
    }

    val requestConnectEvent: LiveData<Event<Request>> = requestConnect.toEvent()


    fun rejectRequest(requestId: Long, message: String = "") = GlobalScope.launch(handler + Dispatchers.IO) {

        rejectRequestUseCase.execute(RejectRequestUseCase.Param(requestId, message))
    }

    fun approveRequest(requestId: Long, result: String = "") = GlobalScope.launch(handler + Dispatchers.IO) {

        approveRequestUseCase.execute(ApproveRequestUseCase.Param(requestId, result)).toEvent()
    }
}