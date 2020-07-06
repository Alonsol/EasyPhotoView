package com.yy.macrophotolib

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.yy.macrophotolib.adapter.ImagePagerAdapter
import com.yy.macrophotolib.entity.ImgOptionEntity
import com.yy.macrophotolib.view.DragViewLayout
import kotlinx.android.synthetic.main.activity_image_preview.*
import java.util.*


class ImagePreviewActivity : AppCompatActivity() {

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

    private lateinit var  datas: ArrayList<ImageInfo>

    companion object {
        private const val DURATION = 250L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        optionEntities = intent.getParcelableArrayListExtra("positions")
        mPagerPosition = intent.getIntExtra("image_index", 0)
        datas = intent.getSerializableExtra("image_urls") as ArrayList<ImageInfo>
        colorDrawable = ColorDrawable(ContextCompat.getColor(this, android.R.color.black));
        root.setBackgroundDrawable(colorDrawable)
        if (!optionEntities.isEmpty()) {
            //设置选中的位置来初始化动画
            var entity = optionEntities.get(mPagerPosition)

            startY = entity.getTop();
            startX = entity.getLeft();
            startWidth = entity.getWidth();
            startHeight = entity.getHeight()

            Log.e("TEST","startX->$startX startY->$startY  startWidth->$startWidth  startHeight->$startHeight")
        }

        mAdapter = ImagePagerAdapter(this, datas)
        dragView.dragViewPager.adapter = mAdapter
        dragView.dragViewPager.offscreenPageLimit = 1
        dragView.dragViewPager.currentItem = 0

        dragView.dragViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (optionEntities != null && !optionEntities.isEmpty()) {
                    val entity = optionEntities[position]
                    startY = entity.top
                    startX = entity.left
                    startWidth = entity.width
                    startHeight = entity.height
                }
            }

        })
        dragView.setDragListener(object :
                DragViewLayout.DragListener {
            override fun onDragFinished() {
                onBackPressed();

            }

        })
        dragView.dragViewPager.currentItem = mPagerPosition
        //注册一个回调函数，当一个视图树将要绘制时调用这个回调函数。
        val observer = dragView.getViewTreeObserver();



        observer.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                dragView.viewTreeObserver.removeOnPreDrawListener(this)
                val screenLocation = IntArray(2)
                dragView.getLocationOnScreen(screenLocation)
                //动画需要移动的距离
                xDelta = (startX - screenLocation[0]) * 1f
                yDelta = (startY - screenLocation[1]) * 1f
                //计算缩放比例
                Log.e("TEST","xDelta =${xDelta}  yDelta = ${yDelta}")
                mWidthScale = startWidth.toFloat() / dragView.getWidth()
                mHeightScale = startHeight.toFloat() / dragView.getHeight()
                Handler().postDelayed({
                    enterAnimation(Runnable {
                        //开始动画之后要做的操作
                    })
                },500)

                return true
            }
        })


    }

    override fun onBackPressed() {
        val screenLocation = IntArray(2)
        dragView.getLocationOnScreen(screenLocation)
        xDelta = (startX - screenLocation[0]/2) * 1f
        yDelta = (startY - screenLocation[1]/2) * 1f
        mWidthScale = startWidth.toFloat() / dragView.getWidth()
        mHeightScale = startHeight.toFloat() / dragView.getHeight()
        exitAnimation(Runnable { //结束动画要做的操作
            finish()
            overridePendingTransition(0, 0)
        })
    }

    private fun enterAnimation(enterAction: Runnable) {
        //放大动画
        dragView.setPivotX(0F)
        dragView.setPivotY(0F)
        dragView.setScaleX(mWidthScale)
        dragView.setScaleY(mHeightScale)
        dragView.setTranslationX(xDelta)
        dragView.setTranslationY(yDelta)

        Log.e("TEST"," enterAnimation  mWidthScale->$mWidthScale   mHeightScale->$mHeightScale  xDelta->$xDelta  yDelta->$yDelta")
        val sDecelerator: TimeInterpolator = DecelerateInterpolator()
        dragView.animate().setDuration(2000).scaleX(1F)
                .scaleY(1F).translationX(0F).translationY(0F).setInterpolator(sDecelerator).withEndAction(enterAction)
        val bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255)
        bgAnim.setDuration(2000)
        bgAnim.start()
    }

    private fun exitAnimation(endAction: Runnable) {
        //缩小动画
//        dragView.setPivotX(0f)
//        dragView.setPivotY(0f)
//        dragView.setScaleX(1f)
//        dragView.setScaleY(1f)
//        dragView.setTranslationX(10f)
//        dragView.setTranslationY(10f)
        Log.e("TEST"," exitAnimation  mWidthScale->$mWidthScale   mHeightScale->$mHeightScale  xDelta->$xDelta  yDelta->$yDelta")
        val sInterpolator: TimeInterpolator = LinearInterpolator()
//        dragView.animate().setDuration(250L).scaleX(mWidthScale).scaleY(mHeightScale).translationX(xDelta).translationY(yDelta).setInterpolator(sInterpolator).withEndAction(endAction)
        dragView.animate().setDuration(150L).alpha(0f).setInterpolator(sInterpolator).withEndAction(endAction)
        //设置背景渐透明
        val bgAnim: ObjectAnimator = ObjectAnimator.ofInt(colorDrawable, "alpha", 0)
        bgAnim.setDuration(150L)
        bgAnim.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        Glide.get(this).clearMemory()
    }

}
