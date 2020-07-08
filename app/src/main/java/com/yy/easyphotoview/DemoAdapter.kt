package com.yy.easyphotoview

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yy.macrophotolib.ImageInfo
import com.yy.macrophotolib.utils.LoadUtils
import java.util.*

/**
 * @function :匹配对象
 * @date 2018/12/03
 * @author yuyang
 */
class DemoAdapter(val context: Context, val items: List<ImageInfo>) :
    RecyclerView.Adapter<DemoAdapter.ViewHolder>() {
    private var mCaches = HashMap<Int,ImageView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rl_item_match_state, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var url = items[position].remoteUrl
        if (url.startsWith("http") || url.startsWith("https")) {
            Glide.with(context).load(items[position].remoteUrl).into(holder.image)
        } else {
            Glide.with(context)
                .load(LoadUtils.getImageContentUri(context, url))
                .into(holder.image)
        }
//        Glide.with(context).load(url).into(holder.image)
        holder.flRoot.setOnClickListener {
            mOnChooseListener?.onChoose(position, items)
        }
        mCaches[position] = holder.image
    }

    fun getAllView(): List<ImageView> {
        val list = ArrayList<ImageView>()
        list.clear()
        for (i in 0 until mCaches.size) {
            list.add(mCaches[i]!!)
        }
        return list
    }


    class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var image = view.findViewById<ImageView>(R.id.iv)
        var flRoot = view.findViewById<FrameLayout>(R.id.flRoot)
    }

    private var mOnChooseListener: OnChooseListener? = null

    fun setOnChooseListener(listener: OnChooseListener) = apply {
        this.mOnChooseListener = listener
    }

    interface OnChooseListener {
        fun onChoose(position: Int, items: List<ImageInfo>)
    }
}