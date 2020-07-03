package com.yy.macrophotolib.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * Created by yy on 2019/3/03.
 * function: 不可滚动的ViewPager
 */

class UnScrollableViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : androidx.viewpager.widget.ViewPager(context, attrs) {

    private var canScroll = false

    init {
        this.canScroll = true
    }

    private var startX=0f
    private var startY=0f


    fun setPagingEnabled(enabled: Boolean) {
        this.canScroll = enabled
    }

//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        when(ev.action){
//            MotionEvent.ACTION_DOWN ->{
//                startY = ev.rawY
//                startX = ev.rawX
//
//            }
//
//            MotionEvent.ACTION_MOVE ->{
//                var endX = ev.rawX
//                var endY = ev.rawY
//                if (abs(endY-startY) > abs(endX-startX)){
//                    return false
//                } else {
//                    requestDisallowInterceptTouchEvent(false)
//                }
//            }
//        }
//        return super.onTouchEvent(ev)
//    }

}