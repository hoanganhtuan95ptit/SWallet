package com.simple.wallet.presentation.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.view.children
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.simple.coreapp.utils.ext.bottom
import com.simple.coreapp.utils.ext.left
import com.simple.coreapp.utils.ext.right
import com.simple.coreapp.utils.ext.top

class CustomSwipeRefreshLayout(context: Context, attrs: AttributeSet?) : SwipeRefreshLayout(context, attrs) {

    private var downX = 0f

    private var scaledTouchSlop = 0


    private var isBannerMove: Boolean = false

    private var isBannerUnder: Boolean = false

    private var mapViewAndLocation = hashMapOf<View, Rect>()


    init {
        scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)

        findChildScrollHorizontal().forEach {

            mapViewAndLocation[it] = Rect()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mapViewAndLocation.forEach { (view, rect) ->

            rect.set(view.left(this.id), view.top(this.id), view.right(this.id), view.bottom(this.id))
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        val actionMasked = ev?.actionMasked

        when {
            actionMasked == MotionEvent.ACTION_DOWN -> {

                downX = ev.rawX

                isBannerMove = false
                isBannerUnder = mapViewAndLocation.any { isViewUnder(it.value, ev) }
            }

            isBannerUnder && actionMasked == MotionEvent.ACTION_MOVE -> {

                val calculateDiff = calculateDistanceX(ev, downX)

                if (calculateDiff > scaledTouchSlop) {
                    isBannerMove = true
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        return if (isBannerMove) {
            false
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }

    private fun isViewUnder(rect: Rect?, ev: MotionEvent): Boolean {

        return if (rect == null) {
            false
        } else {
            ev.x >= rect.left && ev.x < rect.right && ev.y >= rect.top && ev.y < rect.bottom
        }
    }

    private fun calculateDistanceX(event: MotionEvent, downX: Float): Int {

        return kotlin.math.abs(event.rawX - downX).toInt()
    }

    private fun ViewGroup.findChildScrollHorizontal(): List<View> {

        val list = arrayListOf<View>()

        children.toList().forEach {

            if (it is ViewPager2) {

                list.add(it)
            } else if (it is ViewGroup) {

                list.addAll(it.findChildScrollHorizontal())
            }
        }

        return list
    }
}