package com.yy.macrophotolib.view

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yy.macrophotolib.utils.ScreenUtils
import java.io.File
import kotlin.math.min

/**
 * Created by yy on 2020/7/03.
 * function: 长图
 */
class LongImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SubsamplingScaleImageView(context, attrs) {

    init {
        setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
        setOnClickListener {
            (context as Activity).onBackPressed()
        }
    }

    fun loadUrl(url: String) {
        Glide.with(context)
            .download(url)
            .into(object : Target<File> {
                override fun onLoadStarted(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

                override fun getSize(cb: SizeReadyCallback) {
                    cb.onSizeReady(
                        Target.SIZE_ORIGINAL,
                        Target.SIZE_ORIGINAL
                    )
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
                    resource: File,
                    transition: Transition<in File>?
                ) {
                    val imageSource = ImageSource.uri(Uri.fromFile(resource))
                    val sWidth = BitmapFactory.decodeFile(resource.absolutePath).width
                    val sHeight = BitmapFactory.decodeFile(resource.absolutePath).height
                    val screenWith: Int = ScreenUtils.getScreenWidth(context)
                    val screenHeight: Int = ScreenUtils.getScreenHeight(context)

                    val scale = sWidth*1.0F / screenWith

                    if (scale < 1) {//图片宽度小于屏幕宽度
                        minScale = screenWith*1.0F  / sWidth
                        maxScale = 3* minScale
                        setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
                        setImage(imageSource, ImageViewState(screenWith*1.0F  / sWidth, PointF(0f, 0f), 0))
                        setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                        setDoubleTapZoomScale(maxScale)
                    } else {
                        minScale = screenWith / sWidth.toFloat()
                        maxScale = 10F;
                        setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
                        setImage(imageSource)
                        setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_FIXED)
                    }
                }


            })
    }
}