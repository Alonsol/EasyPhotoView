package com.yy.macrophotolib.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

object ScreenUtils {

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    fun getScreenHeight(context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }


    /**
     * dp单位转成px
     *
     * @param context context
     * @param dp      dp值
     * @return px值
     */
    fun dp2px(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

}