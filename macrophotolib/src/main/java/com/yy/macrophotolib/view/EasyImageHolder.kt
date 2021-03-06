package com.yy.macrophotolib.view

//import com.yy.macrophotolib.GlideApp
import android.app.Activity
import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.alexvasilkov.gestures.Settings
import com.alexvasilkov.gestures.animation.ViewPosition
import com.alexvasilkov.gestures.views.GestureImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yy.macrophotolib.ImagePreviewActivity
import com.yy.macrophotolib.R
import com.yy.macrophotolib.callback.OnProcessFinishListener
import com.yy.macrophotolib.dialog.BottomSheetWindow
import com.yy.macrophotolib.utils.LoadUtils
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
    private var gestureImage: GestureImageView? = null

    fun loadFile(
        url: String, viewPosition: ArrayList<String>?
        , selectPosition: Int = 0
    ) {
        Glide.with(context)
            .load(
                if (url.startsWith("http") || url.startsWith("https")) url else LoadUtils.getImageContentUri(
                    context,
                    url
                )
            )
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
                    addView(imageView, layoutParam)
                }

                override fun getSize(cb: SizeReadyCallback) {
                    cb.onSizeReady(480, 840)
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
                        loadNormalPic(url, resource, scale)

                    } else if (sWidth < sHeight) { //宽度小于高度

                        if (sHeight * scale > screenHeight) {//长图加载
                            var longImageView = loadLongImage(url, resource, sWidth, screenWith)
                            val layoutParam = LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT
                            )
                            addView(longImageView, layoutParam)
                        } else {//普通图片
                            loadNormalPic(url, resource, scale)
                        }

                    }
                }
            })
    }

    private fun loadLongImage(
        url: String,
        resource: Drawable,
        sWidth: Int,
        screenWith: Int
    ): SubsamplingScaleImageView {
        var longImageView = SubsamplingScaleImageView(context)
        longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
        longImageView.setOnClickListener {
            (context as Activity).onBackPressed()
        }
        val imageSource = ImageSource.bitmap(LoadUtils.drawableToBitmap(resource))

        val scale = sWidth * 1.0F / screenWith

        if (scale < 1) {//图片宽度小于屏幕宽度
            longImageView.minScale = screenWith * 1.0F / sWidth
            longImageView.maxScale = 3 * longImageView.minScale
            longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
            longImageView.setImage(
                imageSource,
                ImageViewState(screenWith * 1.0F / sWidth, PointF(0f, 0f), 0)
            )
            longImageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
            longImageView.setDoubleTapZoomScale(longImageView.maxScale)
        } else {
            longImageView.minScale = screenWith / sWidth.toFloat()
            longImageView.maxScale = 10F;
            longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
            longImageView.setImage(imageSource)
            longImageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_FIXED)
        }

        longImageView.setOnLongClickListener {
            BottomSheetWindow(context).apply {
                width(ViewGroup.LayoutParams.MATCH_PARENT)
                view(R.layout.layout_bottom_menu)
                animStyle(R.style.menu_animation)
                setOnItemClickListener(object : BottomSheetWindow.OnItemClickListener {
                    override fun onSave() {
                        LoadUtils.saveFile(context, url,
                            OnProcessFinishListener { success ->
                                if (success) {
                                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }

                })
            }.showAtLocation(longImageView, Gravity.BOTTOM)
            true
        }
        return longImageView
    }

    private fun loadNormalPic(url: String, resource: Drawable, scale: Float) {

        var gestureImage = GestureImageView(context)
        this.gestureImage = gestureImage
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

        addView(gestureImage, layoutParam)
        gestureImage.setImageDrawable(resource)
        gestureImage.setOnClickListener {
            (context as Activity).onBackPressed()
        }

        gestureImage.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                BottomSheetWindow(context).apply {
                    width(ViewGroup.LayoutParams.MATCH_PARENT)
                    view(R.layout.layout_bottom_menu)
                    animStyle(R.style.menu_animation)
                    setOnItemClickListener(object : BottomSheetWindow.OnItemClickListener {
                        override fun onSave() {
                            LoadUtils.saveFile(context, url,
                                OnProcessFinishListener { success ->
                                    if (success) {
                                        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }

                    })
                }.showAtLocation(gestureImage, Gravity.BOTTOM)
                return true
            }

        })

    }


    private fun runAfterImageDraw(gestureImage: GestureImageView) {
        gestureImage.getViewTreeObserver().addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                gestureImage.getViewTreeObserver().removeOnPreDrawListener(this)
                // 只有当activity不是从保存状态创建时，才应该播放动画
                enterFullImage(gestureImage, selectPosition)
                return true
            }
        })
        gestureImage.invalidate()
    }

    private fun enterFullImage(gestureImage: GestureImageView, positions: Int) {
        // 播放从提供的位置输入动画
        if (mViewPosition.size > positions) {
            val position = ViewPosition.unpack(mViewPosition.get(positions))
            gestureImage.getPositionAnimator().enter(position, true)
        }
    }

    fun onBackPressd() {
        gestureImage?.getPositionAnimator()?.exit(true)
    }

}