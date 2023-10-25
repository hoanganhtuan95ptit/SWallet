package com.simple.wallet.presentation.view.shape.round

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.simple.wallet.R


class RoundFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RoundView {

    override val path = Path()

    override var rectF: RectF = RectF()

    override var listRadius: FloatArray = floatArrayOf()


    override var clipPadding: Boolean = false


    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundConstraintLayout, defStyleAttr, 0)


        this.clipPadding = typedArray.getBoolean(R.styleable.RoundConstraintLayout_android_clipToPadding, true)

        val radius = typedArray.getDimension(R.styleable.RoundConstraintLayout_radius, 0f)

        val topLeftRadius = typedArray.getDimension(R.styleable.RoundConstraintLayout_topLeftRadius, radius)
        val topRightRadius = typedArray.getDimension(R.styleable.RoundConstraintLayout_topRightRadius, radius)
        val bottomLeftRadius = typedArray.getDimension(R.styleable.RoundConstraintLayout_bottomLeftRadius, radius)
        val bottomRightRadius = typedArray.getDimension(R.styleable.RoundConstraintLayout_bottomRightRadius, radius)

        listRadius = floatArrayOf(topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius)

        typedArray.recycle()
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        updateRect()

        refresh()
    }

    override fun dispatchDraw(canvas: Canvas) {

        val count = canvas.save()

        canvas.clipPath(path)

        super.dispatchDraw(canvas)

        canvas.restoreToCount(count)
    }
}