//package com.simple.wallet.presentation.transaction.send
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.FrameLayout
//import androidx.core.os.bundleOf
//import androidx.core.view.children
//import androidx.core.view.isVisible
//import androidx.core.view.updatePadding
//import androidx.fragment.app.Fragment
//import androidx.transition.ChangeBounds
//import androidx.transition.Fade
//import androidx.transition.TransitionManager
//import androidx.transition.TransitionSet
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import com.simple.adapter.MultiAdapter
//import com.simple.coreapp.ui.adapters.SpaceAdapter
//import com.simple.coreapp.ui.base.fragments.BaseViewModelSheetFragment
//import com.simple.coreapp.ui.dialogs.OptionFragment.Companion.KEY_REQUEST
//import com.simple.coreapp.utils.AppException
//import com.simple.coreapp.utils.autoCleared
//import com.simple.coreapp.utils.ext.getSerializableOrNull
//import com.simple.coreapp.utils.ext.getStringOrEmpty
//import com.simple.coreapp.utils.extentions.beginTransitionAwait
//import com.simple.coreapp.utils.extentions.doOnHeightNavigationChange
//import com.simple.coreapp.utils.extentions.observeLaunch
//import com.simple.coreapp.utils.extentions.observeQueue
//import com.simple.coreapp.utils.extentions.scaleAnimAwait
//import com.simple.coreapp.utils.extentions.setDebouncedClickListener
//import com.simple.coreapp.utils.extentions.setVisible
//import com.simple.coreapp.utils.extentions.submitListAwait
//import com.simple.coreapp.utils.extentions.toPx
//import com.simple.coreapp.utils.extentions.vibrate
//import com.simple.navigation.domain.entities.NavigationEvent
//import com.simple.navigation.utils.ext.setNavigationResultListener
//import com.simple.state.ResultState
//import com.simple.state.isFailed
//import com.simple.state.isStart
//import com.simple.state.isSuccess
//import com.simple.state.toSuccess
//import com.simple.wallet.PARAM_DATA
//import com.simple.wallet.R
//import com.simple.wallet.databinding.LayoutActionConfirmBinding
//import com.simple.wallet.databinding.PopupListBinding
//import com.simple.wallet.domain.entities.Chain
//import com.simple.wallet.domain.entities.Request
//import com.simple.wallet.presentation.adapters.BottomAdapter
//import com.simple.wallet.presentation.adapters.HeaderAdapter
//import com.simple.wallet.presentation.adapters.KeyValueAdapter
//import com.simple.wallet.presentation.adapters.MessageAdapter
//import com.simple.wallet.presentation.adapters.MessageViewItem
//import com.simple.wallet.presentation.adapters.TokenApproveAdapter
//import org.koin.core.parameter.ParametersDefinition
//import org.koin.core.parameter.parametersOf
//
//
//class SendTransactionConfirmFragment : BaseViewModelSheetFragment<PopupListBinding, SendTransactionConfirmViewModel>() {
//
//
//    private val keyRequest: String by lazy {
//
//        requireArguments().getStringOrEmpty(KEY_REQUEST)
//    }
//
//    private val request: Request by lazy {
//
//        requireArguments().getSerializableOrNull(PARAM_DATA)!!
//    }
//
//
//    private val chainId: Long by lazy {
//
//        request.transaction?.chainId ?: Chain.ETHEREUM_ID
//    }
//
//
//    private var adapter by autoCleared<MultiAdapter>()
//
//    private var bindingAction by autoCleared<LayoutActionConfirmBinding>()
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val binding = binding ?: return
//
//        bindingAction = LayoutActionConfirmBinding.inflate(LayoutInflater.from(binding.root.context))
//
//        setupCancel()
//        setupConfirm()
//        setupSetting()
//        setupBottomSheet()
//        setupWindowInset()
//        setupRecyclerView()
//
//        observeData()
//
//        requireContext().vibrate()
//
//        val container = container ?: return
//
//        container.addView(bindingAction!!.root, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM))
//    }
//
//    override fun dismiss() {
//
////        val state = viewModel.sendTransactionState.value
////
////        val resultData = state?.toSuccess()?.data ?: state?.toFailed()?.message ?: ""
////
////        val resultStatus = state?.isSuccess() == true
////
////        setNavigationResult(keyRequest, bundleOf(PARAM_DATA to viewModel.mRequest, PARAM_RESULT_STATUS to resultStatus, PARAM_RESULT_DATA to resultData))
//
//        super.dismiss()
//    }
//
//    override fun getParameter(): ParametersDefinition {
//
//        return { parametersOf(request) }
//    }
//
//    private fun setupCancel() {
//
//        val binding = bindingAction ?: return
//
//        binding.tvNegative.setOnClickListener {
//
//            dismiss()
//        }
//    }
//
//    private fun setupConfirm() {
//
//        val binding = bindingAction ?: return
//
//        binding.tvPositive.setDebouncedClickListener(anim = true) {
//
//            viewModel.sendTransaction()
//        }
//    }
//
//    private fun setupSetting() {
//
//        binding ?: return
//
//        setNavigationResultListener(KEY_REQUEST_SETTING) { _, result ->
//
////            val gas = result.getParcelableOrNull<Gas>(PARAM_GAS)
////            val customNonce = result.getSerializableOrNull<BigInteger>(PARAM_NONCE)
////            val customGasLimit = result.getSerializableOrNull<BigDecimal>(PARAM_GAS_LIMIT)
////
////            viewModel.updateSettingInfo(gas, customGasLimit, customNonce)
//        }
//    }
//
//    private fun setupBottomSheet() {
//
//        val behavior = behavior ?: return
//
//        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//                updateLocationAction()
//            }
//        })
//    }
//
//    private fun setupWindowInset() {
//
//        val binding = binding ?: return
//
//        doOnHeightNavigationChange {
//
//            bindingAction?.root?.updatePadding(left = 30.toPx(), bottom = it + 30.toPx(), right = 30.toPx())
//
//            bindingAction?.root?.post {
//
//                binding.root.updatePadding(bottom = bindingAction!!.root.height)
//            }
//        }
//    }
//
//    private fun setupRecyclerView() {
//
//        val binding = binding ?: return
//
//        val spaceAdapter = SpaceAdapter()
//
//        val keyValueAdapter = KeyValueAdapter(onKeyItemClick = {
//
//        }, onValueItemClick = {
//
//        })
//
////        val feeTransactionInfoAdapter = FeeTransactionInfoAdapter { view, item ->
////
////            val gasId = viewModel.gas.value?.id
////
////            val nonce = viewModel.customNonce.value
////
////            val bonusFee = viewModel.bonusFee.value
////
////            val gasLimit = viewModel.gasLimit.value?.toBigDecimal() ?: BigDecimal("100000")
////
////            val gasLimitDefault = viewModel.gasLimitDefault.value?.toBigDecimal() ?: BigDecimal("100000")
////
////
////            val walletAddress = viewModel.currentWallet.get().address
////
////
////            offerNavEvent(SendTransactionGasEvent(KEY_REQUEST_SETTING, chainId, walletAddress, gasId, nonce, bonusFee, gasLimit, gasLimitDefault))
////        }
//
//        val bottomAdapter = BottomAdapter()
//
//        val headerAdapter = HeaderAdapter()
//
//        val messageAdapter = MessageAdapter { view, item ->
//
//            viewModel.updateConfirm()
//        }
//
//        val tokenApproveAdapter = TokenApproveAdapter()
//
//        adapter = MultiAdapter(
//            spaceAdapter,
//            bottomAdapter,
//            headerAdapter,
//            messageAdapter,
//            keyValueAdapter,
//            tokenApproveAdapter,
////            feeTransactionInfoAdapter,
//        ).apply {
//
//            setRecyclerView(binding.recyclerView)
//        }
//
//        binding.recyclerView.itemAnimator = null
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun observeData() = with(viewModel) {
//
//        buttonState.observe(viewLifecycleOwner) { state ->
//
//            val bindingAction = bindingAction ?: return@observe
//
//
//            val buttonState = state.toSuccess()?.data
//
//            bindingAction.tvPositive.isClickable = buttonState == ButtonState.REVIEW_TRANSACTION
//            bindingAction.tvPositive.alpha = if (!bindingAction.tvPositive.isClickable) 0.2f else 1f
//
//
//            bindingAction.progressPositive.isVisible = state.isStart()
//
//
//            bindingAction.tvPositive.text = when (state.toSuccess()?.data) {
//
//                ButtonState.WATCH_WALLET -> getString(R.string.action_not_support)
//
//                else -> getString(R.string.action_confirm)
//            }
//        }
//
//        sendTransactionState.observeLaunch(viewLifecycleOwner) {
//
//            if (it.isSuccess()) {
//
//                dismiss()
//                return@observeLaunch
//            }
//
//            if (it is ResultState.Failed && it.cause is AppException) if ((it.cause as AppException).code == TransactionCode.PLEASE_CONFIRM) {
//
//                val index = adapter?.currentList?.indexOfFirst { it is MessageViewItem }?.takeIf { it >= 0 } ?: return@observeLaunch
//
//                val itemView = binding?.recyclerView?.findViewHolderForAdapterPosition(index)?.itemView ?: return@observeLaunch
//
//                itemView.scaleAnimAwait(1.1f, 500, true)
//
//                return@observeLaunch
//            }
//
//
//            val bindingAction = bindingAction ?: return@observeLaunch
//
//            bindingAction.tvNegative.isClickable = it.isFailed()
//            bindingAction.tvPositive.isClickable = it.isFailed()
//
//            bindingAction.progressPositive.setVisible(it.isStart())
//            bindingAction.tvNegative.setVisible(!it.isStart())
//
//            TransitionManager.beginDelayedTransition(bindingAction.root, TransitionSet().setDuration(350).addTransition(ChangeBounds()).addTransition(Fade()))
//        }
//
//        viewItemListDisplay.observeQueue(viewLifecycleOwner, tag = this@SendTransactionConfirmFragment.javaClass.name, context = handler) {
//
//            val binding = binding ?: return@observeQueue
//
//            val bottomSheet = bottomSheet ?: return@observeQueue
//
//            binding.recyclerView.submitListAwait(it)
//
//            bottomSheet.beginTransitionAwait(TransitionSet().setDuration(350).addTransition(ChangeBounds()).addTransition(Fade()))
//        }
//    }
//
//    private fun updateLocationAction() {
//
//        val binding = binding ?: return
//
//        val bottomSheet = bottomSheet ?: return
//
//        val bindingAction = bindingAction ?: return
//
//        val bottom = binding.recyclerView.top + (binding.recyclerView.children.firstOrNull()?.bottom ?: 0)
//
//        val transaction = (bottomSheet.parent as View).top + bottomSheet.top + bottom
//        val transactionYMin = transaction - bindingAction.root.top + 0f
//
//        bindingAction.root.translationY = maxOf(0f, transactionYMin)
//    }
//
//    companion object {
//
//        fun newInstance(keyRequest: String, data: Request) = SendTransactionConfirmFragment().apply {
//
//            arguments = bundleOf(
//
//                KEY_REQUEST to keyRequest,
//
//                PARAM_DATA to data,
//            )
//        }
//    }
//}
//
//data class SendTransactionEvent(val keyRequest: String, val data: Request) : NavigationEvent() {
//
//    override fun provideFragment(): Fragment {
//
//        return SendTransactionConfirmFragment.newInstance(keyRequest, data)
//    }
//}
//
//private const val KEY_REQUEST_SETTING = "KEY_REQUEST_SETTING"
