package com.yy.easyphotoview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.yy.macrophotolib.ImagePreviewActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Glide.with(this).load("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1593749011&di=466378d5d474f65662a7f846aeb1a60d&src=http://wx3.sinaimg.cn/orj360/005OzLj9ly1g5pnw725vrj30g01hce5k.jpg").into(image)
        image.setOnClickListener {
            var intent = Intent(this,ImagePreviewActivity::class.java)
            startActivity(intent)
//            val optionsCompat: ActivityOptionsCompat =
//                ActivityOptionsCompat.makeSceneTransitionAnimation(
//                   this, image, "image"
//                )
//
//            startActivity( intent, optionsCompat.toBundle())
//            overridePendingTransition(0, 0)
        }
    }
}
