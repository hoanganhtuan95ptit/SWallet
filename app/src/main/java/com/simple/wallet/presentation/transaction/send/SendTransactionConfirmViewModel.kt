package com.simple.wallet.presentation.transaction.send

import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Range
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.one.web3.utils.fromWei
import com.one.web3.utils.toWei
import com.simple.adapter.ViewItemCloneable
import com.simple.core.utils.extentions.asObjectOrNull
import com.simple.coreapp.ui.adapters.SpaceViewItem
import com.simple.coreapp.utils.AppException
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.listenerSources
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.orListEmpty
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
import com.simple.coreapp.utils.extentions.withStyle
import com.simple.state.ResultState
import com.simple.state.doSuccess
import com.simple.state.isFailed
import com.simple.state.isStart
import com.simple.state.toSuccess
import com.simple.wallet.DP_20
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Gas
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Transaction
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.entities.extra.ApproveExtra
import com.simple.wallet.domain.entities.extra.TransferExtra
import com.simple.wallet.domain.usecases.DetectRequestAsyncUseCase
import com.simple.wallet.domain.usecases.chain.GetChainByUseCase
import com.simple.wallet.domain.usecases.token.GetTokenByUseCase
import com.simple.wallet.domain.usecases.transaction.SendTransactionUseCase
import com.simple.wallet.domain.usecases.wallet.GetWalletByUseCase
import com.simple.wallet.presentation.adapters.BottomViewItem
import com.simple.wallet.presentation.adapters.KeyValueViewItemV3
import com.simple.wallet.presentation.adapters.MessageViewItem
import com.simple.wallet.presentation.adapters.TokenApproveViewItem
import com.simple.wallet.presentation.transaction.send.adapter.FeeTransactionInfoViewItem
import com.simple.wallet.utils.exts.FormatNumberType
import com.simple.wallet.utils.exts.decimal
import com.simple.wallet.utils.exts.shortenValue
import com.simple.wallet.utils.exts.takeIfNotEmpty
import com.simple.wallet.utils.exts.toDisplay
import com.simple.wallet.utils.exts.toTransactionHeaderViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

