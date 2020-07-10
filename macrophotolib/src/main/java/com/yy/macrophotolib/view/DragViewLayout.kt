package com.yy.macrophotolib.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import androidx.viewpager.widget.ViewPager
import com.yy.macrophotolib.R
import com.yy.macrophotolib.utils.ScreenUtils
import kotlin.math.abs


/**
 * Created by yy on 2020/7/03.
 * function: 手势操作控件
 */
class DragViewLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    private lateinit var dragHelper: ViewDragHelper
    lateinit var dragViewPager: UnScrollableViewPager
    lateinit var leftTextView: TextView
    lateinit var rightTextView: TextView
    private var overLimit = false
    private var listener: DragListener? = null
    private var downX = 0f
    private var downY = 0f

    init {
        addDragView()
        addLeftBottomView()
        addRightBottomView()
        initDragHelper()
    }

    private fun addLeftBottomView() {
        leftTextView = TextView(context)
        leftTextView.textSize = 16f
        leftTextView.setTextColor(Color.WHITE)
        leftTextView.setPadding(25, 10, 25, 10)
        val layoutParam = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParam.addRule(ALIGN_PARENT_LEFT)
        layoutParam.addRule(ALIGN_PARENT_BOTTOM)
        layoutParam.leftMargin = ScreenUtils.dp2px(context, 15)
        layoutParam.bottomMargin = ScreenUtils.dp2px(context, 15)
        leftTextView.setBackgroundResource(R.drawable.shape_bottom)
        leftTextView.layoutParams = layoutParam
        addView(leftTextView)
    }

    private fun addRightBottomView() {
        rightTextView = TextView(context)
        rightTextView.textSize = 16f
        rightTextView.setTextColor(Color.WHITE)
        rightTextView.setPadding(25, 10, 25, 10)
        val layoutParam = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParam.addRule(ALIGN_PARENT_RIGHT)
        layoutParam.addRule(ALIGN_PARENT_BOTTOM)
        layoutParam.rightMargin = ScreenUtils.dp2px(context, 15)
        layoutParam.bottomMargin = ScreenUtils.dp2px(context, 15)
        rightTextView.setBackgroundResource(R.drawable.shape_bottom)
        rightTextView.layoutParams = layoutParam
        rightTextView.text = "保存"
        addView(rightTextView)

        rightTextView.setOnClickListener {

        }
    }

    fun showBtn(isShow: Boolean) {
        leftTextView.visibility = if (isShow) View.VISIBLE else View.GONE
        rightTextView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun addDragView() {
        dragViewPager = UnScrollableViewPager(context)
        val layoutParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParam.addRule(CENTER_IN_PARENT)
        dragViewPager.setBackgroundColor(Color.TRANSPARENT)
        addView(dragViewPager, layoutParam)

        dragViewPager.addOnPageChangeListener(this)
    }

    private fun initDragHelper() {
        showBtn(false)
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

                if (releasedChild == dragViewPager) {
                    if (overLimit || abs(yvel) > 8000) {
                        if (listener != null) {
                            listener!!.onDragFinished()
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
                if (scale <= 0) {
                    scale = 0f
                }
                if (a == 0f) {
                    leftTextView.alpha = 1f
                    rightTextView.alpha = 1f

                } else {
                    leftTextView.alpha = 0f
                    rightTextView.alpha = 0f
                }

                currentScale = scale
                (parent as ConstraintLayout).setBackgroundColor(changeAlpha(-0x1000000, scale))

                if (abs(top) <= measuredHeight / 4) {
                    scale = 1 - abs(a)
                    dragViewPager.scaleX = scale
                    dragViewPager.scaleY = scale
                    overLimit = abs(top) > measuredHeight / 5
                }

            }
        })
    }

    private var currentScale = 0f

    fun changeAlpha(color: Int, fraction: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = (Color.alpha(color) * fraction).toInt()
        return Color.argb(alpha, red, green, blue)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                val distanceX = abs(ev.rawX - downX)
                val distanceY = abs(ev.rawY - downY)
                if (distanceX > dragHelper.touchSlop && distanceX > distanceY) {
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


    fun setDragListener(listener: DragListener?) {
        this.listener = listener
    }

    interface DragListener {
        fun onDragFinished()
        fun onPageSelected(position: Int)
        fun onPageLoad()
    }

    private var currentPosition = 0

    override fun onPageScrollStateChanged(state: Int) {
        if (currentPosition + 1 == dragViewPager.adapter?.count) {
            listener?.onPageLoad()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        currentPosition = position
        listener?.onPageSelected(currentPosition)
        leftTextView.text = "${currentPosition + 1}/${dragViewPager.adapter?.count}"
    }


}