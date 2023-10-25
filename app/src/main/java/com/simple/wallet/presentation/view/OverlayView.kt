package com.simple.wallet.presentation.view

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.simple.coreapp.utils.extentions.animation
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.*

class OverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCornerPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    private val pointCenter = PointF()


    private val pointCenterMin = PointF()
    private val pointCenterMax = PointF()

    private val pointRoundMin = PointF()
    private val pointRoundMax = PointF()


    private val points = CopyOnWriteArrayList<PointF>()


    private var pointSelected: PointF? = null


    private var qrcode: Boolean? = null

    private var canTouch: Boolean = true

    private var valueAnimator: ValueAnimator? = null


    private var downX = 0f
    private var downY = 0f


    init {
        mLinePaint.strokeWidth = 2f
        mLinePaint.color = Color.WHITE
        mLinePaint.style = Paint.Style.FILL

        mCornerPaint.strokeWidth = 15f
        mCornerPaint.style = Paint.Style.STROKE

        mBackgroundPaint.color = Color.parseColor("#99000000")

        mCornerPaint.color = Color.WHITE
//        val ta = context.obtainStyledAttributes(attrs, R.styleable.OverlayView)
//        mCornerPaint.color = ta.getColor(R.styleable.OverlayView_roundColor, Color.WHITE)
//        ta.recycle()
    }

    open fun getPoint(): List<PointF> {
        return points
    }

    fun setQrcode(qrcode: Boolean) {
        this.qrcode = qrcode

        updateScope()
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        updateScope()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (!canTouch || qrcode != false) {

            pointSelected = null
            return false
        }

        if (event?.action == MotionEvent.ACTION_DOWN) {

            updatePointSelected(event)

            return pointSelected != null
        } else if (pointSelected != null && event?.action == MotionEvent.ACTION_MOVE) {

            updatePointSelected(event)

            var x = event.rawX - downX
            var y = event.rawY - downY

            when {
                points.indexOf(pointSelected!!) == 0 -> {
                    x = min(x, pointCenterMin.x)
                    x = max(x, pointRoundMin.x)

                    y = min(y, pointCenterMin.y)
                    y = max(y, pointRoundMin.y)
                }

                points.indexOf(pointSelected!!) == 1 -> {
                    x = min(x, pointRoundMax.x)
                    x = max(x, pointCenterMax.x)

                    y = min(y, pointCenterMin.y)
                    y = max(y, pointRoundMin.y)
                }

                points.indexOf(pointSelected!!) == 2 -> {
                    x = min(x, pointRoundMax.x)
                    x = max(x, pointCenterMax.x)

                    y = min(y, pointRoundMax.y)
                    y = max(y, pointCenterMax.y)
                }

                points.indexOf(pointSelected!!) == 3 -> {
                    x = min(x, pointCenterMin.x)
                    x = max(x, pointRoundMin.x)

                    y = min(y, pointRoundMax.y)
                    y = max(y, pointCenterMax.y)
                }
            }

            pointSelected!!.set(x, y)

            update(points, pointCenter, pointSelected!!)

            postInvalidate()

            return true
        } else if (event?.action == MotionEvent.ACTION_UP) {

            pointSelected = null
            return false
        } else {

            return false
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (points.size != 4 || height == 0 || width == 0 || qrcode == null) return

        drawBackground(canvas)

        drawCorner(canvas)
    }


    private fun updateScope() {

        if (measuredHeight < 10 || measuredWidth < 10 || qrcode == null) return

        valueAnimator?.cancel()

        val paddingTop = paddingTop.toFloat()
        val paddingLeft = paddingLeft.toFloat()
        val paddingRight = paddingRight.toFloat()
        val paddingBottom = paddingBottom.toFloat()


        if (points.isEmpty()) {

            points.add(PointF(0F, 0F))
            points.add(PointF(measuredWidth.toFloat(), 0F))
            points.add(PointF(measuredWidth.toFloat(), measuredHeight.toFloat()))
            points.add(PointF(0F, measuredHeight.toFloat()))
        }


        val pointDefaultMin = PointF()
        val pointDefaultMax = PointF()


        if (qrcode == true) {

            pointCenter.set((measuredWidth + paddingLeft - paddingRight) / 2, (measuredHeight + paddingTop - paddingBottom) / 2)

            val space = min(pointCenter.x - paddingLeft, pointCenter.y - paddingTop)
            pointRoundMin.set(pointCenter.x - space, pointCenter.y - space)
            pointRoundMax.set(pointCenter.x + space, pointCenter.y + space)

            pointDefaultMin.set(pointCenter.x - space, pointCenter.y - space)
            pointDefaultMax.set(pointCenter.x + space, pointCenter.y + space)
        } else {

            pointCenter.set((measuredWidth + paddingLeft - paddingRight) / 2, (measuredHeight + paddingTop - paddingBottom) / 2)

            pointRoundMin.set(paddingLeft, paddingTop)
            pointRoundMax.set(measuredWidth - paddingRight, measuredHeight - paddingBottom)

            pointDefaultMin.set(paddingLeft, max(pointCenter.y - 200, paddingTop))
            pointDefaultMax.set(measuredWidth - paddingRight, min(measuredHeight - paddingBottom, pointCenter.y + 200))
        }

        pointCenterMin.set(pointCenter.x - 100, pointCenter.y - 100)
        pointCenterMax.set(pointCenter.x + 100, pointCenter.y + 100)

        valueAnimator = listOf(
            PropertyValuesHolder.ofFloat("x0", points[0].x, pointDefaultMin.x),
            PropertyValuesHolder.ofFloat("y0", points[0].y, pointDefaultMin.y),

            PropertyValuesHolder.ofFloat("x1", points[1].x, pointDefaultMax.x),
            PropertyValuesHolder.ofFloat("y1", points[1].y, pointDefaultMin.y),

            PropertyValuesHolder.ofFloat("x2", points[2].x, pointDefaultMax.x),
            PropertyValuesHolder.ofFloat("y2", points[2].y, pointDefaultMax.y),

            PropertyValuesHolder.ofFloat("x3", points[3].x, pointDefaultMin.x),
            PropertyValuesHolder.ofFloat("y3", points[3].y, pointDefaultMax.y),
        ).animation(onStart = {

            canTouch = false
        }, onUpdate = {

            points.clear()
            points.add(PointF(it.getAnimatedValue("x0") as Float, it.getAnimatedValue("y0") as Float))
            points.add(PointF(it.getAnimatedValue("x1") as Float, it.getAnimatedValue("y1") as Float))
            points.add(PointF(it.getAnimatedValue("x2") as Float, it.getAnimatedValue("y2") as Float))
            points.add(PointF(it.getAnimatedValue("x3") as Float, it.getAnimatedValue("y3") as Float))

            postInvalidate()
        }, onEnd = {

            canTouch = true
        })
    }

    private fun updatePointSelected(event: MotionEvent) {
        val point = findPointNear(PointF(event.x, event.y), points, pointSelected != null)

        if (pointSelected != point && point != null) {
            pointSelected = point
            downX = event.rawX - pointSelected!!.x
            downY = event.rawY - pointSelected!!.y
        }
    }


    private fun drawCorner(canvas: Canvas?) {

        val path = Path()

        path.moveTo(points[0].let { PointF(it.x, it.y + 80) })
        path.addCornerRound(points[0], Corner.TOP_LEFT)
        path.lineTo(points[0].let { PointF(it.x + 80, it.y) })

        path.moveTo(points[1].let { PointF(it.x - 80, it.y) })
        path.addCornerRound(points[1], Corner.TOP_RIGHT)
        path.lineTo(points[1].let { PointF(it.x, it.y + 80) })

        path.moveTo(points[2].let { PointF(it.x, it.y - 80) })
        path.addCornerRound(points[2], Corner.BOTTOM_RIGHT)
        path.lineTo(points[2].let { PointF(it.x - 80, it.y) })

        path.moveTo(points[3].let { PointF(it.x + 80, it.y) })
        path.addCornerRound(points[3], Corner.BOTTOM_LEFT)
        path.lineTo(points[3].let { PointF(it.x, it.y - 80) })

        canvas?.drawPath(path, mCornerPaint)
    }

    private fun drawBackground(canvas: Canvas?) {
        val path = Path()

        path.moveTo(points[0].let { PointF(it.x, it.y + 50) })

        path.addCornerRound(points[0], Corner.TOP_LEFT)
        path.addCornerRound(points[1], Corner.TOP_RIGHT)
        path.addCornerRound(points[2], Corner.BOTTOM_RIGHT)
        path.addCornerRound(points[3], Corner.BOTTOM_LEFT)

        path.lineTo(points[0].let { PointF(it.x, it.y + 50) })

        val width = width.toFloat()
        val height = height.toFloat()

        path.apply {
            lineTo(0f, 0f)
            lineTo(0f, height)
            lineTo(width, height)
            lineTo(width, 0f)
            lineTo(0f, 0f)
            fillType = Path.FillType.EVEN_ODD
        }

        canvas?.drawPath(path, mBackgroundPaint)
    }


    private fun Path.moveTo(point: PointF) {

        moveTo(point.x, point.y)
    }

    private fun Path.lineTo(point: PointF) {

        lineTo(point.x, point.y)
    }

    private fun Path.addCornerRound(point: PointF, corner: Corner) {
        val point1: PointF

        val point2: PointF

        when (corner) {
            Corner.TOP_LEFT -> {
                point1 = point.let { PointF(it.x, it.y + 50) }
                point2 = point.let { PointF(it.x + 50, it.y) }
            }

            Corner.TOP_RIGHT -> {
                point1 = point.let { PointF(it.x - 50, it.y) }
                point2 = point.let { PointF(it.x, it.y + 50) }
            }

            Corner.BOTTOM_RIGHT -> {
                point1 = point.let { PointF(it.x, it.y - 50) }
                point2 = point.let { PointF(it.x - 50, it.y) }
            }

            Corner.BOTTOM_LEFT -> {
                point1 = point.let { PointF(it.x + 50, it.y) }
                point2 = point.let { PointF(it.x, it.y - 50) }
            }
        }

        lineTo(point1)
        cubicTo(point1.x, point1.y, point.x, point.y, point2.x, point2.y)
        lineTo(point2)
    }


    private enum class Corner {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    companion object {

        /**
         * tìm kiếm point gần nhất
         */
        fun findPointNear(point: PointF, points: List<PointF>, touch: Boolean): PointF? {
            var min = Double.MAX_VALUE

            var pointNear: PointF? = null

            for (p in points) {
                var distance = sqrt((point.x - p.x).toDouble().pow(2) + (point.y - p.y).toDouble().pow(2))

                distance = abs(distance)

                if (distance < min && (distance < 100 || touch)) {
                    min = distance

                    pointNear = p
                }
            }

            return pointNear
        }

        /**
         * cập nhật lại
         */
        fun update(points: List<PointF>, pointCenter: PointF, pointMove: PointF) {

            if (points.size != 4) return

            val topLeft = points[0]
            val topRight = points[1]

            val bottomRight = points[2]
            val bottomLeft = points[3]

            when (points.indexOf(pointMove)) {
                0 -> {
                    symmetricalY(pointCenter, topLeft, topRight)
                    symmetricalX(pointCenter, topRight, bottomRight)
                    symmetricalY(pointCenter, bottomRight, bottomLeft)
                }

                1 -> {
                    symmetricalX(pointCenter, topRight, bottomRight)
                    symmetricalY(pointCenter, bottomRight, bottomLeft)
                    symmetricalX(pointCenter, bottomLeft, topLeft)
                }

                2 -> {
                    symmetricalY(pointCenter, bottomRight, bottomLeft)
                    symmetricalX(pointCenter, bottomLeft, topLeft)
                    symmetricalY(pointCenter, topLeft, topRight)
                }

                3 -> {
                    symmetricalX(pointCenter, bottomLeft, topLeft)
                    symmetricalY(pointCenter, topLeft, topRight)
                    symmetricalX(pointCenter, topRight, bottomRight)
                }
            }
        }

        /**
         * đối xứng qua một điểm theo trục Y
         */
        private fun symmetricalY(pointCenter: PointF, pointInput: PointF, pointOutput: PointF) {

            pointOutput.y = pointInput.y
            pointOutput.x = pointCenter.x - pointInput.x + pointCenter.x
        }

        /**
         * đổi xứng qua một điểm theo trục X
         */
        private fun symmetricalX(pointCenter: PointF, pointInput: PointF, pointOutput: PointF) {

            pointOutput.x = pointInput.x
            pointOutput.y = pointCenter.y - pointInput.y + pointCenter.y
        }
    }
}