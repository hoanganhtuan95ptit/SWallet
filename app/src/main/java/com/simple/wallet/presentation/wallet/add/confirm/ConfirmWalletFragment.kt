package com.simple.wallet.presentation.wallet.add.confirm

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.simple.coreapp.ui.base.fragments.BaseFragment
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.coreapp.utils.extentions.observeLaunch
import com.simple.navigation.NavigationProvider
import com.simple.state.doSuccess
import com.simple.state.isSuccess
import com.simple.wallet.R
import com.simple.wallet.presentation.wallet.add.AddWalletViewModel
import com.simple.wallet.presentation.wallet.add.import.ImportWalletFragment
import com.simple.wallet.theme.JetchatTheme
import com.simple.wallet.utils.exts.LottieView
import com.simple.wallet.utils.exts.NextView
import com.simple.wallet.utils.exts.TextFormat
import com.simple.wallet.utils.exts.navigationBarPadding
import com.simple.wallet.utils.exts.statusBarPadding
import com.simple.wallet.utils.exts.uppercaseFirst


class ConfirmWalletFragment : BaseFragment() {

    private val viewModel: AddWalletViewModel by lazy {
        getViewModelGlobal(AddWalletViewModel::class)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = ComposeView(inflater.context).apply {

        setContent {
            JetchatTheme {
                ContentView(viewModel = viewModel) {

                    dismiss()
                }
            }
        }
    }
}

@Composable
private fun ContentView(
    viewModel: AddWalletViewModel,
    onNextClick: () -> Unit = {},
) {

    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)

    Box {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LottieView(
                resId = R.raw.anim_add_wallet_congratulations_v2,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .wrapContentWidth()
                    .height(200.dp),
                alignment = Alignment.BottomCenter
            )


            val walletName = remember { mutableStateOf("") }

            val walletAddress = remember { mutableStateOf("") }


            val isNextEnable = rememberSaveable { mutableStateOf(false) }


            viewModel.addWalletStateEvent.observeLaunch(lifecycleOwner) { event ->

                val state = event.getContentIfNotHandled() ?: return@observeLaunch

                state.doSuccess {

                    walletName.value = it.name.uppercaseFirst()

                    walletAddress.value = it.addressMap.toList().first().first
                }

                isNextEnable.value = state.isSuccess()
            }

            TextFormat(
                modifier = Modifier
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.message_add_wallet_congratulations, "`${walletName.value}`"),
                style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                textAlign = TextAlign.Center,
            )

            Text(
                modifier = Modifier
                    .padding(top = 12.dp),
                text = walletAddress.value,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center,
            )

            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 30.dp)
                    .fillMaxSize()
                    .navigationBarPadding(),
            ) {

                NextView(
                    text = stringResource(id = R.string.action_finish),
                    enable = isNextEnable.value,
                    onClick = onNextClick
                )
            }
        }

        LottieView(
            resId = R.raw.anim_congratulations,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            alignment = Alignment.TopCenter
        )
    }
}

class ConfirmWalletProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/confirm-wallet"
    }

    override fun provideListScope(deepLink: String): List<Class<*>> {

        return listOf(Activity::class.java, ImportWalletFragment::class.java)
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return ConfirmWalletFragment()
    }
}