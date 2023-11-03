package com.simple.wallet.presentation.walletconnect.approveconnect

import android.graphics.Typeface
import android.text.style.StyleSpan
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.simple.adapter.LoadingViewItem
import com.simple.adapter.ViewItemCloneable
import com.simple.core.utils.extentions.asObjectOrNull
import com.simple.coreapp.ui.adapters.SpaceViewItem
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.AppException
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.listenerSources
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.offerActive
import com.simple.coreapp.utils.extentions.orListEmpty
import com.simple.coreapp.utils.extentions.orStateEmpty
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.coreapp.utils.extentions.postValue
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.text.TextImage
import com.simple.coreapp.utils.extentions.text.TextRes
import com.simple.coreapp.utils.extentions.text.TextSpan
import com.simple.coreapp.utils.extentions.text.TextWithTextColorAttrColor
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.withTextColor
import com.simple.state.ResultState
import com.simple.state.isFailed
import com.simple.state.isStart
import com.simple.state.isSuccess
import com.simple.state.toSuccess
import com.simple.wallet.DP_12
import com.simple.wallet.DP_16
import com.simple.wallet.DP_20
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.usecases.DetectRequestAsyncUseCase
import com.simple.wallet.domain.usecases.chain.GetChainSelectedUseCase
import com.simple.wallet.domain.usecases.wallet.GetWalletSelectedUseCase
import com.simple.wallet.domain.usecases.walletconnect.ApproveConnectUseCase
import com.simple.wallet.domain.usecases.walletconnect.GetConnectInfoAsyncUseCase
import com.simple.wallet.domain.usecases.walletconnect.PairConnectUseCase
import com.simple.wallet.domain.usecases.walletconnect.RejectConnectUseCase
import com.simple.wallet.presentation.adapters.BottomViewItem
import com.simple.wallet.presentation.adapters.ErrorHeaderViewItem
import com.simple.wallet.presentation.adapters.KeyValueViewItemV3
import com.simple.wallet.presentation.adapters.MessageViewItem
import com.simple.wallet.utils.exts.takeIfNotEmpty
import com.simple.wallet.utils.exts.toConnectHeaderViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class ApproveConnectConfirmViewModel(
    private val pair: String? = null,
    private val slide: Request.Slide? = null,

    private val pairConnectUseCase: PairConnectUseCase,

    private val rejectConnectUseCase: RejectConnectUseCase,
    private val approveConnectUseCase: ApproveConnectUseCase,

    private val detectRequestAsyncUseCase: DetectRequestAsyncUseCase,
    private val getConnectInfoAsyncUseCase: GetConnectInfoAsyncUseCase,

    private val getChainSelectedUseCase: GetChainSelectedUseCase,
    private val getWalletSelectedUseCase: GetWalletSelectedUseCase
) : BaseViewModel() {


    private val itemLoadingList = listOf(
        LoadingViewItem(R.layout.item_transaction_info_header_loading)
    )


    internal val rejectConnectState: LiveData<ResultState<Request>> = MediatorLiveData()

    internal val approveConnectState: LiveData<ResultState<Request>> = MediatorLiveData()


    val currentChain: LiveData<Chain> = liveData {

        postValue(getChainSelectedUseCase.execute(GetChainSelectedUseCase.Param(isSupportAllChain = true)))
    }

    var currentWallet: LiveData<Wallet> = liveData {

        postValue(getWalletSelectedUseCase.execute(GetWalletSelectedUseCase.Param(isSupportAllChain = false)))
    }


    @VisibleForTesting
    val isConfirm: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {

        value = false
    }

    @VisibleForTesting
    val requestDetectState: LiveData<ResultState<Request>> = liveData {

        postValue(ResultState.Start)

        getConnectInfo().collect {

            postValue(it)
        }
    }


    @VisibleForTesting
    val headerViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(requestDetectState) {

        val state = requestDetectState.get()

        if (state !is ResultState.Success) return@combineSources


        val list = arrayListOf<ViewItemCloneable>()

        list.add(state.data.toConnectHeaderViewItem())

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val infoViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(requestDetectState) {

        val state = requestDetectState.get()

        if (state !is ResultState.Success) return@combineSources


        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(state.data.getConnectInfo())

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val messageViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(isConfirm, requestDetectState, rejectConnectState.orStateEmpty(), approveConnectState.orStateEmpty()) {

        val state = requestDetectState.get()

        if (state !is ResultState.Success) return@combineSources

        val request = state.data

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(request.toMessageViewItem(isConfirm.get()))

//        rejectConnectState.value?.asObjectOrNull<ResultState.Failed>()?.toMessageInfo("rejectConnectState")?.let { viewItem ->
//
//            list.add(SpaceViewItem(height = 8.toPx()))
//            list.add(viewItem)
//        }

        approveConnectState.value?.asObjectOrNull<ResultState.Failed>()?.toMessageInfo("approveConnectState")?.let { viewItem ->

            list.add(SpaceViewItem(height = 8.toPx()))
            list.add(viewItem)
        }

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val bottomViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(currentChain, currentWallet) {

        val currentChain = currentChain.get()

        val currentWallet = currentWallet.get()

        val list = arrayListOf<ViewItemCloneable>()

        list.add(BottomViewItem(currentChain, currentWallet).refresh(true))

        postDifferentValueIfActive(list)
    }

    @VisibleForTesting
    val dataInfoViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(headerViewItemList, infoViewItemList.orListEmpty(), messageViewItemList.orListEmpty(), bottomViewItemList) {

        val list = arrayListOf<ViewItemCloneable>()

        list.addAll(headerViewItemList.getOrEmpty())

        infoViewItemList.getOrEmpty().takeIfNotEmpty()?.let {

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

    @VisibleForTesting
    val requestDetectViewItemList: LiveData<List<ViewItemCloneable>> = combineSources(requestDetectState) {

        when (val requestDetectState = requestDetectState.get()) {

            is ResultState.Start -> {

                postDifferentValue(itemLoadingList)
            }

            is ResultState.Failed -> ErrorHeaderViewItem(
                logo = R.raw.anim_connect_error,
                title = R.string.title_connect_error.toText(),
                caption = requestDetectState.cause.message?.toText() ?: emptyText()
            ).let {

                postDifferentValue(listOf(it))
            }

            else -> {

                postDifferentValue(emptyList())
            }
        }
    }


    @VisibleForTesting
    val viewItemList: LiveData<List<ViewItemCloneable>> = listenerSources(dataInfoViewItemList, requestDetectViewItemList) {

        val dataInfoViewItemList = dataInfoViewItemList.getOrEmpty()

        val requestDetectViewItemList = requestDetectViewItemList.getOrEmpty()

        if (dataInfoViewItemList.isNotEmpty()) {

            postDifferentValueIfActive(dataInfoViewItemList)
        } else if (requestDetectViewItemList.isNotEmpty()) {

            postDifferentValueIfActive(requestDetectViewItemList)
        }
    }

    internal val viewItemListDisplay: LiveData<List<ViewItemCloneable>> = listenerSources(viewItemList) {

        postDifferentValueIfActive(viewItemList.getOrEmpty())
    }


    internal val buttonState: LiveData<Enum<*>> = listenerSources(currentWallet, requestDetectState, rejectConnectState, approveConnectState) {

        (if (requestDetectState.value.isStart()) {

            ButtonState.DETECT_LOADING
        } else if (requestDetectState.value.isFailed()) {

            ButtonState.DETECT_FAILED
        } else if (currentWallet.value?.isWatch == true) {

            ButtonState.WATCH_WALLET
        } else if (rejectConnectState.value.isStart()) {

            ButtonState.REJECT_LOADING
        } else if (approveConnectState.value.isStart()) {

            ButtonState.APPROVE_LOADING
        } else {

            ButtonState.REVIEW
        }).let {

            postDifferentValue(it)
        }
    }


    fun updateConfirm() {

        isConfirm.postValue(true)
    }

    fun updateCurrentWallet(wallet: Wallet) {

        this.currentWallet.postDifferentValue(wallet) { old, new ->
            old?.id.equals(new.id, true)
        }
    }


    fun rejectConnect() = viewModelScope.launch(handler + Dispatchers.IO) {

        approveConnectState.postValue(null)

        val requestDetectState = requestDetectState.get()

        if (!requestDetectState.isSuccess()) {

            rejectConnectState.postValue(ResultState.Failed(AppException(code = ErrorCode.REQUEST_DETECT_FAILED)))

            return@launch
        }


        kotlin.runCatching {

            val request = requestDetectState.toSuccess()!!.data

            rejectConnectState.postDifferentValue(ResultState.Start)

            rejectConnectState.postDifferentValue(rejectConnectUseCase.execute(RejectConnectUseCase.Param(request)))
        }.getOrElse {

            rejectConnectState.postDifferentValue(ResultState.Failed(it))
        }
    }

    fun approveConnect() = viewModelScope.launch(handler + Dispatchers.IO) {

        rejectConnectState.postValue(null)

        val messageViewItemList = messageViewItemList.getOrEmpty().filterIsInstance<MessageViewItem>()

        if (messageViewItemList.isNotEmpty() && messageViewItemList.any { it.needConfirm } && isConfirm.value == false) {

            approveConnectState.postValue(ResultState.Failed(AppException(code = ErrorCode.PLEASE_CONFIRM)))

            return@launch
        }


        kotlin.runCatching {

            val wallet = currentWallet.get()

            val request = requestDetectState.get().toSuccess()!!.data

            approveConnectState.postDifferentValue(ResultState.Start)

            approveConnectState.postDifferentValue(approveConnectUseCase.execute(ApproveConnectUseCase.Param(wallet, request)))
        }.getOrElse {

            approveConnectState.postDifferentValue(ResultState.Failed(it))
        }
    }


    private suspend fun getConnectInfo() = if (pair == null || slide == null) getConnectInfoAsyncUseCase.execute().flatMapConcat {

        detectRequestAsyncUseCase.execute(DetectRequestAsyncUseCase.Param(it))
    }.map {

        ResultState.Success(it)
    } else {

        pair(pair, slide, System.currentTimeMillis())
    }

    private suspend fun pair(pair: String, slide: Request.Slide, start: Long) = pairConnectUseCase.execute(PairConnectUseCase.Param(pair, slide)).let { state ->

        if (state.isFailed()) {

            delay(1000 - System.currentTimeMillis() + start)
        }

        when (state) {

            is ResultState.Success -> detectRequestAsyncUseCase.execute(DetectRequestAsyncUseCase.Param(state.data)).map {

                ResultState.Success(it)
            }

            else -> channelFlow<ResultState<Request>> {

                offerActive(state)

                awaitClose { }
            }
        }
    }

    @Suppress("UnusedReceiverParameter")
    private fun Request.getConnectInfo(): List<ViewItemCloneable> {

        val list = arrayListOf<ViewItemCloneable>()

        list.add(SpaceViewItem(height = 20.toPx(), background = R.drawable.bg_top_corners_8dp_stroke_1dp_divider))

        TextSpan(TextWithTextColorAttrColor(R.string.message_connect_0.toText(), com.google.android.material.R.attr.colorOnBackground), StyleSpan(Typeface.BOLD)).let {

            list.add(KeyValueViewItemV3("0", key = it, value = emptyText(), background = R.drawable.bg_left_right_stroke_1dp_divider, paddingLeft = DP_16, paddingRight = DP_16).refresh())
        }

        listOf(
            TextImage(R.drawable.img_tick_accent_24dp, DP_12),
            TextWithTextColorAttrColor(R.string.message_connect_1.toText(), com.google.android.material.R.attr.colorAccent),
        ).toText("   ").let {

            list.add(KeyValueViewItemV3("1", key = it, value = emptyText(), background = R.drawable.bg_left_right_stroke_1dp_divider, paddingLeft = DP_16, paddingRight = DP_16).refresh())
        }

        listOf(
            TextImage(R.drawable.img_tick_accent_24dp, DP_12),
            TextWithTextColorAttrColor(R.string.message_connect_2.toText(), com.google.android.material.R.attr.colorAccent),
        ).toText("   ").let {

            list.add(KeyValueViewItemV3("2", key = it, value = emptyText(), background = R.drawable.bg_left_right_stroke_1dp_divider, paddingLeft = DP_16, paddingRight = DP_16).refresh())
        }

        listOf(
            TextImage(R.drawable.img_close_on_background_variant_24dp, DP_12),
            TextWithTextColorAttrColor(R.string.message_connect_3.toText(), com.simple.coreapp.R.attr.colorOnBackgroundVariant),
        ).toText("   ").let {

            list.add(KeyValueViewItemV3("3", key = it, value = emptyText(), background = R.drawable.bg_left_right_stroke_1dp_divider, paddingLeft = DP_16, paddingRight = DP_16).refresh())
        }

        list.add(SpaceViewItem(height = 20.toPx(), background = R.drawable.bg_bottom_corners_16dp_stroke_1dp_divider))

        return list
    }

    private fun ResultState.Failed.toMessageInfo(id: String) = if (cause.message?.isNotEmpty() == true) MessageViewItem(id).apply {

        message = cause.message?.toText()?.withTextColor(com.google.android.material.R.attr.colorError) ?: emptyText()
        messageIcon = R.drawable.img_error_24dp.toImage()
        background = R.drawable.bg_corners_16dp_solid_error_10
    } else {

        null
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

    enum class ButtonState {

        REVIEW, WATCH_WALLET, DETECT_LOADING, DETECT_FAILED, REJECT_LOADING, APPROVE_LOADING, NONE
    }

    enum class ErrorCode {

        PLEASE_CONFIRM, REQUEST_DETECT_FAILED
    }
}