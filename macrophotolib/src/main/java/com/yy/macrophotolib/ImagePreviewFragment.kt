package com.yy.macrophotolib

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.yy.macrophotolib.adapter.ImagePagerAdapter
import com.yy.macrophotolib.callback.OnImageListener
import com.yy.macrophotolib.const.CURRENT_POSITION
import com.yy.macrophotolib.const.IMAGE_INFO
import com.yy.macrophotolib.const.LOCATION_INFO
import com.yy.macrophotolib.entity.ImgOptionEntity
import com.yy.macrophotolib.view.DragViewLayout
import kotlinx.android.synthetic.main.activity_image_preview.*
import java.util.ArrayList

class ImagePreviewFragment : Fragment() {


    private lateinit var mAdapter: ImagePagerAdapter
    private var mPagerPosition = 0

    private var optionEntities = ArrayList<ImgOptionEntity>()

    //开始的坐标值
    private var startY = 0
    private var startX = 0

    //开始的宽高
    private var startWidth = 0
    private var startHeight = 0

    //X、Y的移动距离
    private var xDelta = 0f
    private var yDelta = 0f

    //X、Y的缩放比例
    private var mWidthScale = 0f
    private var mHeightScale = 0f

    private lateinit var colorDrawable: ColorDrawable

    private lateinit var datas: ArrayList<ImageInfo>

    private var dragView:DragViewLayout?=null

    private var imageListener:OnImageListener?=null

    fun addImageListener(imageListener:OnImageListener){
        this.imageListener =imageListener
    }

    companion object {
        private const val DURATION = 250L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = requireActivity().window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        val bundle = this.arguments
        if (bundle != null){
            optionEntities = bundle.getParcelableArrayList(LOCATION_INFO)
            mPagerPosition = bundle.getInt(CURRENT_POSITION, 0)
            datas = bundle.getParcelableArrayList(IMAGE_INFO)
        }
        return View.inflate(context,R.layout.activity_image_preview,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dragView = view.findViewById(R.id.dragView)
        setUpView()
    }

    private fun setUpView() {
        colorDrawable = ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.black))
        root.setBackgroundDrawable(colorDrawable)

        if (optionEntities.isNotEmpty()) {
            var entity = optionEntities[mPagerPosition]

            startY = entity.top
            startX = entity.left
            startWidth = entity.width
            startHeight = entity.height
        }

        mAdapter = ImagePagerAdapter(requireContext(), datas)
        dragView?.dragViewPager?.adapter = mAdapter
        dragView?.dragViewPager?.offscreenPageLimit = 1
        dragView?.setDragListener(object : DragViewLayout.DragListener {
            override fun onDragFinished() {
                onBackPressed()

            }

            override fun onPageSelected(position: Int) {
                if (optionEntities.isNotEmpty()) {
//                    val entity = optionEntities[position]
//                    startY = entity.top
//                    startX = entity.left
//                    startWidth = entity.width
//                    startHeight = entity.height
                }
            }

            override fun onPageLoad() {
                datas.addAll(datas)
                mAdapter.notifyDataSetChanged()
            }

        })
        dragView?.viewTreeObserver?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                dragView?.viewTreeObserver?.removeOnPreDrawListener(this)
                val screenLocation = IntArray(2)
                dragView?.getLocationOnScreen(screenLocation)
                xDelta = (startX - screenLocation[0]) * 1f
                yDelta = (startY - screenLocation[1]) * 1f
                mWidthScale = startWidth.toFloat() / dragView!!.width
                mHeightScale = startHeight.toFloat() / dragView!!.height
                enterAnimation(Runnable {
                    dragView?.showBtn(true)
                })

                return true
            }
        })
        dragView?.dragViewPager?.currentItem = mPagerPosition
    }

    fun onBackPressed() {
        val screenLocation = IntArray(2)
        dragView!!.getLocationOnScreen(screenLocation)
        xDelta = (startX - screenLocation[0]/2) * 1f
        yDelta = (startY - screenLocation[1]/2) * 1f
        mWidthScale = startWidth.toFloat() / dragView!!.getWidth()
        mHeightScale = startHeight.toFloat() / dragView!!.getHeight()
        exitAnimation(Runnable { //结束动画要做的操作
            imageListener?.onRemove()
        })
    }


    private fun enterAnimation(enterAction: Runnable) {
        //放大动画
        dragView?.pivotX = 0F
        dragView?.pivotY = 0F
        dragView?.scaleX = mWidthScale
        dragView?.scaleY = mHeightScale
        dragView?.translationX = xDelta
        dragView?.translationY = yDelta

        val sDecelerator: TimeInterpolator = DecelerateInterpolator()
        dragView?.animate()?.setDuration(DURATION)?.scaleX(1F)
            ?.scaleY(1F)?.translationX(0F)?.translationY(0F)?.setInterpolator(sDecelerator)?.withEndAction(enterAction)
        val bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255)
        bgAnim.duration = DURATION
        bgAnim.start()
    }

    private fun exitAnimation(endAction: Runnable) {
        //缩小动画
        val sInterpolator: TimeInterpolator = LinearInterpolator()
//        dragView.animate().setDuration(250L).scaleX(mWidthScale).scaleY(mHeightScale).translationX(xDelta).translationY(yDelta).setInterpolator(sInterpolator).withEndAction(endAction)
        dragView?.animate()?.setDuration(DURATION)?.alpha(0f)?.setInterpolator(sInterpolator)?.withEndAction(endAction)
        //设置背景渐透明
        val bgAnim: ObjectAnimator = ObjectAnimator.ofInt(colorDrawable, "alpha", 0)
        bgAnim.duration = DURATION
        bgAnim.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("haha","111111111111")
        Glide.get(requireContext()).clearMemory()
    }

}