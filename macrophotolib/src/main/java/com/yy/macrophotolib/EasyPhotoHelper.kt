package com.yy.macrophotolib

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.yy.macrophotolib.callback.OnImageListener
import com.yy.macrophotolib.const.CURRENT_POSITION
import com.yy.macrophotolib.const.IMAGE_INFO
import com.yy.macrophotolib.const.LOCATION_INFO
import com.yy.macrophotolib.entity.ImgOptionEntity

class EasyPhotoHelper(private val context: AppCompatActivity) {


    private val optionEntities = ArrayList<ImgOptionEntity>()

    private val imageInfo = ArrayList<ImageInfo>()

    private var currentPosition = 0

    private var imagePreviewFragment: ImagePreviewFragment? = null

    companion object {
        private const val TAG = "EasyPhotoHelper"
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


    fun show() {
//        val fragmentManager = context.supportFragmentManager
//        findFragment(fragmentManager)
        val intent = Intent(context, ImagePreviewActivity::class.java)
        intent.putExtra(IMAGE_INFO, imageInfo)
        intent.putExtra(CURRENT_POSITION, currentPosition)
        intent.putParcelableArrayListExtra(LOCATION_INFO, optionEntities)
        context.startActivity(intent)
        context.overridePendingTransition(0, 0)
    }


    /**
     * 查看fragment
     */
    private fun findFragment(fragmentManager: FragmentManager?) {
        var imagePreviewFragment = getFragment(fragmentManager)
        if (imagePreviewFragment != null) {
            return
        } else {
            imagePreviewFragment = ImagePreviewFragment()
            var bundle = Bundle()
            bundle.putParcelableArrayList(IMAGE_INFO, imageInfo)
            bundle.putInt(CURRENT_POSITION, currentPosition)
            bundle.putParcelableArrayList(LOCATION_INFO, optionEntities)
            imagePreviewFragment.arguments = bundle
            fragmentManager?.beginTransaction()?.add(android.R.id.content, imagePreviewFragment)
                ?.commitNow()
        }

        imagePreviewFragment.addImageListener(object : OnImageListener {
            override fun onRemove() {
                var transaction = fragmentManager?.beginTransaction()
                transaction?.remove(imagePreviewFragment)
                transaction?.commit()
            }

        })

    }

    private fun getFragment(fragmentManager: FragmentManager?): ImagePreviewFragment? {
        return fragmentManager?.findFragmentByTag(TAG) as ImagePreviewFragment?
    }
}