package com.simple.wallet.utils.exts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.NextView(
    text: String,
    enable: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .align(Alignment.BottomEnd)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)
            .clickable(enabled = enable, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
