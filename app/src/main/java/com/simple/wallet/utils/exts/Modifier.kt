package com.simple.wallet.utils.exts

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.simple.coreapp.utils.extentions.doOnHeightNavigationChange
import com.simple.coreapp.utils.extentions.doOnHeightStatusChange


fun Modifier.statusBarPadding() = composed {

    var statusBarHeight by rememberSaveable { mutableIntStateOf(0) }

    (LocalContext.current as Activity).doOnHeightStatusChange {

        if (statusBarHeight != it) statusBarHeight = it
    }

    val statusBarHeightDp = with(LocalDensity.current) {

        statusBarHeight.toDp()
    }

    padding(top = statusBarHeightDp)
}

fun Modifier.navigationBarPadding() = composed {

    var navigationBarHeight by rememberSaveable { mutableIntStateOf(0) }

    (LocalContext.current as Activity).doOnHeightNavigationChange {

        if (navigationBarHeight != it) navigationBarHeight = it
    }

    val navigationBarHeightDp = with(LocalDensity.current) {

        navigationBarHeight.toDp()
    }

    padding(bottom = navigationBarHeightDp)
}

fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(factory = {

    val density = LocalDensity.current

    val strokeWidthPx = density.run { strokeWidth.toPx() }

    val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

    drawWithCache {

        onDrawBehind {

            val stroke = Stroke(
                width = strokeWidthPx,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            drawRoundRect(
                color = color,
                style = stroke,
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
        }
    }
})
