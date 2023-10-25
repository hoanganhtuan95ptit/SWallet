package com.simple.wallet.presentation.view.shape

import android.graphics.Path
import android.graphics.RectF
import android.view.View

interface ShapeView {

    val path: Path

    fun refresh() {

        if (this !is View) {
            return
        }

        if (height <= 0 || width <= 0) {
            return
        }

        path.reset()

        path.addPath(generatePath())

        postInvalidate()
    }

    fun generatePath(): Path
}