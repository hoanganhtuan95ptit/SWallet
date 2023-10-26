package com.simple.wallet.presentation.approveconnect

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet
import androidx.transition.TransitionSet.ORDERING_TOGETHER
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.adapter.MultiAdapter
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.ui.adapters.SpaceAdapter
import com.simple.coreapp.ui.base.fragments.BaseViewModelSheetFragment
import com.simple.coreapp.ui.dialogs.OptionFragment.Companion.KEY_REQUEST
import com.simple.coreapp.utils.AppException
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightNavigationChange
import com.simple.coreapp.utils.extentions.observeLaunch
import com.simple.coreapp.utils.extentions.observeQueue
import com.simple.coreapp.utils.extentions.postAwait
import com.simple.coreapp.utils.extentions.scaleAnimAwait
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setVisible
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.coreapp.utils.extentions.vibrate
import com.simple.navigation.utils.ext.setNavigationResultListener
import com.simple.state.ResultState
import com.simple.state.isStart
import com.simple.state.isSuccess
import com.simple.wallet.DP_32
import com.simple.wallet.PAYLOAD_PAIR
import com.simple.wallet.PAYLOAD_SLIDE
import com.simple.wallet.R
import com.simple.wallet.databinding.LayoutActionConfirmBinding
import com.simple.wallet.databinding.PopupListBinding
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Request.Slide.Companion.toSessionRequestSlide
import com.simple.wallet.presentation.adapters.BottomAdapter
import com.simple.wallet.presentation.adapters.ErrorAdapter
import com.simple.wallet.presentation.adapters.HeaderAdapter
import com.simple.wallet.presentation.adapters.KeyValueAdapter
import com.simple.wallet.presentation.adapters.MessageAdapter
import com.simple.wallet.presentation.adapters.MessageViewItem
import com.simple.wallet.presentation.adapters.TokenApproveAdapter
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf

internal class ApprovalConnectConfirmFragment : BaseViewModelSheetFragment<PopupListBinding, ApproveConnectConfirmViewModel>() {


    private val pair: String? by lazy {

        arguments?.getString(PAYLOAD_PAIR)
    }

    private val slide: Request.Slide? by lazy {

        arguments?.getString(PAYLOAD_SLIDE)?.toSessionRequestSlide()
    }


    private var adapter by autoCleared<MultiAdapter>()

    private var bindingAction by autoCleared<LayoutActionConfirmBinding>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = binding ?: return

        bindingAction = LayoutActionConfirmBinding.inflate(LayoutInflater.from(binding.root.context))

        setupCancel()
        setupConfirm()
        setupBottomSheet()
        setupWindowInset()
        setupRecyclerView()

        observeData()
//        observeChainData()
//        observeWalletData()
//        observerCurrencyData()

        requireContext().vibrate()

        val container = container ?: return

