package com.simple.wallet.presentation.message.sign

import android.graphics.Typeface
import android.text.style.StyleSpan
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.simple.adapter.ViewItemCloneable
import com.simple.core.utils.extentions.asObjectOrNull
import com.simple.core.utils.extentions.toJson
import com.simple.core.utils.extentions.toObject
import com.simple.core.utils.extentions.toTree
import com.simple.coreapp.ui.adapters.SpaceViewItem
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.listenerSources
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.coreapp.utils.extentions.postValue
import com.simple.coreapp.utils.extentions.text.TextSpan
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.state.ResultState
import com.simple.state.doSuccess
import com.simple.state.toSuccess
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Message
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.entities.extra.ApproveExtra
import com.simple.wallet.domain.entities.extra.SignPersonalExtra
import com.simple.wallet.domain.usecases.DetectRequestAsyncUseCase
import com.simple.wallet.domain.usecases.SignMessageUseCase
import com.simple.wallet.presentation.adapters.BottomViewItem
import com.simple.wallet.presentation.adapters.KeyValueViewItemV3
import com.simple.wallet.presentation.adapters.TokenApproveViewItem
import com.simple.wallet.utils.exts.shortenValue
import com.simple.wallet.utils.exts.toHeaderViewItem
import com.simple.wallet.utils.exts.toMessageViewItem
import com.simple.wallet.utils.exts.toViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger

