package com.yy.macrophotolib.dialog

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.PopupWindow
import androidx.core.widget.PopupWindowCompat.showAsDropDown
import com.yy.macrophotolib.indef.XGravity
import com.yy.macrophotolib.indef.YGravity

/**
 * @function 通用popwindow
 * @author yuyang
 * @date 2018/09/16
 */
abstract class AbsCommonPopWindow(val context: Context) {

    protected lateinit var mPopupWindow: PopupWindow

    private var mInputMethodMode = PopupWindow.INPUT_METHOD_FROM_FOCUSABLE

    private var mSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

    private var backgroundAlpha: Float = 1f

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var canTouchOutside: Boolean = true

    private var canFocusable: Boolean = true

    private var canTouchable: Boolean = true

    lateinit var mView: View

    private var animStyle: Int = 0

    @YGravity
    private var mYGravity = YGravity.BELOW

    @XGravity
    private var mXGravity = XGravity.CENTER

    private var mOffsetX: Int = 0

    private var mOffsetY: Int = 0


    private var mEnableKeyBack = true

    private var mEnableBgAlpha = true

    fun view(resView: Int) {
        mView = LayoutInflater.from(context).inflate(resView, null)
    }

    fun view(view: View) {
        mView = view
    }


    fun height(heightPx: Int) {
        mHeight = heightPx
    }

    fun width(widthPx: Int) {
        mWidth = widthPx
    }

    fun heightDp(heightDp: Int) {
        mHeight = dp2px(context, heightDp)
    }

    fun widthDp(widthDp: Int) {
        mWidth = dp2px(context, widthDp)
    }

    fun canTouchOutside(canTouchOutsideable: Boolean) {
        canTouchOutside = canTouchOutsideable
    }

    fun canFocusable(canFocuse: Boolean) {
        canFocusable = canFocuse
    }

    fun YGravity(@YGravity yGravity: Int) {
        this.mYGravity = yGravity
    }

    fun XGravity(@XGravity xGravity: Int) {
        this.mXGravity = xGravity
    }

    fun OffsetX(offsetX: Int) {
        this.mOffsetX = offsetX
    }

    fun OffsetY(offsetY: Int) {
        this.mOffsetY = offsetY
    }

    fun animStyle(animStylable: Int) {
        this.animStyle = animStylable
    }

    fun enableKeyBack(enable: Boolean) {
        this.mEnableKeyBack = enable
    }

    fun enableBgAlpha(enable: Boolean) {
        this.mEnableBgAlpha = enable
    }

