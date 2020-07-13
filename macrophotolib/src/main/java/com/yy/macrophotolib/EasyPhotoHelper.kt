package com.yy.macrophotolib

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import com.yy.macrophotolib.callback.ILoadDataCallback
import com.yy.macrophotolib.const.CURRENT_POSITION
import com.yy.macrophotolib.const.IMAGE_INFO
import com.yy.macrophotolib.const.LOCATION_INFO
import com.yy.macrophotolib.entity.ImgOptionEntity
import com.yy.macrophotolib.manager.DataManager
import com.yy.macrophotolib.utils.DataUtils
import java.lang.ref.WeakReference
import kotlin.math.acos

class EasyPhotoHelper(private val activity: Activity) {


    private val optionEntities = ArrayList<ImgOptionEntity>()

    private val imageInfo = ArrayList<ImageInfo>()

    private var currentPosition = 0

    private var listener:ILoadDataCallback?=null


    companion object {
        private const val TAG = "EasyPhotoHelper"
    }

    init {
        DataManager.getInstance(activity).setDataCallback(DataUpdateListener(activity))

    }

    /**
     * 添加图片控件，转动画需要
     * optionEntities为空时，默认没有专场动画
     */
    fun addLocationViews(views: List<View>) = apply {
        val viewLoc = IntArray(2)
        views.forEach {
            it.getLocationOnScreen(viewLoc)
            var entity = ImgOptionEntity(viewLoc[0], viewLoc[1], it.width, it.height)
            optionEntities.add(entity)
        }
    }


    /**
     * 添加图片信息
     */
    fun addImageInfo(info: ArrayList<ImageInfo>) = apply {
        imageInfo.addAll(info)
    }

    /**
     * 设置当前位置
     */
    fun currentPosition(position: Int) = apply {
        currentPosition = position
    }

    fun addPageReadyListener(listener:ILoadDataCallback) = apply {
        this.listener = listener
    }

    inner class DataUpdateListener(activity: Activity) : ILoadDataCallback {
        private val weakActivity: WeakReference<Activity> = WeakReference(activity)

        override fun loadPreData() {
            weakActivity.get()?.let {
                listener?.loadPreData()
            }

        }



        override fun loadNextData() {
            weakActivity.get()?.let {
                listener?.loadNextData()

            }
        }
    }



    fun show() {
        val intent = Intent(activity, ImagePreviewActivity::class.java)
        intent.putExtra(IMAGE_INFO, imageInfo)
        intent.putExtra(CURRENT_POSITION, currentPosition)
        intent.putParcelableArrayListExtra(LOCATION_INFO, optionEntities)
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }

}