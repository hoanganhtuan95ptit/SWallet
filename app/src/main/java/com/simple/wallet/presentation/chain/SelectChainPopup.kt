package com.simple.wallet.presentation.chain

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.ui.base.fragments.BaseSheetFragment
import com.simple.coreapp.ui.dialogs.OptionFragment.Companion.KEY_REQUEST
import com.simple.coreapp.utils.extentions.getViewModel
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.setNavigationResult
import com.simple.wallet.DATA
import com.simple.wallet.presentation.chain.adapters.SelectChainViewItem
import com.simple.wallet.theme.JetchatTheme
import com.simple.wallet.utils.exts.uppercaseFirst
import org.koin.core.parameter.parametersOf

class SelectChainPopup : BaseSheetFragment() {

    private val viewModel: SelectChainViewModel by lazy {
        getViewModel(SelectChainViewModel::class, parameters = { parametersOf(arguments?.getString(CHAIN_ID)?.toLongOrNull() ?: 0L, arguments?.getString(IS_SUPPORT_ALL_CHAIN)?.toBooleanStrictOrNull() ?: true) })
    }

    private var viewItem: SelectChainViewItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = ComposeView(inflater.context).apply {

        setContent {
            JetchatTheme {
                ContentView(viewModel) {

                    viewItem = it
                    dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val viewItem = viewItem ?: return

        setNavigationResult(arguments?.getString(KEY_REQUEST) ?: "", bundleOf(DATA to viewItem.data))
    }

    companion object {

        private const val CHAIN_ID = "chainId"

        private const val IS_SUPPORT_ALL_CHAIN = "isSupportAllChain"
    }
}

@Composable
private fun ContentView(
    viewModel: SelectChainViewModel,
    onClick: (SelectChainViewItem) -> Unit
) {


    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .size(width = 40.dp, height = 4.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp))
        )


        val viewItemList = rememberSaveable { mutableStateOf(emptyList<ViewItemCloneable>()) }


        viewModel.chainViewItemList.observe(lifecycleOwner) {

            viewItemList.value = it
        }

        ContentMainView(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            viewItemList = viewItemList.value
        )
    }
}


@Composable
private fun ContentMainView(
    onClick: (SelectChainViewItem) -> Unit,
    modifier: Modifier = Modifier,
    viewItemList: List<ViewItemCloneable>
) {

    LazyColumn(
        modifier = modifier
    ) {

        for (viewItem in viewItemList) when (viewItem) {

            is SelectChainViewItem -> item {

                ChainViewItem(viewItem, onClick = onClick)
            }

            else -> {

            }
        }

        item {

            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(50.dp)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ChainViewItem(
    viewItem: SelectChainViewItem,
    onClick: (SelectChainViewItem) -> Unit,
) {

    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable(onClick = { onClick.invoke(viewItem) }),
        verticalAlignment = Alignment.CenterVertically
    ) {

        GlideImage(
            model = viewItem.image.getImage(context),
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(150.dp)),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = viewItem.name.getString(context).toString().uppercaseFirst(),
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Light),
        )
    }
}

class SelectChainProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/select-chain"
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return SelectChainPopup().apply {

            arguments = bundleOf(*params.toList().toTypedArray())
        }
    }
}