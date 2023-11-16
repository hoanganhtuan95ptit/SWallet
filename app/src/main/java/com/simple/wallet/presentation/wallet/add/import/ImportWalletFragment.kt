package com.simple.wallet.presentation.wallet.add.import

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.ui.base.fragments.BaseFragment
import com.simple.coreapp.utils.AppException
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.getSerializableOrNull
import com.simple.coreapp.utils.extentions.clear
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.getViewModel
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.coreapp.utils.extentions.haveText
import com.simple.coreapp.utils.extentions.text
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.navigation.utils.ext.setNavigationResultListener
import com.simple.state.ResultState
import com.simple.state.isSuccess
import com.simple.state.toSuccess
import com.simple.wallet.DATA
import com.simple.wallet.KEY_REQUEST
import com.simple.wallet.PARAM_ACTION
import com.simple.wallet.PARAM_SCAN
import com.simple.wallet.R
import com.simple.wallet.domain.entities.scan.ScanData
import com.simple.wallet.presentation.wallet.add.AddWalletViewModel
import com.simple.wallet.presentation.wallet.add.import.ImportWalletFragment.Companion.REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET
import com.simple.wallet.theme.JetchatTheme
import com.simple.wallet.utils.exts.NextView
import com.simple.wallet.utils.exts.dashedBorder
import com.simple.wallet.utils.exts.navigationBarPadding
import com.simple.wallet.utils.exts.statusBarPadding
import com.simple.wallet.utils.exts.takeIfNotBlank

class ImportWalletFragment : BaseFragment() {


    private val scanString: String? by lazy {
        arguments?.getString(PARAM_SCAN)
    }

    private val viewModel by lazy {
        getViewModel(this, ImportWalletViewModel::class)
    }

    private val addWalletViewModel: AddWalletViewModel by lazy {
        getViewModelGlobal(AddWalletViewModel::class)
    }


    private val clipboardHasData = mutableStateOf(false)


    private var clipboard by autoCleared<ClipboardManager>()

