package com.yy.macrophotolib.adapter

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.yy.macrophotolib.ImageInfo
import com.yy.macrophotolib.view.EasyImageHolder

class ImagePagerAdapter(private val context: Context, private val mData: List<ImageInfo>) : PagerAdapter() {
    private val itemViewSparseArray: SparseArray<EasyImageHolder> = SparseArray()
    var currentView: EasyImageHolder? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var photoView: EasyImageHolder? = itemViewSparseArray.get(position)
        if (photoView == null) {
            photoView = EasyImageHolder(context)
            photoView.loadFile(mData[position].remoteUrl, null, 0)
            itemViewSparseArray.put(position, photoView)
        }
        currentView = photoView
        container.addView(photoView)
        return photoView
    }


    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
        itemViewSparseArray.remove(position)
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}