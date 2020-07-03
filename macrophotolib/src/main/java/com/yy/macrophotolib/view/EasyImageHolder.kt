package com.yy.macrophotolib.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.RelativeLayout
import com.alexvasilkov.gestures.Settings
import com.alexvasilkov.gestures.views.GestureImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.yy.macrophotolib.R
import com.yy.macrophotolib.utils.ScreenUtils

/**
 * Created by yy on 2019/3/03.
 * function: 图片容器
 */

class EasyImageHolder @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : RelativeLayout(context, attrs) {

    fun loadFile(url: String) {
        Glide.with(context)
            .load(url)
            .into(object : Target<Drawable> {
                override fun onLoadStarted(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    var imageView = ImageView(context)
                    imageView.setImageResource(R.mipmap.not_pic)
                    val layoutParam = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                    )
                    addView(imageView,layoutParam)
                }

                override fun getSize(cb: SizeReadyCallback) {
                    cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                }

                override fun getRequest(): Request? {
                    return null
                }

                override fun onStop() {

                }

                override fun setRequest(request: Request?) {

                }

                override fun removeCallback(cb: SizeReadyCallback) {

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onStart() {

                }

                override fun onDestroy() {

                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {

                    val sWidth = resource.intrinsicWidth
                    val sHeight = resource.intrinsicHeight
                    val screenWith: Int = ScreenUtils.getScreenWidth(context)
                    val screenHeight: Int = ScreenUtils.getScreenHeight(context)
                    val scale = screenWith * 1.0F / sWidth//屏幕宽度相对于图片宽度
                    if (sWidth >= sHeight) {//宽度大于高度->正常加载
                        loadNormalPic(resource, scale)

                    } else if (sWidth < sHeight) { //宽度小于高度

                        if (sHeight * scale > screenHeight) {//长图加载
                            var longImageView = LongImageView(context)
                            longImageView.loadUrl(url)
                            val layoutParam = LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT
                            )
                            addView(longImageView, layoutParam)
                        } else {//普通图片
                            loadNormalPic(resource, scale)
                        }

                    }
                }
            })
    }

    private fun loadNormalPic(resource: Drawable, scale: Float) {
        var gestureImage = GestureImageView(context)
        gestureImage.controller.settings.isRotationEnabled = false
        gestureImage.controller.settings.isRestrictRotation = true
        gestureImage.controller.settings.maxZoom = 3 * scale

        gestureImage.controller.settings.isFillViewport = true
        gestureImage.controller.settings.gravity = Gravity.CENTER
        gestureImage.controller.settings.fitMethod = Settings.Fit.INSIDE
        gestureImage.setImageDrawable(resource)
        val layoutParam = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(gestureImage, layoutParam)
        gestureImage.setOnClickListener{
            (context as Activity).onBackPressed()
        }
    }

}