class SendTransactionConfirmViewModel(
    val mRequest: Request,

    private val getTokenByUseCase: GetTokenByUseCase,
    private val getChainByUseCase: GetChainByUseCase,
    private val getWalletByUseCase: GetWalletByUseCase,

    private val sendTransactionUseCase: SendTransactionUseCase,
    private val detectRequestAsyncUseCase: DetectRequestAsyncUseCase,
) : TransactionViewModel() {

    override val currentChain: LiveData<Chain> = liveData {

        getChainByUseCase.execute(GetChainByUseCase.Param(mRequest.chainId!!)).firstOrNull()?.let {

            postValue(it)
        }
    }

    override var currentWallet: LiveData<Wallet> = liveData {

        getWalletByUseCase.execute(GetWalletByUseCase.Param(mRequest.walletAddress ?: return@liveData)).firstOrNull()?.let {

            postValue(it)
        }
    }

    override val nativeToken: LiveData<Token> = combineSources(currentChain) {

        getTokenByUseCase.execute(GetTokenByUseCase.Param(chainId = listOf(mRequest.chainId!!), tokenType = listOf(Token.Type.NATIVE))).firstOrNull()?.let {

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

        detectRequestAsyncUseCase.execute(DetectRequestAsyncUseCase.Param(request.get())).collect {

            postValue(ResultState.Success(it))
        }
    }

    @VisibleForTesting
    val requestDetect: LiveData<Request> = combineSources(requestDetectState) {

        requestDetectState.get().doSuccess {

            postValue(it)
        }
    }

    override val transaction: LiveData<Transaction> = combineSources(requestDetectState) {

        requestDetectState.get().doSuccess {

            postValue(it.transaction)
        }
    }


    @VisibleForTesting
    val headerViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(requestDetect) {

        val list = arrayListOf<ViewItemCloneable>()

        list.add(requestDetect.get().toTransactionHeaderViewItem())

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val transactionInfoViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(nativeToken, requestDetect) {

        val nativeToken = nativeToken.get()

        val transaction = requestDetect.get().transaction ?: return@combineSources

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(transaction.getTransactionInfo(nativeToken))

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val transactionFeeViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(gas, gasLimit, bonusFee, nativeToken, currentChain) {

        val gas = gas.get()

        val bonusFee = bonusFee.get()

        val gasLimit = gasLimit.get()

        val nativeToken = nativeToken.get()

        val currentChain = currentChain.get()


        val list = arrayListOf<ViewItemCloneable>()

        list.add(FeeTransactionInfoViewItem().refresh(gas, gasLimit, nativeToken, currentChain, bonusFee))

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val messageViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(gas, gasLimit, bonusFee, nativeToken, isConfirm, requestDetectState) {

        val requestDetectState = requestDetectState.get()

        val request = requestDetectState.toSuccess()?.data


        val gas = gas.get()

        val gasLimitInfo = gasLimitState.get().toSuccess()?.data?.let { Pair(gasLimit.get().toBigDecimal(), Range(it.second.lower.toBigDecimal(), it.second.upper.toBigDecimal())) } ?: return@combineSources


        val bonusFee = bonusFee.get()

        val nativeToken = nativeToken.get()


        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(request.toMessageViewItem(isConfirm.get(), gas, gasLimitInfo, nativeToken, bonusFee))

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
    val viewItemList: LiveData<List<ViewItemCloneable>> = combineSources(headerViewItemList, transactionInfoViewItemList, transactionFeeViewItemList.orListEmpty(), messageViewItemList.orListEmpty(), bottomViewItemList) {

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(headerViewItemList.getOrEmpty())

        transactionInfoViewItemList.getOrEmpty().takeIfNotEmpty()?.let {

            list.add(SpaceViewItem(height = DP_20))
            list.addAll(it)
        }

        transactionFeeViewItemList.getOrEmpty().takeIfNotEmpty()?.let {

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


    internal val sendTransactionState: LiveData<ResultState<String>> = MediatorLiveData()


    internal val buttonState: LiveData<Enum<*>> = listenerSources(currentWallet, gasListState, gasLimitState, bonusFeeState, requestDetectState, sendTransactionState) {

        (if (gasListState.value.isStart() || gasLimitState.value.isStart() || bonusFeeState.value.isStart() || requestDetectState.value.isStart()) {

            ButtonState.DETECT_LOADING
        } else if (gasListState.value.isFailed() || gasLimitState.value.isFailed() || bonusFeeState.value.isFailed() || requestDetectState.value.isFailed()) {

            ButtonState.DETECT_FAILED
        } else if (currentWallet.value?.isWatch == true) {

            ButtonState.WATCH_WALLET
        } else if (sendTransactionState.value.isStart()) {

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

    fun sendTransaction() = viewModelScope.launch(handler + Dispatchers.IO) {

        val messageViewItemList = messageViewItemList.getOrEmpty().filterIsInstance<MessageViewItem>()

        if (messageViewItemList.isNotEmpty() && messageViewItemList.any { it.needConfirm } && isConfirm.value == false) {

            sendTransactionState.postValue(ResultState.Failed(AppException(code = TransactionCode.PLEASE_CONFIRM)))

            return@launch
        }


        kotlin.runCatching {

            val gas = gas.get()

            val transaction = transaction.get()

            transaction.nonce = nonce.get()
            transaction.gasLimit = gasLimit.get()
            transaction.gasPriceWei = gas.gasPriceWei
            transaction.priorityFeeWei = gas.priorityFeeWei

            sendTransactionState.postDifferentValue(ResultState.Start)

            sendTransactionState.postDifferentValue(sendTransactionUseCase.execute(SendTransactionUseCase.Param(transaction)))
        }.getOrElse {

            sendTransactionState.postDifferentValue(ResultState.Failed(it))
        }
    }

    private fun Transaction.getTransactionInfo(nativeToken: Token): List<ViewItemCloneable> {

        val list = arrayListOf<ViewItemCloneable>()

        if (type == Transaction.Type.SEND) {

            val extra = this.extra.asObjectOrNull<TransferExtra>()!!

            val keyReceiver = R.string.title_receiver.toText()

            val valueReceiver = extra.receiverAddress.shortenValue().toText().withStyle(Typeface.BOLD)

            list.add(KeyValueViewItemV3("RECEIVER", key = keyReceiver, value = valueReceiver).refresh())


            val keyValue = R.string.title_amount_transfer.toText()

            val valueValue = listOf(
                listOf("-".toText(), extra.amountTransfer.decimal(extra.tokenTransfer.decimals).toDisplay(FormatNumberType.BALANCE)).toText(" "),
                extra.tokenTransfer.symbol.uppercase().toText()
            ).toText(" ").withStyle(Typeface.BOLD)

            list.add(KeyValueViewItemV3("VALUE", key = keyValue, value = valueValue).refresh())
        } else if (type == Transaction.Type.APPROVAL) {

            val extra = this.extra.asObjectOrNull<ApproveExtra>()!!

            val keyReceiver = R.string.title_sender.toText()

            val valueReceiver = extra.senderAddress.shortenValue().toText().withStyle(Typeface.BOLD)

            list.add(KeyValueViewItemV3("SENDER", key = keyReceiver, value = valueReceiver).refresh())


            if (extra.amountApprove > BigInteger.ZERO) list.add(TokenApproveViewItem(extra).refresh())
        } else {

            val keySender = R.string.title_sender.toText()

            val valueSender = to.shortenValue().toText().withStyle(Typeface.BOLD)

            list.add(KeyValueViewItemV3("SENDER", key = keySender, value = valueSender).refresh())


            val keyValue = R.string.title_value.toText()

            val valueValue = listOf(
                listOf("-".toText(), value.decimal(nativeToken.decimals).toDisplay(FormatNumberType.BALANCE)).toText(" "),
                nativeToken.symbol.uppercase().toText()
            ).toText(" ").withStyle(Typeface.BOLD)

            list.add(KeyValueViewItemV3("VALUE", key = keyValue, value = valueValue).refresh())


            val keyData = R.string.title_data.toText()

            val valueData = data.toText().withStyle(Typeface.BOLD)

            list.add(KeyValueViewItemV3("DATA", key = keyData, value = valueData).refresh())
        }

        return list
    }

    private fun Request?.toMessageViewItem(isConfirm: Boolean, gas: Gas, gasLimitInfo: Pair<BigDecimal, Range<BigDecimal>>, native: Token, bonusFee: BigDecimal): List<ViewItemCloneable> {

        if (this == null) {

            return emptyList()
        }

        val gasLimit = gasLimitInfo.first

        val gasLimitRange = gasLimitInfo.second


        val list = arrayListOf<Text>()

        if (gasLimit !in gasLimitRange) {

            val from = StringBuilder()
                .append(
                    gas.gasPriceWei.multiply(gasLimitInfo.second.lower)
                        .plus(bonusFee)
                        .fromWei(Convert.Unit.ETHER).toDisplay(FormatNumberType.GAS_FEE)
                )
                .append(" " + native.symbol).toString()


            val to = StringBuilder()
                .append(
                    gas.gasPriceWei.multiply(gasLimitRange.upper).toWei()
                        .plus(bonusFee)
                        .fromWei(Convert.Unit.ETHER)
                )
                .append(" " + native.symbol).toString()

            list.add(TextRes(R.string.message_warning_out_gas, from.toText(), to.toText()))
        }

        if (power?.status in listOf(Request.Power.Status.RISK)) {

            list.add(TextRes(R.string.message_warning_url_risk, TextImage(R.drawable.ic_check_box_normal_accent_24dp, 16.toPx())))
        }

        if (list.isNotEmpty()) MessageViewItem().apply {

            list.add(0, TextSpan(R.string.message_warning.toText(), StyleSpan(Typeface.BOLD)))

            message = list.toText("\n")

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