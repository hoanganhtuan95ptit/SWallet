package com.simple.wallet.presentation.wallet.add

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.ui.base.fragments.BaseSheetFragment
import com.simple.coreapp.utils.extentions.getViewModelGlobal
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.wallet.presentation.adapters.HeaderViewItemV2
import com.simple.wallet.presentation.wallet.add.adapters.OptionViewItem
import com.simple.wallet.theme.JetchatTheme
import com.simple.wallet.utils.exts.dashedBorder
import com.simple.wallet.utils.exts.launchCollect

class AddWalletFragment : BaseSheetFragment() {

    val viewModel: AddWalletViewModel by lazy {
        getViewModelGlobal(AddWalletViewModel::class)
    }

    private var viewItem: OptionViewItem? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = ComposeView(inflater.context).apply {

        viewModel.viewItemList.launchCollect(viewLifecycleOwner.lifecycleScope) { viewItemList ->

            setContent {
                JetchatTheme {
                    ContentView(viewItemList) {

                        viewItem = it
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val viewItem = viewItem ?: return

        offerDeepLink(viewItem.id)
    }
}

@Composable
private fun ContentView(
    viewItemList: List<ViewItemCloneable>,
    onClick: (OptionViewItem) -> Unit
) {

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

        ContentMainView(
            onClick = onClick,
            modifier = Modifier
                .padding(top = 24.dp),
            viewItemList = viewItemList
        )
    }
}

@Composable
private fun ContentMainView(
    onClick: (OptionViewItem) -> Unit,
    modifier: Modifier = Modifier,
    viewItemList: List<ViewItemCloneable>
) {

    LazyColumn(
        modifier = modifier
    ) {

        for (viewItemCloneable in viewItemList) when (viewItemCloneable) {

            is HeaderViewItemV2 -> item {

                HeaderView(viewItemCloneable.logo, stringResource(id = viewItemCloneable.title), stringResource(id = viewItemCloneable.caption))
            }

            is OptionViewItem -> item {

                OptionView(viewItemCloneable, onClick)
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
private fun HeaderView(
    logo: String,
    title: String,
    caption: String
) {

    Column(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(70.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {

            GlideImage(
                model = logo,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        }

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(top = 20.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 8.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OptionView(
    viewItem: OptionViewItem,
    onClick: (OptionViewItem) -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable(interactionSource = interactionSource, indication = null, onClick = { onClick.invoke(viewItem) })
            .scaleOnPress(interactionSource)
            .dashedBorder(1.dp, MaterialTheme.colorScheme.outline, 16.dp)
            .padding(vertical = 16.dp),
    ) {

        Image(
            painter = painterResource(viewItem.image),
            modifier = Modifier
                .padding(start = 16.dp)
                .size(28.dp),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = stringResource(id = viewItem.title),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = viewItem.caption),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 8.dp),
                fontSize = 14.sp,
            )
        }
    }
}

private fun Modifier.scaleOnPress(
    interactionSource: InteractionSource
) = composed {

    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        if (isPressed) {
            0.95f
        } else {
            1f
        }, label = ""
    )

    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

class AddWalletProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/add-wallet"
    }

    override fun provideScope(deepLink: String): Class<*> {

        return Activity::class.java
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return AddWalletFragment()
    }
}