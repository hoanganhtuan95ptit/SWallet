package com.simple.wallet.utils.exts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun LottieView(
    resId: Int,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))

    LottieAnimation(
        modifier = modifier,
        alignment = alignment,
        iterations = LottieConstants.IterateForever,
        composition = composition,
    )
}
