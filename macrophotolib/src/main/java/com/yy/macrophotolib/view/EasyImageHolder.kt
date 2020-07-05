package com.yy.macrophotolib.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.ImageView
import android.widget.RelativeLayout
import com.alexvasilkov.gestures.Settings
import com.alexvasilkov.gestures.animation.ViewPosition
import com.alexvasilkov.gestures.animation.ViewPositionAnimator.PositionUpdateListener
import com.alexvasilkov.gestures.views.GestureImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.yy.macrophotolib.R
import com.yy.macrophotolib.utils.ScreenUtils
import java.util.*

/**
 * Created by yy on 2019/3/03.
 * function: 图片容器
 */

class EasyImageHolder @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : RelativeLayout(context, attrs) {

    private var selectPosition = 0
    private val mViewPosition = ArrayList<String>()
    private var gestureImage :GestureImageView?=null

    fun loadFile(url: String,viewPosition: ArrayList<String>?
                 ,  selectPosition:Int=0) {
//        this.selectPosition=selectPosition
//        mViewPosition.clear()
//        mViewPosition.addAll(viewPosition)
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

    private var finished = false

    private fun loadNormalPic(resource: Drawable, scale: Float) {

        var gestureImage = GestureImageView(context)
        this.gestureImage= gestureImage
        gestureImage.controller.settings.isRotationEnabled = false
        gestureImage.controller.settings.isRestrictRotation = true
        gestureImage.controller.settings.maxZoom = 3 * scale

        gestureImage.controller.settings.isFillViewport = true
        gestureImage.controller.settings.gravity = Gravity.CENTER
        gestureImage.controller.settings.fitMethod = Settings.Fit.INSIDE

        val layoutParam = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
//
//        gestureImage.getPositionAnimator().addPositionUpdateListener(PositionUpdateListener { position, isLeaving ->
//                // Exit animation is finished,下面这个mGb和mGic的setVisibility会不断的调用，看能不能进行优化
//                if (gestureImage.getVisibility() == View.VISIBLE) {
//                    val isFinished = position == 0f && isLeaving
////                    gestureImage.setVisibility(if (isFinished) View.INVISIBLE else View.VISIBLE)
//                    if (isFinished && !finished) {
//                        finished = true
//                        //下面两行代码是为了退出时的流畅效果，避免出现闪烁
//                        gestureImage.setOnClickListener(null)
//                        gestureImage.getController().getSettings().disableBounds()
//                        gestureImage.getPositionAnimator().setState(0f, true, true)
//                        if ((context as Activity) != null) {
//                            (context as Activity).finish()
//                            (context as Activity).overridePendingTransition(0, 0)
//                        }
//                    }
//                }
//            })
        addView(gestureImage, layoutParam)
        gestureImage.setImageDrawable(resource)
//        runAfterImageDraw(gestureImage)
        gestureImage.setOnClickListener{
            (context as Activity).onBackPressed()
        }

    }


    private fun runAfterImageDraw( gestureImage: GestureImageView) {
        gestureImage.getViewTreeObserver().addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                gestureImage.getViewTreeObserver().removeOnPreDrawListener(this)
                // 只有当activity不是从保存状态创建时，才应该播放动画
                enterFullImage( gestureImage,selectPosition)
                return true
            }
        })
        gestureImage.invalidate()
    }

    private fun enterFullImage( gestureImage: GestureImageView,positions: Int) {
        // 播放从提供的位置输入动画
        if ( mViewPosition.size > positions) {
            val position = ViewPosition.unpack(mViewPosition.get(positions))
            gestureImage.getPositionAnimator().enter(position, true)
        }
    }

    fun onBackPressd(){
        gestureImage?.getPositionAnimator()?.exit(true)
    }

}