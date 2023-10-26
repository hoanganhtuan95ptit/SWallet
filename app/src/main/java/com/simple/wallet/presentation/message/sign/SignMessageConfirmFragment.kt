package com.simple.wallet.presentation.message.sign

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
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.adapter.MultiAdapter
import com.simple.core.utils.extentions.asObject
import com.simple.core.utils.extentions.asObjectOrNull
import com.simple.core.utils.extentions.resumeActive
import com.simple.coreapp.ui.adapters.SpaceAdapter
import com.simple.coreapp.ui.base.fragments.BaseViewModelSheetFragment
import com.simple.coreapp.ui.dialogs.OptionFragment.Companion.KEY_REQUEST
import com.simple.coreapp.utils.AppException
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.getSerializableOrNull
import com.simple.coreapp.utils.ext.getStringOrEmpty
import com.simple.coreapp.utils.extentions.doOnHeightNavigationChange
import com.simple.coreapp.utils.extentions.observeLaunch
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setVisible
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.vibrate
import com.simple.navigation.domain.entities.NavigationEvent
import com.simple.state.ResultState
import com.simple.state.isFailed
import com.simple.state.isStart
import com.simple.state.isSuccess
import com.simple.state.toSuccess
import com.simple.wallet.PARAM_DATA
import com.simple.wallet.R
import com.simple.wallet.databinding.LayoutActionConfirmBinding
import com.simple.wallet.databinding.PopupListBinding
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.presentation.adapters.BottomAdapter
import com.simple.wallet.presentation.adapters.HeaderAdapter
import com.simple.wallet.presentation.adapters.KeyValueAdapter
import com.simple.wallet.presentation.adapters.MessageAdapter
import com.simple.wallet.presentation.adapters.MessageViewItem
import com.simple.wallet.presentation.adapters.TokenApproveAdapter
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf


class SignMessageConfirmFragment : BaseViewModelSheetFragment<PopupListBinding, SignMessageConfirmViewModel>() {


    private val keyRequest: String by lazy {

        requireArguments().getStringOrEmpty(KEY_REQUEST)
    }

    private val request: Request by lazy {

        requireArguments().getSerializableOrNull(PARAM_DATA)!!
    }