        container.addView(bindingAction!!.root, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM))
    }

    override fun getParameter(): ParametersDefinition {

        return { parametersOf(pair, slide) }
    }

    private fun setupCancel() {

        val binding = bindingAction ?: return

        binding.tvNegative.setOnClickListener {

            viewModel.rejectConnect()
        }
    }

    private fun setupConfirm() {

        val binding = bindingAction ?: return

        binding.tvPositive.setDebouncedClickListener(anim = true) {

            viewModel.approveConnect()
        }
    }

    private fun setupBottomSheet() {

        binding ?: return

        val bottomSheet = bottomSheet ?: return

        val behavior = BottomSheetBehavior.from(bottomSheet)

        val bottomSheetParent = bottomSheet.parent as ViewGroup

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                updateLocationAction()
            }
        })

        bottomSheet.doOnLayout {

            behavior.peekHeight = (bottomSheetParent.parent as View).height
        }

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupWindowInset() {

        doOnHeightNavigationChange {

            val binding = binding ?: return@doOnHeightNavigationChange

            val bindingAction = bindingAction ?: return@doOnHeightNavigationChange

            bindingAction.root.updatePadding(left = DP_32, bottom = it + DP_32, right = DP_32)

            bindingAction.root.post {

                binding.root.updatePadding(bottom = bindingAction.root.height)
            }
        }
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return


        val keyRequestPickWallet = "KEY_REQUEST_PICK_WALLET"

        setNavigationResultListener(keyRequestPickWallet) { _, bundle ->

//            val listWallet = walletViewModel.walletList.getOrEmpty()
//
//            val walletPick = bundle.getParcelableOrNull<Wallet>(SelectWalletPopup.PARAM_WALLET) ?: return@setNavigationResultListener
//
//            val wallet = listWallet.firstOrNull { it.id.equals(walletPick.id, true) } ?: return@setNavigationResultListener
//
//            viewModel.updateCurrentWallet(wallet)
        }


        val spaceAdapter = SpaceAdapter()

        val keyValueAdapter = KeyValueAdapter(onKeyItemClick = {

        }, onValueItemClick = {

        })

        val messageInfoAdapter = MessageAdapter { _, _ ->

            viewModel.updateConfirm()
        }

        val errorHeaderAdapter = ErrorAdapter()

        val tokenApproveAdapter = TokenApproveAdapter()


        val bottomTransactionInfoAdapter = BottomAdapter(onChainClicked = { _, _ ->


        }, onWalletClicked = { _, _ ->

//            offerNavEvent(PickWalletEvent(keyRequestPickWallet, viewModel.currentWallet.value?.address ?: ""))
        })

        val headerTransactionInfoAdapter = HeaderAdapter()


        adapter = MultiAdapter(
            spaceAdapter,
            keyValueAdapter,
            messageInfoAdapter,
            errorHeaderAdapter,
            tokenApproveAdapter,
            headerTransactionInfoAdapter,
            bottomTransactionInfoAdapter,
        ).apply {

            setRecyclerView(binding.recyclerView)
        }

        binding.recyclerView.itemAnimator = null
    }

    private fun observeData() = with(viewModel) {

        buttonState.observeQueue(viewLifecycleOwner, tag = this@ApprovalConnectConfirmFragment.javaClass.name + "buttonState") { state ->

            val bindingAction = bindingAction ?: return@observeQueue

            bindingAction.root.postAwait()

            if (state != ApproveConnectConfirmViewModel.ButtonState.REQUEST_DETECT_LOADING) bindingAction.root.beginTransitionAwait(AutoTransition().setDuration(350).setOrdering(ORDERING_TOGETHER)) {

                bindButtonState(state)
            } else {

                bindButtonState(state)
            }
        }

        rejectConnectState.observeLaunch(viewLifecycleOwner) { state ->

            if (!state.isStart()) {

                dismiss()
                return@observeLaunch
            }
//
//            if (state is ResultState.Failed && state.cause is AppExceptionV2) if (state.cause.asObject<AppExceptionV2>().code == ErrorCode.REQUEST_DETECT_FAILED) {
//
//                dismiss()
//                return@observeLaunch
//            }
        }

        approveConnectState.observeLaunch(viewLifecycleOwner) { state ->

            if (state.isSuccess()) {

                dismiss()
                return@observeLaunch
            }


            if (state is ResultState.Failed && state.cause is AppException) if (state.cause.asObject<AppException>().code == ApproveConnectConfirmViewModel.ErrorCode.PLEASE_CONFIRM) {

                val index = adapter?.currentList?.indexOfFirst { it is MessageViewItem }?.takeIf { it >= 0 } ?: return@observeLaunch

                val itemView = binding?.recyclerView?.findViewHolderForAdapterPosition(index)?.itemView ?: return@observeLaunch

                itemView.scaleAnimAwait(1.1f, 500, true)

                return@observeLaunch
            }
        }

        viewItemListDisplay.observeQueue(viewLifecycleOwner, tag = this@ApprovalConnectConfirmFragment.javaClass.name) {

            val binding = binding ?: return@observeQueue

            binding.recyclerView.submitListAwait(it)

            bottomSheet.asObject<ViewGroup>().beginTransitionAwait(TransitionSet().setDuration(350).addTransition(ChangeBounds()).addTransition(Fade()))
        }
    }

