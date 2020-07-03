package com.yy.macrophotolib.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.abs

class DragViewLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private lateinit var dragHelper: ViewDragHelper
    lateinit var dragViewPager: UnScrollableViewPager
    private var overLimit = false
    private var mWidth = 0
    private var mHeight: Int = 0


    init {
        addDragView()
        initDragHelper()
    }

    private fun addDragView() {
        dragViewPager = UnScrollableViewPager(context)
        val layoutParam = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        )
        layoutParam.addRule(CENTER_IN_PARENT)
        dragViewPager.setBackgroundColor(Color.TRANSPARENT)
        addView(dragViewPager, layoutParam)
    }

    private var scale = 0f

    private fun initDragHelper() {
        dragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return child == dragViewPager
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                return left
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return top
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {

                if (releasedChild === dragViewPager) {
                    if (overLimit || abs(yvel) > 8000) {
                        if (listener != null) {
                            listener!!.onDragFinished()
//                            startFinishAnim()
                        }
                        overLimit = false
                    } else {
                        dragHelper.settleCapturedViewAt(0, 0)
                        invalidate()
                    }


                }
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                return width
            }

            override fun getViewVerticalDragRange(child: View): Int {
                return height
            }

            override fun onViewPositionChanged(
                    changedView: View,
                    left: Int,
                    top: Int,
                    dx: Int,
                    dy: Int
            ) {
                val a = top.toFloat() / measuredHeight.toFloat()
                var scale = 1 - 2 * abs(a)
                if (scale <=0){
                    scale = 0f
                }
                (parent as ConstraintLayout).setBackgroundColor(changeAlpha(-0x1000000,scale ))
                if (abs(top) <= measuredHeight / 4) {
                    scale = 1 - abs(a)
                    dragViewPager.scaleX = scale
                    dragViewPager.scaleY = scale
                    overLimit = abs(top) > measuredHeight / 5
                }

            }
        })

    }


    fun startFinishAnim() {
        Log.e("test","alpha ->$scale")
        val publishAlphaAnim = ObjectAnimator.ofFloat(this, "alpha", scale, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.duration = 300
        animatorSet.play(publishAlphaAnim)
        animatorSet.start()
    }


    fun changeAlpha(color: Int, fraction: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = (Color.alpha(color) * fraction).toInt()
        return Color.argb(alpha, red, green, blue)
    }

    private var downX = 0f
    private var downY: kotlin.Float = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                val distanceX = Math.abs(ev.rawX - downX)
                val distanceY = Math.abs(ev.rawY - downY)
                if (distanceX > dragHelper.getTouchSlop() && distanceX > distanceY) {
                    return super.onInterceptTouchEvent(ev)
                }
            }
        }

        val handled: Boolean = dragHelper.shouldInterceptTouchEvent(ev)
        return if (handled) handled else super.onInterceptTouchEvent(ev)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        if (changed) {
            mWidth = width
            mHeight = height
        }
    }

    private var listener: DragListener? = null
    fun setDragListener(listener: DragListener?) {
        this.listener = listener
    }

    interface DragListener {
        fun onDragFinished()
    }
}