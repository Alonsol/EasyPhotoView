package com.yy.macrophotolib

import android.app.Activity
import android.content.Intent
import android.view.View
import com.yy.macrophotolib.const.CURRENT_POSITION
import com.yy.macrophotolib.const.IMAGE_INFO
import com.yy.macrophotolib.const.LOCATION_INFO
import com.yy.macrophotolib.entity.ImgOptionEntity

class EasyPhotoHelper(private val context: Activity) {


    private val optionEntities = ArrayList<ImgOptionEntity>()

    private val imageInfo = ArrayList<ImageInfo>()

    private var currentPosition = 0

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


    fun show() {
        val intent = Intent(context, ImagePreviewActivity::class.java)
        intent.putExtra(IMAGE_INFO, imageInfo)
        intent.putExtra(CURRENT_POSITION, currentPosition)
        intent.putParcelableArrayListExtra(LOCATION_INFO, optionEntities)
        context.startActivity(intent)
        context.overridePendingTransition(0, 0)
    }

}