    private var onPrimaryClipChangedListener by autoCleared<ClipboardManager.OnPrimaryClipChangedListener>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onDetach() {
        super.onDetach()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = ComposeView(inflater.context).apply {

        setContent {
            JetchatTheme {
                ContentView(
                    scanData = scanString ?: "",
                    viewModel = viewModel,
                    clipboardHasData = clipboardHasData,
                    addWalletViewModel = addWalletViewModel,
                    onScanClick = {

                        offerDeepLink("/camera?$KEY_REQUEST=$REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET&$PARAM_ACTION=scan")
                    },
                    onBackClick = {

                        dismiss()
                    }, onNextClick = {

                        dismiss()

                        offerDeepLink("/confirm-wallet")
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        onPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {

            clipboardHasData.value = clipboard?.haveText() == true
        }
    }

    override fun onResume() {
        super.onResume()

        view?.post {

            clipboardHasData.value = clipboard?.haveText() == true
        }

        clipboard?.addPrimaryClipChangedListener(onPrimaryClipChangedListener)
    }

    override fun onPause() {
        super.onPause()

        clipboard?.removePrimaryClipChangedListener(onPrimaryClipChangedListener)
    }

    companion object {

        const val REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET = "REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET"
    }
}

@Composable
private fun ContentView(
    scanData: String = "",
    viewModel: ImportWalletViewModel,
    clipboardHasData: MutableState<Boolean>,
    addWalletViewModel: AddWalletViewModel,
    onScanClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
) {

    val walletKey = rememberSaveable { mutableStateOf("") }

    val walletName = rememberSaveable { mutableStateOf("") }

    val walletNameDefault: String = stringResource(id = R.string.wallet_name)


    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarPadding()
    ) {


        Image(
            painter = painterResource(R.drawable.ic_back_on_background_24dp),
            modifier = Modifier
                .padding(start = 8.dp)
                .width(40.dp)
                .height(56.dp)
                .padding(8.dp)
                .clickable { onBackClick.invoke() },
            contentDescription = null,
        )

        Text(
            text = stringResource(id = R.string.title_import_wallet),
            style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        )

        WalletKey(
            scanData = scanData,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .wrapContentSize()
                .dashedBorder(1.dp, MaterialTheme.colorScheme.outline, 16.dp),
            viewModel = viewModel,
            walletKey = walletKey,
            clipboardHasData = clipboardHasData,
            onScanClick = onScanClick
        )


        val isNextEnable = rememberSaveable { mutableStateOf(false) }

        val isErrorVisible = rememberSaveable { mutableStateOf(false) }


        viewModel.walletType.observe(lifecycleOwner) {

            val visible = it is ResultState.Failed && (it.cause !is AppException || it.cause.asObject<AppException>().code != ImportWalletViewModel.ImportWalletErrorCode.EMPTY)

            isErrorVisible.value = visible

            isNextEnable.value = it.isSuccess()
        }

        ErrorView(
            visible = isErrorVisible.value
        )

        Input(
            hint = stringResource(id = R.string.hint_enter_name_wallet),
            modifier = Modifier
                .padding(start = 16.dp, top = 20.dp, end = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .defaultMinSize(minHeight = 40.dp)
                .dashedBorder(1.dp, MaterialTheme.colorScheme.outline, 16.dp),
            onTextChange = { walletName.value = it }
        )

        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 30.dp)
                .fillMaxSize()
                .navigationBarPadding(),
        ) {

            NextView(
                text = stringResource(id = R.string.action_continue),
                enable = isNextEnable.value
            ) {

                addWalletViewModel.importWallet(walletName.value.takeIfNotBlank() ?: walletNameDefault, walletKey.value, viewModel.walletType.get().toSuccess()!!.data)

                onNextClick()
            }
        }
    }
}

@Composable
private fun WalletKey(
    modifier: Modifier = Modifier,
    viewModel: ImportWalletViewModel,
    scanData: String,
    walletKey: MutableState<String>,
    clipboardHasData: MutableState<Boolean>,
    onScanClick: () -> Unit = {}
) {

    Box(
        modifier = modifier,
    ) {

        val activity = LocalContext.current as Activity


        val text = remember { mutableStateOf(scanData) }

        activity.setNavigationResultListener(REQUEST_SCAN_DATA_WHEN_IMPORT_WALLET) { _, b ->

            val result: ScanData = b.getSerializableOrNull<ScanData>(DATA) ?: return@setNavigationResultListener

            text.value = result.text
        }

        Input(
            hint = stringResource(id = R.string.hint_import_wallet),
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .defaultMinSize(minHeight = 150.dp),
            onTextChange = {

                walletKey.value = it

                viewModel.updateInputKey(it)
            }
        )

        Image(
            painter = painterResource(R.drawable.ic_scan_on_background_24dp),
            modifier = Modifier
                .padding(end = 8.dp)
                .size(40.dp)
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clickable { onScanClick() },
            contentDescription = null,
        )

        val isCopyVisible by clipboardHasData

        if (isCopyVisible) Image(
            painter = painterResource(R.drawable.ic_copy_accent_24dp),
            modifier = Modifier
                .padding(end = 48.dp)
                .size(40.dp)
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clickable {

                    val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                    val it = clipboardManager
                        .text()
                        .toString()

                    clipboardManager.clear()


                    text.value = it

                    walletKey.value = it

                    viewModel.updateInputKey(it)
                },
            contentDescription = null,
        )
    }
}

@Composable
private fun ErrorView(
    visible: Boolean,
) {

    AnimatedVisibility(
        visible = visible,
    ) {

        Text(
            text = stringResource(id = R.string.message_key_invalid),
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Light),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Input(
    modifier: Modifier = Modifier,
    hint: String = "",
    text: MutableState<String> = remember { mutableStateOf("") },
    onTextChange: (String) -> Unit = {}
) {

    var textWrap by text

    TextField(
        value = textWrap,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = modifier,
        placeholder = {

            Text(
                text = hint,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
        },
        onValueChange = {
            textWrap = it
            onTextChange(it)
        },
    )
}

class ImportWalletProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/import-wallet"
    }

    override fun provideScope(deepLink: String): Class<*> {

        return Activity::class.java
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return ImportWalletFragment().apply {

            arguments = bundleOf(*params.toList().toTypedArray())
        }
    }
}