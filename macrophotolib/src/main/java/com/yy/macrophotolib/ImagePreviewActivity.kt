package com.yy.macrophotolib

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import com.yy.macrophotolib.adapter.ImagePagerAdapter
import com.yy.macrophotolib.view.DragViewLayout
import kotlinx.android.synthetic.main.activity_image_preview.*


class ImagePreviewActivity : FragmentActivity() {

    private lateinit var mAdapter: ImagePagerAdapter

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

        ViewCompat.setTransitionName(dragView, "image")
        mAdapter = ImagePagerAdapter(this, DataUtils.getUrls())
        dragView.dragViewPager.adapter = mAdapter
        dragView.dragViewPager.offscreenPageLimit = 1
        dragView.dragViewPager.currentItem = 0
        dragView.setDragListener(object :
            DragViewLayout.DragListener {
            override fun onDragFinished() {
                Handler().postDelayed({
                    finish()
//                    overridePendingTransition(R.anim.anim_fade_in,R.anim.anim_fade_out)
                }, 10)
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}
