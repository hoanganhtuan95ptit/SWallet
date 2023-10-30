package com.simple.wallet.presentation.walletconnect

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.simple.coreapp.utils.ext.getSerializableListOrNull
import com.simple.coreapp.utils.ext.getSerializableOrNull
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.navigation.utils.ext.offerEvent
import com.simple.navigation.utils.ext.setNavigationResultListener
import com.simple.state.ResultState
import com.simple.state.isSuccess
import com.simple.state.toFailed
import com.simple.wallet.DATA
import com.simple.wallet.DATA_STATE
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.presentation.ViewObserve
import com.simple.wallet.presentation.message.sign.SignMessageConfirmEvent
import com.simple.wallet.utils.exts.takeIfNotEmpty

class WalletConnectViewObserve : ViewObserve {

    override fun setOwner(activity: AppCompatActivity) = with(activity.getViewModelGlobal(WalletConnectViewModel::class)) {

        val keyRequestWalletConnect = "KEY_REQUEST_WALLET_CONNECT"

        activity.setNavigationResultListener(keyRequestWalletConnect) { _, data ->

            val list = data.getSerializableListOrNull<Request>(DATA)?.takeIfNotEmpty() ?: data.getSerializableOrNull<Request>(DATA)?.let { listOf(it) }?.takeIfNotEmpty() ?: return@setNavigationResultListener

            val state = data.getSerializableOrNull<ResultState<String>>(DATA_STATE)


            list.forEachIndexed { _, request ->

                if (state is ResultState.Success) {

                    approveRequest(request.id, state.data)
                } else {

                    rejectRequest(request.id, state?.toFailed()?.cause?.message ?: "reject")
                }
            }


            val requestFirst = list.firstOrNull() ?: return@setNavigationResultListener

            if (requestFirst.slide == Request.Slide.ANOTHER_APP) {

                activity.finish()
            } else if (state.isSuccess() && requestFirst.method in listOf(Request.Method.SEND_TRANSACTION, Request.Method.SIGN_TRANSACTION)) {

//                offerNavEvent(OpenTransactionProgressEvent(requestFirst.walletAddress ?: return@setNavigationResultListener, hash = resultDataFirst ?: return@setNavigationResultListener))
            }
        }

        requestEvent.observe(activity) { event ->

            val data = event.getContentIfNotHandled() ?: return@observe

            when (data.method) {

                Request.Method.SIGN_AUTH, Request.Method.SIGN_MESSAGE, Request.Method.SIGN_PERSONAL_MESSAGE, Request.Method.SIGN_MESSAGE_TYPED -> {

                    activity.offerEvent(SignMessageConfirmEvent(keyRequestWalletConnect, data))
                }

                Request.Method.SEND_TRANSACTION, Request.Method.SIGN_TRANSACTION -> {

//                    offerNavEvent(SendTransactionEvent(keyRequestWalletConnect, data))
                }

                else -> {

//                    walletConnectViewModel.rejectRequest(data.id)
                }
            }
        }

        requestConnectEvent.observe(activity) { event ->

            event.getContentIfNotHandled() ?: return@observe

            activity.offerDeepLink("/wallet-connect")
//            offerNavEvent(WalletConnectEvent())
        }
    }
}