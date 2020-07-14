package com.yy.easyphotoview

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.yy.macrophotolib.EasyPhotoHelper
import com.yy.macrophotolib.ImageInfo
import com.yy.macrophotolib.callback.ILoadDataCallback
import com.yy.macrophotolib.manager.DataManager
import com.yy.macrophotolib.utils.DataUtils
import com.yy.macrophotolib.utils.MediaUtil
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ILoadDataCallback {

    private lateinit var adapter: DemoAdapter
    private lateinit var datas: ArrayList<ImageInfo>

    private var count = 1

    private var remoteData = ArrayList<ImageInfo>()
    private var loadPre = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        datas = DataUtils.getUrls()
        adapter = DemoAdapter(this, datas)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = adapter
        remoteData = assembleData(DataUtils.getSystemPhotoList(this))

        adapter.setOnChooseListener(object : DemoAdapter.OnChooseListener {
            override fun onChoose(position: Int, items: List<ImageInfo>) {
                count = 1
                loadPre = false
                EasyPhotoHelper(this@MainActivity)
                    .addImageInfo(datas)
                    .addLocationViews(adapter.getAllView())
                    .currentPosition(position)
                    .addPageReadyListener(this@MainActivity)
                    .show()
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
                .originalEnable(true)
                .imageEngine(GlideImageEngine()) //Picasso加载方式
                .forResult(0x1111)
        }
    }


    private fun assembleData(list: List<String>): ArrayList<ImageInfo> {
        var images = ArrayList<ImageInfo>()
        list.forEach {
            var info = ImageInfo(it)
            images.add(info)
        }
        return images
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x1111 && resultCode == RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            var pathList = Matisse.obtainResult(data);
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


    private fun getPageNo(pageNo: Int): List<ImageInfo>? {
        val datas = assembleData(DataUtils.getSystemPhotoList(this))
        val startIndex = (pageNo - 1) * 20
        var endIndex = 20 * pageNo
        if (startIndex >= datas.size - 1) {
            return null
        } else if (endIndex > datas.size - 1) {
            endIndex = datas.size - 1
        }
        return datas.subList(startIndex, endIndex)
    }

    override fun loadPreData() {
        Log.e("test", "loadPreData  count->$count")
        if (!loadPre) {
            count = 1
            loadPre = true
        }
        getPageNo(count)?.let {
            DataManager.getInstance().updatePhoto(it, true)
        }

        count++
    }

    override fun loadNextData() {
        Log.e("test", "loadNextData count->$count")
        if (loadPre) {
            count = 1
            loadPre = false
        }
        getPageNo(count)?.let {
            DataManager.getInstance().updatePhoto(it, false)
        }
        count++
    }
}
