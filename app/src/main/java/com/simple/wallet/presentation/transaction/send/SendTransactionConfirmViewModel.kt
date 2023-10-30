//package com.simple.wallet.presentation.transaction.send
//
//import android.graphics.Typeface
//import android.text.style.StyleSpan
//import android.util.Range
//import androidx.annotation.VisibleForTesting
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MediatorLiveData
//import androidx.lifecycle.viewModelScope
//import com.simple.adapter.ViewItemCloneable
//import com.simple.core.utils.extentions.asObjectOrNull
//import com.simple.coreapp.ui.adapters.SpaceViewItem
//import com.simple.coreapp.utils.extentions.combineSources
//import com.simple.coreapp.utils.extentions.get
//import com.simple.coreapp.utils.extentions.getOrEmpty
//import com.simple.coreapp.utils.extentions.getOrNull
//import com.simple.coreapp.utils.extentions.listenerSources
//import com.simple.coreapp.utils.extentions.liveData
//import com.simple.coreapp.utils.extentions.postDifferentValue
//import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
//import com.simple.coreapp.utils.extentions.postValue
//import com.simple.coreapp.utils.extentions.text.TextSpan
//import com.simple.coreapp.utils.extentions.toPx
//import com.simple.coreapp.utils.extentions.toText
//import com.simple.state.ResultState
//import com.simple.state.doSuccess
//import com.simple.state.isStart
//import com.simple.state.toSuccess
//import com.simple.wallet.R
//import com.simple.wallet.domain.entities.Request
//import com.simple.wallet.domain.entities.Transaction
//import com.simple.wallet.domain.entities.extra.ApproveExtra
//import com.simple.wallet.domain.usecases.DetectRequestAsyncUseCase
//import com.simple.wallet.domain.usecases.transaction.SendTransactionUseCase
//import com.simple.wallet.presentation.adapters.HeaderViewItem
//import com.simple.wallet.presentation.adapters.KeyValueViewItemV3
//import com.simple.wallet.presentation.adapters.TokenApproveViewItem
//import com.simple.wallet.utils.exts.FormatNumberType
//import com.simple.wallet.utils.exts.divideToPowerTen
//import com.simple.wallet.utils.exts.shortenValue
//import com.simple.wallet.utils.exts.toBigDecimalOrDefaultZero
//import com.simple.wallet.utils.exts.toDisplay
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.web3j.crypto.transaction.type.TransactionType
//import java.math.BigInteger
//
//class SendTransactionConfirmViewModel(
//    val mRequest: Request,
//
//    private val sendTransactionUseCase: SendTransactionUseCase,
//    private val detectRequestAsyncUseCase: DetectRequestAsyncUseCase,
//) : TransactionViewModel() {
//
//    @VisibleForTesting
//    val isConfirm: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
//
//        value = false
//    }
//
//    @VisibleForTesting
//    val request: LiveData<Request> = liveData {
//
//        postValue(mRequest)
//    }
//
//    @VisibleForTesting
//    val requestDetectState: LiveData<ResultState<Request>> = combineSources(request) {
//
//        if (value == null) {
//
//            postValue(ResultState.Start)
//        }
//
//        detectRequestAsyncUseCase.execute(DetectRequestAsyncUseCase.Param(request.get())).collect {
//
//            postValue(ResultState.Success(it))
//        }
//    }
//
//    @VisibleForTesting
//    val requestDetect: LiveData<Request> = combineSources(requestDetectState) {
//
//        requestDetectState.get().doSuccess {
//
//            postValue(it)
//        }
//    }
//
//    val transaction: LiveData<Transaction> = combineSources(requestDetectState) {
//
//        requestDetectState.get().doSuccess {
//
//            postValue(it.transaction)
//        }
//    }
//
//
//    @VisibleForTesting
//    val headerViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(requestDetect) {
//
//        val list = arrayListOf<ViewItemCloneable>()
//
//        list.add(HeaderViewItem(requestDetect.get()).refresh())
//
//        postDifferentValueIfActive(list)
//    }
//
//    @VisibleForTesting
//    val transactionInfoViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(nativeToken, requestDetect) {
//
//        val nativeToken = nativeToken.get()
//
//        val transaction = requestDetect.get().transaction ?: return@combineSources
//
//        val list = arrayListOf<ViewItemCloneable>()
//
//        list.addAll(transaction.getTransactionInfo(nativeToken))
//
//        postDifferentValueIfActive(list)
//    }
//
//    @VisibleForTesting
//    val transactionFeeViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(gas, gasLimit, bonusFee, nativeToken, currentChain, currencyInfo) {
//
//        val gas = gas.get()
//
//        val gasLimit = gasLimit.get()
//
//        val nativeToken = nativeToken.get()
//
//        val currencyInfo = currencyInfo.get()
//
//        val currentChain = currentChain.get()
//
//        val transactionFee = bonusFee.get()
//
//
//        val list = arrayListOf<ViewItemCloneable>()
//
////        list.add(FeeTransactionInfoViewItem().refresh(gas, gasLimit, nativeToken, currencyInfo, currentChain, transactionFee))
//
//        postDifferentValueIfActive(list)
//    }
//
//    @VisibleForTesting
//    val transactionMessageViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(gas, gasLimit, bonusFee, nativeToken, isConfirm, requestDetectState) {
//
//        val sessionMessageDetectState = requestDetectState.get()
//
//        val sessionMessage = sessionMessageDetectState.toSuccess()?.data
//
//
//        val gasPrice = gas.get().gasPrice.toBigDecimalOrDefaultZero()
//
//        val gasLimit = gasLimitState.get().toSuccess()?.data?.let { Pair(gasLimit.get().toBigDecimal(), Range(it.second.lower.toBigDecimal(), it.second.upper.toBigDecimal())) }
//
//        val bonusFee = bonusFee.get()
//
//        val nativeToken = nativeToken.get()
//
//
//        val list = arrayListOf<ViewItemCloneable>()
//
////        MessageInfoViewItem("").refresh(isConfirm.get(), sessionMessage, gasPrice, gasLimit, nativeToken, bonusFee)?.let { viewItem ->
////
////            list.add(viewItem)
////        }
//
//        postDifferentValueIfActive(list)
//    }
//
//    @VisibleForTesting
//    val bottomViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(currentChain, currentWallet) {
//
//        val currentChain = currentChain.get()
//
//        val currentWallet = currentWallet.get()
//
//        val list = arrayListOf<ViewItemCloneable>()
//
////        list.add(BottomTransactionInfoViewItem(currentChain, currentWallet).refresh())
//
//        postDifferentValueIfActive(list)
//    }
//
//    @VisibleForTesting
//    val viewItemList: LiveData<List<ViewItemCloneable>> = combineSources(headerViewItemList, transactionInfoViewItemList, transactionFeeViewItemList, transactionMessageViewItemList, bottomViewItemList) {
//
//        val list = arrayListOf<ViewItemCloneable>()
//
//        list.addAll(headerViewItemList.getOrEmpty())
//
//        list.addAll(transactionInfoViewItemList.getOrEmpty())
//
//        transactionFeeViewItemList.getOrEmpty().takeIf { it.isNotEmpty() }?.let {
//
//            list.add(SpaceViewItem(height = 20.toPx()))
//            list.addAll(it)
//        }
//
//        transactionMessageViewItemList.getOrEmpty().takeIf { it.isNotEmpty() }?.let {
//
//            list.add(SpaceViewItem(height = 20.toPx()))
//            list.addAll(it)
//        }
//
//        bottomViewItemList.getOrEmpty().takeIf { it.isNotEmpty() }?.let {
//
//            list.add(SpaceViewItem(height = 20.toPx()))
//            list.addAll(it)
//        }
//
//        postDifferentValueIfActive(list)
//    }.apply {
//
//
//    }
//
//    internal val viewItemListDisplay: LiveData<List<ViewItemCloneable>> = combineSources(viewItemList) {
//
//        postDifferentValueIfActive(viewItemList.getOrEmpty())
//    }
//
//
//    internal val sendTransactionState: LiveData<ResultState<String>> = MediatorLiveData()
//
//
//    internal val buttonState: LiveData<ResultState<ButtonState>> = listenerSources(currentWallet, gasLimitState) {
//
//        val state = listOf(gasLimitState.getOrNull())
//
//        if (state.any { it?.isStart() == true }) {
//
//            postValue(ResultState.Start)
//        } else if (currentWallet.get().isWatch) {
//
//            postValue(ResultState.Success(ButtonState.WATCH_WALLET))
//        } else {
//
//            postValue(ResultState.Success(ButtonState.REVIEW_TRANSACTION))
//        }
//    }
//
//
//    fun updateConfirm() {
//
//        isConfirm.postValue(true)
//    }
//
//    fun sendTransaction() = viewModelScope.launch(handler + Dispatchers.IO) {
//
//        val messageViewItemList = transactionMessageViewItemList.getOrEmpty().filterIsInstance<MessageInfoViewItem>()
//
//        if (messageViewItemList.isNotEmpty() && messageViewItemList.any { it.showConfirm } && isConfirm.value == false) {
//
//            sendTransactionState.postValue(ResultState.Failed("", AppExceptionV2(code = TransactionCode.PLEASE_CONFIRM)))
//
//            return@launch
//        }
//
//
//        kotlin.runCatching {
//
//            sendTransactionState.postDifferentValue(ResultState.Start)
//
//            sendTransactionState.postDifferentValue(sendTransactionSingle())
//        }.getOrElse {
//
//            sendTransactionState.postDifferentValue(ResultState.Failed(it.message ?: "error", it))
//        }
//    }
//
//    private suspend fun sendTransactionSingle(): ResultState<String> {
//
//        val gas = gas.get()
//
//        val currentChain = currentChain.get()
//
//        val transaction = transaction.get()
//
//        val value = transaction.value
//
//        val gasLimit = gasLimit.get()
//
//        val gasPrice = gas.gasPrice
//
//        val priorityFee = gas.priorityFee
//
//
//        return SendTransactionUseCase.Param(
//
//            to = transaction.to,
//            from = currentWallet.get().address,
//
//            data = transaction.data,
//
//            value = value,
//
//            nonce = customNonce.value?.toInt() ?: -1,
//            gasLimit = gasLimit,
//            gasPrice = gasPrice.toBigDecimalOrDefaultZero(),
//            priorityFee = priorityFee.toBigDecimalOrDefaultZero(),
//
//            isFromDApp = true,
//
//            chainId = currentChain.id,
//            rpcUrls = currentChain.rpcList,
//        ).let {
//
//            sendTransactionUseCase.execute(it)
//        }
//    }
//
//    private fun Transaction.getTransactionInfo(nativeToken: Token): List<ViewItemCloneable> {
//
//        val list = arrayListOf<ViewItemCloneable>()
//
//        if (type == TransactionType.SEND) {
//
//            val extra = this.extra.asObjectOrNull<TransferTransactionExtra>()!!
//
//            val keyReceiver = R.string.title_receiver.toText()
//
//            val valueReceiver = extra.receiverAddress.shortenValue().toText().let {
//
//                TextSpan(it, StyleSpan(Typeface.BOLD))
//            }
//
//            list.add(KeyValueViewItemV3("RECEIVER", key = keyReceiver, value = valueReceiver).refresh())
//
//
//            val keyValue = R.string.title_amount_transfer.toText()
//
//            val valueValue = listOf(
//                ("-" + extra.amountTransfer.toBigDecimal().divideToPowerTen(extra.tokenTransfer.decimals).toDisplay(FormatNumberType.BALANCE)).toText(),
//                extra.tokenTransfer.symbol.uppercase().toText()
//            ).toText(" ").let {
//
//                TextSpan(it, StyleSpan(Typeface.BOLD))
//            }
//
//            list.add(KeyValueViewItemV3("VALUE", key = keyValue, value = valueValue).refresh())
//        } else if (type == TransactionType.APPROVAL) {
//
//            val extra = this.extra.asObjectOrNull<ApproveExtra>()!!
//
//            val keyReceiver = R.string.title_sender.toText()
//
//            val valueReceiver = extra.senderAddress.shortenValue().toText().let {
//
//                TextSpan(it, StyleSpan(Typeface.BOLD))
//            }
//
//            list.add(KeyValueViewItemV3("SENDER", key = keyReceiver, value = valueReceiver).refresh())
//
//
//            if (extra.amountApprove > BigInteger.ZERO) list.add(TokenApproveViewItem(extra).refresh())
//        } else {
//
//            val keySender = R.string.title_sender.toText()
//
//            val valueSender = to.shortenValue().toText().let {
//
//                TextSpan(it, StyleSpan(Typeface.BOLD))
//            }
//
//            list.add(KeyValueViewItemV3("SENDER", key = keySender, value = valueSender).refresh())
//
//
//            val keyValue = R.string.title_value.toText()
//
//            val valueValue = listOf(
//                ("-" + value.toBigDecimal().divideToPowerTen(nativeToken.decimals).toDisplay(FormatNumberType.BALANCE)).toText(),
//                nativeToken.symbol.uppercase().toText()
//            ).toText(" ").let {
//
//                TextSpan(it, StyleSpan(Typeface.BOLD))
//            }
//
//            list.add(KeyValueViewItemV3("VALUE", key = keyValue, value = valueValue).refresh())
//
//
//            val keyData = R.string.title_data.toText()
//
//            val valueData = data.toText().let {
//
//                TextSpan(it, StyleSpan(Typeface.BOLD))
//            }
//
//            list.add(KeyValueViewItemV3("DATA", key = keyData, value = valueData).refresh())
//        }
//
//        return list
//    }
//}
//
//internal enum class ButtonState {
//
//    REVIEW_TRANSACTION, WATCH_WALLET, UNKNOWN
//}
//
//internal enum class TransactionCode {
//
//    PLEASE_CONFIRM
//}