    private val chainId: Long by lazy {

        request.message?.chainId ?: Chain.ALL_NETWORK
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

    override fun dismiss() {
//
//        val state = viewModel.signMessageState.value
//
//        val resultData = state?.toSuccess()?.data ?: state?.toFailed()?.message ?: ""
//
//        val resultStatus = state?.isSuccess() == true
//
//        setNavigationResult(keyRequest, bundleOf(PARAM_DATA to viewModel.mRequest, PARAM_RESULT_STATUS to resultStatus, PARAM_RESULT_DATA to resultData))

        super.dismiss()
    }

    override fun getParameter(): ParametersDefinition {

        return { parametersOf(request) }
    }

    private fun setupCancel() {

        val binding = bindingAction ?: return

        binding.tvNegative.setOnClickListener {

            dismiss()
        }
    }

    private fun setupConfirm() {

        val binding = bindingAction ?: return

        binding.tvPositive.setDebouncedClickListener(anim = true) {

            viewModel.signMessage()
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

        val binding = binding ?: return

        doOnHeightNavigationChange {

            bindingAction?.root?.updatePadding(left = 30.toPx(), bottom = it + 30.toPx(), right = 30.toPx())

            bindingAction?.root?.post {

                binding.root.updatePadding(bottom = bindingAction!!.root.height)
            }
        }
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val spaceAdapter = SpaceAdapter()

        val keyValueAdapter = KeyValueAdapter(onKeyItemClick = {

        }, onValueItemClick = {

        })

        val messageInfoAdapter = MessageAdapter { view, item ->

            viewModel.updateConfirm()
        }

        val tokenApproveAdapter = TokenApproveAdapter()


        val bottomAdapter = BottomAdapter()

        val headerAdapter = HeaderAdapter()


        adapter = MultiAdapter(
            spaceAdapter,
            headerAdapter,
            bottomAdapter,
            keyValueAdapter,
            messageInfoAdapter,
            tokenApproveAdapter,
        ).apply {

            setRecyclerView(binding.recyclerView)
        }

        binding.recyclerView.itemAnimator = null
    }

    private fun observeData() = with(viewModel) {

        buttonState.observe(viewLifecycleOwner) { state ->

            val bindingAction = bindingAction ?: return@observe


            val buttonState = state.toSuccess()?.data

            bindingAction.tvPositive.isClickable = buttonState == SignMessageConfirmViewModel.ButtonState.REVIEW_TRANSACTION
            bindingAction.tvPositive.alpha = if (!bindingAction.tvPositive.isClickable) 0.2f else 1f


            bindingAction.progressPositive.isVisible = state.isStart()


            bindingAction.tvPositive.text = when (state.toSuccess()?.data) {

                SignMessageConfirmViewModel.ButtonState.WATCH_WALLET -> getString(R.string.action_not_support)

                else -> getString(R.string.action_confirm)
            }
        }

        signMessageState.observeLaunch(viewLifecycleOwner) {

            if (it.isSuccess()) {

                dismiss()
                return@observeLaunch
            }


            if (it is ResultState.Failed && it.cause is AppException) if (it.cause.asObject<AppException>().code == SignMessageConfirmViewModel.TransactionCode.PLEASE_CONFIRM) {

                val index = adapter?.currentList?.indexOfFirst { it is MessageViewItem }?.takeIf { it >= 0 } ?: return@observeLaunch

                val itemView = binding?.recyclerView?.findViewHolderForAdapterPosition(index)?.itemView ?: return@observeLaunch

                suspendCancellableCoroutine { a ->

                    itemView.animate().setDuration(300).scaleX(1.1f).scaleY(1.1f).withEndAction {
                        a.resumeActive(true)
                    }.start()
                }

                suspendCancellableCoroutine { a ->

                    itemView.animate().setDuration(300).scaleX(1f).scaleY(1f).withEndAction {
                        a.resumeActive(true)
                    }.start()
                }


                return@observeLaunch
            }


            val bindingAction = bindingAction ?: return@observeLaunch

            bindingAction.tvNegative.isClickable = it.isFailed()
            bindingAction.tvPositive.isClickable = it.isFailed()

            bindingAction.progressPositive.setVisible(it.isStart())
            bindingAction.tvNegative.setVisible(!it.isStart())

            TransitionManager.beginDelayedTransition(bindingAction.root, TransitionSet().setDuration(350).addTransition(ChangeBounds()).addTransition(Fade()))
        }

        viewItemListDisplay.observeLaunch(viewLifecycleOwner) {

            val bottomSheet = bottomSheet ?: return@observeLaunch

            adapter?.submitList(it) {

                TransitionManager.beginDelayedTransition(bottomSheet.parent.asObjectOrNull<ViewGroup>()!!, TransitionSet().setDuration(350).addTransition(ChangeBounds()).addTransition(Fade()))
            }
        }
    }
//
//    private fun observeChainData() = with(chainViewModelV2) {
//
//        chainIdAndChain.observe(viewLifecycleOwner) {
//
//            viewModel.updateCurrentChain(it[chainId] ?: return@observe)
//        }
//
//        chainIdAndNativeToken.observe(viewLifecycleOwner) {
//
//            viewModel.updateNativeToken(it[chainId] ?: return@observe)
//        }
//    }
//
//    private fun observeWalletData() = with(walletViewModel) {
//
//        walletList.observe(viewLifecycleOwner) { list ->
//
//            binding ?: return@observe
//
//            val walletAddress = this@SignMessageConfirmFragment.request.getWalletAddressOrNull() ?: ""
//
//            val wallet = list.find { it.address.equals(walletAddress, true) } ?: walletAddress.toWallet()
//
//            viewModel.updateCurrentWallet(wallet)
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

        fun newInstance(keyRequest: String, data: Request) = SignMessageConfirmFragment().apply {

            arguments = bundleOf(

                KEY_REQUEST to keyRequest,

                PARAM_DATA to data,
            )
        }
    }
}

data class SignMessageEvent(val keyRequest: String, val data: Request) : NavigationEvent() {

    override fun provideFragment(): Fragment {

        return SignMessageConfirmFragment.newInstance(keyRequest, data)
    }
}