//    private fun observeChainData() = with(chainViewModelV2) {
//
//        currentChain.observe(viewLifecycleOwner) {
//
//            viewModel.updateCurrentChain(it)
//        }
//    }
//
//    private fun observeWalletData() = with(walletViewModel) {
//
//        currentWallet.observe(viewLifecycleOwner) {
//
//            viewModel.updateCurrentWallet(it)
//        }
//    }
//
//    private fun observerCurrencyData() = with(settingViewModel) {
//
//        currencyInfo.observe(viewLifecycleOwner) {
//
//            viewModel.updateCurrency(it)
//        }
//    }

    private fun bindButtonState(state: Enum<*>) {

        val bindingAction = bindingAction ?: return

        bindingAction.tvPositive.isClickable = state in listOf(ApproveConnectConfirmViewModel.ButtonState.REVIEW)
        bindingAction.tvPositive.isVisible = when (state) {
            in listOf(ApproveConnectConfirmViewModel.ButtonState.APPROVE_LOADING, ApproveConnectConfirmViewModel.ButtonState.REQUEST_DETECT_LOADING, ApproveConnectConfirmViewModel.ButtonState.REVIEW) -> true
            else -> false
        }
        bindingAction.tvPositive.alpha = when (state) {
            in listOf(ApproveConnectConfirmViewModel.ButtonState.APPROVE_LOADING, ApproveConnectConfirmViewModel.ButtonState.REJECT_LOADING, ApproveConnectConfirmViewModel.ButtonState.REVIEW) -> 1f
            else -> 0.2f
        }
        bindingAction.tvPositive.text = when (state) {
            ApproveConnectConfirmViewModel.ButtonState.APPROVE_LOADING -> getString(R.string.message_confirming)
            ApproveConnectConfirmViewModel.ButtonState.WATCH_WALLET -> getString(R.string.action_not_support)
            else -> getString(R.string.action_confirm)
        }
        bindingAction.progressPositive.setVisible(state in listOf(ApproveConnectConfirmViewModel.ButtonState.APPROVE_LOADING, ApproveConnectConfirmViewModel.ButtonState.REQUEST_DETECT_LOADING))


        bindingAction.tvNegative.isClickable = state in listOf(ApproveConnectConfirmViewModel.ButtonState.REQUEST_DETECT_FAILED, ApproveConnectConfirmViewModel.ButtonState.REVIEW)
        bindingAction.tvNegative.isVisible = when (state) {
            in listOf(
                ApproveConnectConfirmViewModel.ButtonState.REQUEST_DETECT_FAILED,
                ApproveConnectConfirmViewModel.ButtonState.REJECT_LOADING,
                ApproveConnectConfirmViewModel.ButtonState.REQUEST_DETECT_LOADING,
                ApproveConnectConfirmViewModel.ButtonState.REVIEW
            ) -> true

            else -> false
        }
        bindingAction.tvNegative.text = when (state) {
            ApproveConnectConfirmViewModel.ButtonState.REQUEST_DETECT_FAILED -> getString(R.string.action_cancel)
            else -> getString(R.string.action_reject)
        }
        bindingAction.progressNegative.setVisible(state in listOf(ApproveConnectConfirmViewModel.ButtonState.REJECT_LOADING))
    }

    private fun updateLocationAction() {

        val binding = binding ?: return

        val bottomSheet = bottomSheet ?: return

        val bindingAction = bindingAction ?: return

        val bottom = binding.recyclerView.top + (binding.recyclerView.children.firstOrNull()?.bottom ?: 0)

        val transaction = (bottomSheet.parent as View).top + bottomSheet.top + bottom
        val transactionYMin = transaction - bindingAction.root.top + 0f

        bindingAction.root.translationY = maxOf(0f, transactionYMin)
    }

    companion object {


        fun newInstance(keyRequest: String = "", pair: String? = null, slide: Request.Slide? = null) = ApprovalConnectConfirmFragment().apply {

            arguments = bundleOf(

                KEY_REQUEST to keyRequest,

                PAYLOAD_PAIR to pair,
                PAYLOAD_SLIDE to slide?.value
            )
        }
    }
}
