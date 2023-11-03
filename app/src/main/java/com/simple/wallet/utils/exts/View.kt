package com.simple.wallet.utils.exts

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

fun View.getBitmap(): Bitmap? {

    if (width <= 0 || height <= 0) {

        return null
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)

    layout(left, top, right, bottom)

    draw(canvas)

    return bitmap
}