class SignMessageConfirmViewModel(
    val mRequest: Request,

    private val signMessageUseCase: SignMessageUseCase,
    private val detectMessageAsyncUseCase: DetectRequestAsyncUseCase,
) : BaseViewModel() {

    val nativeToken: LiveData<Token> = MediatorLiveData()

    val currentChain: LiveData<Chain> = MediatorLiveData()

    var currentWallet: LiveData<Wallet> = MediatorLiveData()


    @VisibleForTesting
    val isConfirm: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {

        value = false
    }

    @VisibleForTesting
    val request: LiveData<Request> = liveData {

        postValue(mRequest)
    }

    @VisibleForTesting
    val requestDetectState: LiveData<ResultState<Request>> = combineSources(request) {

        if (value == null) {

            postValue(ResultState.Start)
        }

        detectMessageAsyncUseCase.execute(DetectRequestAsyncUseCase.Param(request.get())).collect {

            postValue(ResultState.Success(it))
        }
    }

    @VisibleForTesting
    val requestDetect: LiveData<Request> = combineSources(requestDetectState) {

        requestDetectState.get().doSuccess {

            postValue(it)
        }
    }


    @VisibleForTesting
    val headerViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(requestDetect) {

        val list = arrayListOf<ViewItemCloneable>()

        list.add(requestDetect.get().toHeaderViewItem())

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val messageInfoViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(nativeToken, requestDetect) {

        val message = requestDetect.get().message ?: return@combineSources

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(message.getInfo())

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val transactionMessageViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(nativeToken, isConfirm, requestDetectState) {

        val request = requestDetectState.get().toSuccess()?.data

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(request.toMessageViewItem(isConfirm.get()))

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val bottomViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(currentChain, currentWallet) {

        val currentChain = currentChain.get()

        val currentWallet = currentWallet.get()

        val list = arrayListOf<ViewItemCloneable>()

        list.add(BottomViewItem(currentChain, currentWallet).refresh())

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val viewItemList: LiveData<List<ViewItemCloneable>> = combineSources(headerViewItemList, messageInfoViewItemList, transactionMessageViewItemList, bottomViewItemList) {

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(headerViewItemList.getOrEmpty())

        list.addAll(messageInfoViewItemList.getOrEmpty())

        transactionMessageViewItemList.getOrEmpty().takeIf { it.isNotEmpty() }?.let {

            list.add(SpaceViewItem(height = 20.toPx()))
            list.addAll(it)
        }

        bottomViewItemList.getOrEmpty().takeIf { it.isNotEmpty() }?.let {

            list.add(SpaceViewItem(height = 20.toPx()))
            list.addAll(it)
        }

        postDifferentValueIfActive(list)
    }

    internal val viewItemListDisplay: LiveData<List<ViewItemCloneable>> = combineSources(viewItemList) {

        postDifferentValueIfActive(viewItemList.getOrEmpty())
    }


    internal val signMessageState: LiveData<ResultState<String>> = MediatorLiveData()


    internal val buttonState: LiveData<ResultState<ButtonState>> = listenerSources(currentWallet) {

//        if (currentWallet.get().isWatch) {
//
//            postValue(ResultState.Success(ButtonState.WATCH_WALLET))
//        } else {
//
//            postValue(ResultState.Success(ButtonState.REVIEW_TRANSACTION))
//        }
    }


    fun updateConfirm() {

        isConfirm.postValue(true)
    }

    fun updateNativeToken(token: Token): Boolean {

        if (this.nativeToken.value?.address.equals(token.address, true) && this.nativeToken.value?.symbol.equals(token.symbol, true) && this.nativeToken.value?.chainId == token.chainId) {
            return false
        }

        return this.nativeToken.postDifferentValue(token) { old, new ->
            old?.address.equals(new.address, true) && old?.symbol.equals(new.symbol, true) && old?.chainId == new.chainId
        }
    }

    fun updateCurrentChain(chain: Chain): Boolean {

        if (this.currentChain.value?.id == chain.id) {
            return false
        }

        return this.currentChain.postDifferentValue(chain) { old, new ->
            old?.id == new.id
        }
    }

    fun updateCurrentWallet(wallet: Wallet): Boolean {

//        if (this.currentWallet.value?.address.equals(wallet.address, true)) {
//            return false
//        }
//
//        return this.currentWallet.postDifferentValue(wallet) { old, new ->
//            old?.address.equals(new.address, true)
//        }
        return false
    }

    fun signMessage() = viewModelScope.launch(handler + Dispatchers.IO) {

//        val messageViewItemList = transactionMessageViewItemList.getOrEmpty().filterIsInstance<MessageInfoViewItem>()
//
//        if (messageViewItemList.isNotEmpty() && messageViewItemList.any { it.showConfirm } && isConfirm.value == false) {
//
//            signMessageState.postValue(ResultState.Failed("", AppExceptionV2(code = TransactionCode.PLEASE_CONFIRM)))
//
//            return@launch
//        }
//
//
//        kotlin.runCatching {
//
//            signMessageState.postDifferentValue(ResultState.Start)
//
//            signMessageState.postDifferentValue(signMessageUseCase.execute(SignMessageUseCase.Param(mRequest)))
//        }.getOrElse {
//
//            signMessageState.postDifferentValue(ResultState.Failed(it.message ?: "error", it))
//        }
    }

    private fun Message.getInfo(): List<ViewItemCloneable> {

        val list = arrayListOf<ViewItemCloneable>()

        if (type == Message.Type.SIGN_PERMIT) {

            val extra = this.extra.asObjectOrNull<ApproveExtra>()!!

            val keyReceiver = R.string.title_sender.toText()

            val valueReceiver = extra.senderAddress.shortenValue().toText().let {

                TextSpan(it, StyleSpan(Typeface.BOLD))
            }

            list.add(KeyValueViewItemV3("SENDER", key = keyReceiver, value = valueReceiver).refresh())

            if (extra.amountApprove > BigInteger.ZERO) list.add(TokenApproveViewItem(extra).refresh())
        } else if (type == Message.Type.SIGN_PERSONAL_MESSAGE) {

            val extra = this.extra.asObjectOrNull<SignPersonalExtra>()!!

            list.add(KeyValueViewItemV3("", emptyText(), extra.decode.toText(), paddingLeft = (-16).toPx()).refresh())
        } else {

            list.addAll(this.message.toTree().get("message").toJson().toObject<MutableMap<*, *>>().toViewItem(0))
        }

        return list
    }

    internal enum class ButtonState {

        REVIEW_TRANSACTION, WATCH_WALLET, UNKNOWN
    }

    internal enum class TransactionCode {

        PLEASE_CONFIRM
    }
}