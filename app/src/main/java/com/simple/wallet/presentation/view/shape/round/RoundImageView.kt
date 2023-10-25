package com.simple.wallet.presentation.view.shape.round

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.simple.wallet.R

class RoundedImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr), RoundView {

    override val path = Path()

    override var rectF: RectF = RectF()

    override var listRadius: FloatArray = floatArrayOf()


    override var clipPadding: Boolean = false


    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyleAttr, 0)


        this.clipPadding = typedArray.getBoolean(R.styleable.RoundedImageView_android_clipToPadding, true)

        val radius = typedArray.getDimension(R.styleable.RoundedImageView_radius, 0f)

        val topLeftRadius = typedArray.getDimension(R.styleable.RoundedImageView_topLeftRadius, radius)
        val topRightRadius = typedArray.getDimension(R.styleable.RoundedImageView_topRightRadius, radius)
        val bottomLeftRadius = typedArray.getDimension(R.styleable.RoundedImageView_bottomLeftRadius, radius)
        val bottomRightRadius = typedArray.getDimension(R.styleable.RoundedImageView_bottomRightRadius, radius)

        listRadius = floatArrayOf(topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius)

        typedArray.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        updateRect()

        refresh()
    }

    override fun onDraw(canvas: Canvas) {

        canvas.clipPath(path)

        super.onDraw(canvas)
    }
}