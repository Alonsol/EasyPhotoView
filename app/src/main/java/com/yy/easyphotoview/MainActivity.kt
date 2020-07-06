package com.yy.easyphotoview

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.yy.macrophotolib.DataUtils
import com.yy.macrophotolib.ImageInfo
import com.yy.macrophotolib.ImagePreviewActivity
import com.yy.macrophotolib.entity.ImgOptionEntity
import com.yy.macrophotolib.utils.MediaUtil
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: DemoAdapter
    private lateinit var  datas: ArrayList<ImageInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        datas = DataUtils.getUrls();
        adapter = DemoAdapter(this, datas)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = adapter
        adapter.setOnChooseListener(object : DemoAdapter.OnChooseListener {
            override fun onChoose(position: Int, items: List<ImageInfo>) {
                val intent = Intent(this@MainActivity, ImagePreviewActivity::class.java)
                val optionEntities = ArrayList<ImgOptionEntity>()
                val screenLocationS = IntArray(2)
                val imgDatas = adapter.getAllView()
                imgDatas.forEachIndexed { index, imageView ->
                    imageView.getLocationOnScreen(screenLocationS)
                    var entity = ImgOptionEntity(screenLocationS[0],screenLocationS[1],imageView.width,imageView.height)
                    optionEntities.add(entity)
                }

                intent.putExtra("image_urls", datas)
                intent.putExtra("image_index", position)
                intent.putParcelableArrayListExtra("positions", optionEntities)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

        })


        rlBtn.setOnClickListener {
            Matisse
                .from(this) //选择视频和图片
                .choose(MimeType.ofAll()) //选择图片
                .showSingleMediaType(true) //这两行要连用 是否在选择图片中展示照相 和适配安卓7.0 FileProvider
                .capture(true)
                .captureStrategy(CaptureStrategy(true, "PhotoPicker")) //有序选择图片 123456...
                .countable(true) //最大选择数量为9
                .maxSelectable(9) //选择方向
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) //界面中缩略图的质量
                .thumbnailScale(0.8f) //蓝色主题
                .theme(R.style.Matisse_Zhihu) //黑色主题
                .imageEngine(GlideImageEngine()) //Picasso加载方式
                .forResult(0x1111)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x1111 && resultCode == RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            var pathList =  Matisse.obtainResult(data);
            Log.e("haha","$pathList")
            val photos = ArrayList<ImageInfo>()
            for (uri in pathList) {
                val photoPath = MediaUtil.getMediaUriPath(this@MainActivity, uri)
                if (!photoPath.isNullOrEmpty()) {
                    photos.add(ImageInfo(photoPath))
                }
            }
            datas.addAll(photos)
            adapter.notifyDataSetChanged()
        }
    }
}
