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
import com.simple.coreapp.utils.AppException
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.listenerSources
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.coreapp.utils.extentions.postValue
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.text.TextImage
import com.simple.coreapp.utils.extentions.text.TextRes
import com.simple.coreapp.utils.extentions.text.TextSpan
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.withTextColor
import com.simple.state.ResultState
import com.simple.state.doSuccess
import com.simple.state.isFailed
import com.simple.state.isStart
import com.simple.state.toSuccess
import com.simple.wallet.DP_20
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Message
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.entities.extra.ApproveExtra
import com.simple.wallet.domain.entities.extra.SignPersonalExtra
import com.simple.wallet.domain.usecases.DetectRequestAsyncUseCase
import com.simple.wallet.domain.usecases.chain.GetChainByUseCase
import com.simple.wallet.domain.usecases.message.SignMessageUseCase
import com.simple.wallet.domain.usecases.wallet.GetWalletByUseCase
import com.simple.wallet.presentation.adapters.BottomViewItem
import com.simple.wallet.presentation.adapters.KeyValueViewItemV3
import com.simple.wallet.presentation.adapters.MessageViewItem
import com.simple.wallet.presentation.adapters.TextCaptionViewItem
import com.simple.wallet.presentation.adapters.TokenApproveViewItem
import com.simple.wallet.utils.exts.shortenValue
import com.simple.wallet.utils.exts.takeIfNotEmpty
import com.simple.wallet.utils.exts.toTransactionHeaderViewItem
import com.simple.wallet.utils.exts.toViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigInteger

class SignMessageConfirmViewModel(
    val mRequest: Request,

    private val getChainByUseCase: GetChainByUseCase,
    private val getWalletByUseCase: GetWalletByUseCase,

    private val signMessageUseCase: SignMessageUseCase,
    private val detectMessageAsyncUseCase: DetectRequestAsyncUseCase,
) : BaseViewModel() {


    @VisibleForTesting
    val currentChain: LiveData<Chain> = liveData {

        getChainByUseCase.execute(GetChainByUseCase.Param(mRequest.message!!.chainId)).firstOrNull()?.let {

            postValue(it)
        }
    }

    @VisibleForTesting
    var currentWallet: LiveData<Wallet> = liveData {

        getWalletByUseCase.execute(GetWalletByUseCase.Param(mRequest.walletAddress!!)).firstOrNull()?.let {

            postValue(it)
        }
    }


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

        list.add(requestDetect.get().toTransactionHeaderViewItem())

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val messageInfoViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(requestDetect) {

        val message = requestDetect.get().message ?: return@combineSources

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(message.getMessageInfo())

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val messageViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(isConfirm, requestDetectState) {

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
    val viewItemList: LiveData<List<ViewItemCloneable>> = combineSources(headerViewItemList, messageInfoViewItemList, messageViewItemList, bottomViewItemList) {

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(headerViewItemList.getOrEmpty())

        messageInfoViewItemList.getOrEmpty().takeIfNotEmpty()?.let {

            list.add(SpaceViewItem(height = DP_20))
            list.addAll(it)
        }

        messageViewItemList.getOrEmpty().takeIfNotEmpty()?.let {

            list.add(SpaceViewItem(height = DP_20))
            list.addAll(it)
        }

        bottomViewItemList.getOrEmpty().takeIfNotEmpty()?.let {

            list.add(SpaceViewItem(height = DP_20))
            list.addAll(it)
        }

        postDifferentValueIfActive(list)
    }

    internal val viewItemListDisplay: LiveData<List<ViewItemCloneable>> = combineSources(viewItemList) {

        postDifferentValueIfActive(viewItemList.getOrEmpty())
    }


    internal val signMessageState: LiveData<ResultState<String>> = MediatorLiveData()


    internal val buttonState: LiveData<Enum<*>> = listenerSources(currentWallet, requestDetectState) {

        (if ( requestDetectState.value.isStart()) {

            ButtonState.DETECT_LOADING
        } else if (requestDetectState.value.isFailed()) {

            ButtonState.DETECT_FAILED
        } else if (currentWallet.value?.isWatch == true) {

            ButtonState.WATCH_WALLET
        } else if (signMessageState.value.isStart()) {

            ButtonState.APPROVAL_LOADING
        } else {

            ButtonState.REVIEW
        }).let {

            postDifferentValue(it)
        }
    }


    fun updateConfirm() {

        isConfirm.postValue(true)
    }

    fun signMessage() = viewModelScope.launch(handler + Dispatchers.IO) {

        val messageViewItemList = messageViewItemList.getOrEmpty().filterIsInstance<MessageViewItem>()

        if (messageViewItemList.isNotEmpty() && messageViewItemList.any { it.needConfirm } && isConfirm.value == false) {

            signMessageState.postValue(ResultState.Failed(AppException(code = TransactionCode.PLEASE_CONFIRM)))

            return@launch
        }


        kotlin.runCatching {

            signMessageState.postDifferentValue(ResultState.Start)

            signMessageState.postDifferentValue(signMessageUseCase.execute(SignMessageUseCase.Param(mRequest)))
        }.getOrElse {

            signMessageState.postDifferentValue(ResultState.Failed(it))
        }
    }

    private fun Message.getMessageInfo(): List<ViewItemCloneable> {

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

            list.add(TextCaptionViewItem(id = "", text = extra.decode.toText().withTextColor(com.simple.coreapp.R.attr.colorOnBackgroundVariant)))
        } else {

            list.addAll(this.message.toTree().get("message").toJson().toObject<MutableMap<*, *>>().toViewItem(0))
        }

        return list
    }

    private fun Request?.toMessageViewItem(isConfirm: Boolean): List<ViewItemCloneable> {


        if (this == null) {

            return emptyList()
        }


        val list = arrayListOf<Text>()

        if (power?.status in listOf(Request.Power.Status.RISK)) {

            list.add(TextRes(R.string.message_warning_url_risk, TextImage(R.drawable.ic_check_box_normal_accent_24dp, 16.toPx())))
        }

        if (list.isNotEmpty()) MessageViewItem(id = "KEY").apply {

            list.add(0, TextSpan(R.string.message_warning.toText(), StyleSpan(Typeface.BOLD)))

            message = list.toText("\n").withTextColor(com.google.android.material.R.attr.colorError)

            messageIcon = if (isConfirm) {
                R.drawable.ic_check_box_select_accent_24dp.toImage()
            } else {
                R.drawable.ic_check_box_normal_accent_24dp.toImage()
            }

            background = R.drawable.bg_corners_16dp_solid_error_10

            needConfirm = true
        }.let {

            return listOf(it)
        } else {

            return emptyList()
        }
    }

    internal enum class ButtonState {

        REVIEW, WATCH_WALLET, DETECT_LOADING, DETECT_FAILED, APPROVAL_LOADING
    }

    internal enum class TransactionCode {

        PLEASE_CONFIRM
    }
}