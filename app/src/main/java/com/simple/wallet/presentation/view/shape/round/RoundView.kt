package com.simple.wallet.presentation.view.shape.round

import android.graphics.Path
import android.graphics.RectF
import android.view.View
import com.simple.wallet.presentation.view.shape.ShapeView

interface RoundView : ShapeView {

    val rectF: RectF

    var listRadius: FloatArray


    var clipPadding: Boolean


    fun updateRect() {

        if (this !is View) {

            error("not support it")
        }

        if (clipPadding) {

            rectF.set(0f + paddingLeft, 0f + paddingTop, width.toFloat() - paddingRight, height.toFloat() - paddingBottom)
        } else {

            rectF.set(0f, 0f, width.toFloat(), height.toFloat())
        }
    }


    fun setRadius(radius: Float) {

        setRadius(radius, radius, radius, radius)
    }

    fun setRadius(topLeftRadius: Float, topRightRadius: Float, bottomRightRadius: Float, bottomLeftRadius: Float) {

        listRadius = floatArrayOf(topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius)

        refresh()
    }

    override fun generatePath(): Path {

        val path = Path()

        path.addRoundRect(rectF, listRadius, Path.Direction.CW)

        return path
    }
}