    companion object {

        fun dp2px(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp * density + 0.5f).toInt()
        }
    }

    fun setInputMethodMode(mode: Int) {
        this.mInputMethodMode = mode
    }

    fun setSoftInputMode(mode: Int) {
        this.mSoftInputMode = mode
    }

    fun backgroundAlpha(alpha: Float) {
        this.backgroundAlpha = alpha
    }

    fun setBackgroundAlpha(f: Float) {
        if (mEnableBgAlpha) {
            var activity = mView.context as Activity
            var lp = activity.window.attributes
            lp.alpha = f
            activity.window.attributes = lp
        }
    }

    fun <T : View> getView(id: Int): T {
        return mView.findViewById(id)
    }

    /**
     * 指定屏幕中显示位置
     */
    fun showAtLocation(parent: View, gravity: Int) {
        initAttribute()
        mPopupWindow.showAtLocation(parent, gravity, mOffsetX, mOffsetY)
    }


    fun isShowing(): Boolean {
        if (::mPopupWindow.isInitialized) {
            return mPopupWindow.isShowing
        }
        return false
    }

    /**
     * 相对某个控件anchor显示
     */
    open fun showAnchorDropDown(anchor: View) {
        initAttribute()

        measure()
        val popupWidth = mPopupWindow.contentView.measuredWidth
        val popupHeight = mPopupWindow.contentView.measuredHeight
        updateLocation(popupWidth, popupHeight, anchor, mYGravity, mXGravity, mOffsetX, mOffsetY)
    }

    protected open fun measure() {
        mPopupWindow.contentView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
    }

    private fun initAttribute() {

        mPopupWindow = PopupWindow()


        mOnViewListener?.initViews(mView, this)

        initView(mView)

        mPopupWindow.contentView = mView

        mPopupWindow.width = if (mWidth == 0) ViewGroup.LayoutParams.WRAP_CONTENT else mWidth
        mPopupWindow.height = if (mHeight == 0) ViewGroup.LayoutParams.WRAP_CONTENT else mHeight


//        mPopupWindow.isFocusable = canFocusable
        mPopupWindow.isTouchable = canTouchable

        if (animStyle != 0) {
            mPopupWindow.animationStyle = animStyle
        }

        mPopupWindow.inputMethodMode = mInputMethodMode
        mPopupWindow.softInputMode = mSoftInputMode
        setBackgroundAlpha(backgroundAlpha)

        mPopupWindow.setOnDismissListener {
            setBackgroundAlpha(1f)
            mOnPopDismissListenr?.onPopDismiss()
        }

        initFocusAndBack()
    }

    open fun dismissPopupWindow() {
        if (::mPopupWindow.isInitialized) {
            mPopupWindow.dismiss()
            releaseResource()
        }

    }

    open fun releaseResource() {}


    private fun initFocusAndBack() {

        if (!canTouchOutside) {

            mPopupWindow.isFocusable = true
            mPopupWindow.isOutsideTouchable = false
            mPopupWindow.setBackgroundDrawable(ColorDrawable(-0))
            //注意下面这三个是contentView 不是PopupWindow，响应返回按钮事件
            mPopupWindow.contentView.isFocusable = true
            mPopupWindow.contentView.isFocusableInTouchMode = true

            mPopupWindow.contentView.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    mPopupWindow.dismiss()
                    return@OnKeyListener true
                }
                false
            })

            //在Android 6.0以上 ，只能通过拦截事件来解决
            mPopupWindow.setTouchInterceptor(View.OnTouchListener { v, event ->
                val x = event.x.toInt()
                val y = event.y.toInt()

                if (event.action == MotionEvent.ACTION_DOWN && (x < 0 || x >= mWidth || y < 0 || y >= mHeight)) {
                    return@OnTouchListener true
                } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    return@OnTouchListener true
                }
                false
            })
        } else {
            if (mEnableKeyBack) {
                mPopupWindow.isFocusable = canFocusable
                mPopupWindow.isOutsideTouchable = canTouchOutside
                mPopupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }


    abstract fun initView(mView: View)


    /**
     * 跟新弹窗位置
     */
    private fun updateLocation(width: Int, height: Int, anchor: View, @YGravity yGravity: Int, @XGravity xGravity: Int, x: Int, y: Int) {
        var x = x
        var y = y
        x = calculateX(anchor, xGravity, width, x)
        y = calculateY(anchor, yGravity, height, y)
        showAsDropDown(mPopupWindow, anchor, mOffsetX, mOffsetY, Gravity.NO_GRAVITY)
        mPopupWindow.update(anchor, x, y, width, height)
    }

    /**
     * 根据垂直gravity计算y偏移
     */
    private fun calculateY(anchor: View, vertGravity: Int, measuredH: Int, y: Int): Int {

        var y = y

        when (vertGravity) {

            YGravity.ABOVE ->
                //anchor view之上
                y -= measuredH + anchor.height

            YGravity.ALIGN_BOTTOM ->
                //anchor view底部对齐
                y -= measuredH

            YGravity.CENTER ->
                //anchor view垂直居中
                y -= anchor.height / 2 + measuredH / 2

            YGravity.ALIGN_TOP ->
                //anchor view顶部对齐
                y -= anchor.height

            YGravity.BELOW -> {
            }
        }

        return y
    }


    /**
     * 根据水平gravity计算x偏移
     */
    private fun calculateX(anchor: View, horizGravity: Int, measuredW: Int, x: Int): Int {

        var x = x

        when (horizGravity) {

            XGravity.LEFT ->
                //anchor view左侧
                x -= measuredW

            XGravity.ALIGN_RIGHT ->
                //与anchor view右边对齐
                x -= measuredW - anchor.width

            XGravity.CENTER ->

                //anchor view水平居中
                x += anchor.width / 2 - measuredW / 2

            XGravity.ALIGN_LEFT -> {
            }

            XGravity.RIGHT ->
                //anchor view右侧
                x += anchor.width
        }
        return x
    }


    private var mOnViewListener: OnViewListener? = null


    fun setOnViewListener(listener: OnViewListener) = apply {
        this.mOnViewListener = listener
    }

    interface OnViewListener {
        fun initViews(view: View, popup: AbsCommonPopWindow)
    }


    private var mOnPopDismissListenr: OnPopDismissListenr? = null


    fun setOnPopDismissListenr(listener: OnPopDismissListenr) = apply {
        this.mOnPopDismissListenr = listener
    }

    interface OnPopDismissListenr {
        fun onPopDismiss()
    }

}

