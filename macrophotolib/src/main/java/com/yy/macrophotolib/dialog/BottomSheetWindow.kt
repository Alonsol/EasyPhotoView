package com.yy.macrophotolib.dialog

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.yy.macrophotolib.R

/**
 * @function 评价完成弹窗
 * @author yuyang
 * @date 2018/09/18
 */
class BottomSheetWindow(context: Context) : AbsCommonPopWindow(context) {

    private var mOnItemClickListener: OnItemClickListener? = null

    override fun initView(mView: View) {


        mView.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dismissPopupWindow()
        }
        mView.findViewById<TextView>(R.id.tv_save).setOnClickListener {
            mOnItemClickListener?.onSave()
            dismissPopupWindow()
        }

    }

    interface OnItemClickListener {
        fun onSave()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) = apply {
        mOnItemClickListener = listener
    }
}
