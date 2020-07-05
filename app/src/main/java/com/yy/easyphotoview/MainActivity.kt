package com.yy.easyphotoview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.yy.macrophotolib.DataUtils
import com.yy.macrophotolib.ImageInfo
import com.yy.macrophotolib.ImagePreviewActivity
import com.yy.macrophotolib.entity.ImgOptionEntity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: DemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Glide.with(this).load("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1593749011&di=466378d5d474f65662a7f846aeb1a60d&src=http://wx3.sinaimg.cn/orj360/005OzLj9ly1g5pnw725vrj30g01hce5k.jpg").into(image)
//        image.setOnClickListener {
//            var intent = Intent(this,ImagePreviewActivity::class.java)
//            startActivity(intent)
////            val optionsCompat: ActivityOptionsCompat =
////                ActivityOptionsCompat.makeSceneTransitionAnimation(
////                   this, image, "image"
////                )
////
////            startActivity( intent, optionsCompat.toBundle())
////            overridePendingTransition(0, 0)
//        }
        adapter = DemoAdapter(this, DataUtils.getUrls())
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = adapter
        adapter.setOnChooseListener(object : DemoAdapter.OnChooseListener {
            override fun onChoose(position: Int, items: List<ImageInfo>) {
//                val viewPositions = ArrayList<String>()
//                for (imageView in adapter.getAllView()) {
//                    val viewPosition = ViewPosition.from(imageView)
//                    viewPositions.add(viewPosition.pack())
//                }
                val intent = Intent(this@MainActivity, ImagePreviewActivity::class.java)


                val optionEntities = ArrayList<ImgOptionEntity>()
                val screenLocationS = IntArray(2)
                val imgDatas = adapter.getAllView()
                imgDatas.forEachIndexed { index, imageView ->
                    imageView.getLocationOnScreen(screenLocationS)
                    var entity = ImgOptionEntity(screenLocationS[0],screenLocationS[1],imageView.width,imageView.height)
                    optionEntities.add(entity)
                }

                intent.putExtra("image_urls", DataUtils.getUrls())
                intent.putExtra("image_index", position)
                intent.putParcelableArrayListExtra("positions", optionEntities)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

        })
    }
}
