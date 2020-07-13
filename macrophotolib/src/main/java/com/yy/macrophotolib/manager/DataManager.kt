package com.yy.macrophotolib.manager

import android.annotation.SuppressLint
import android.content.Context
import com.yy.macrophotolib.ImageInfo
import com.yy.macrophotolib.callback.ILoadDataCallback
import com.yy.macrophotolib.callback.ILoadDataResultListener

class DataManager(activity: Context) {

    private var callback: ILoadDataCallback? = null

    private var resultListener:ILoadDataResultListener? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: DataManager? = null

        fun getInstance(activity: Context): DataManager {
            if (instance == null) {
                synchronized(DataManager::class) {
                    if (instance == null) {
                        instance = DataManager(activity.applicationContext)
                    }
                }
            }

            return instance!!
        }
    }

    fun setDataCallback(callback: ILoadDataCallback) {
        this.callback = callback
    }

    fun loadPreData() {
        callback?.loadPreData()
    }

    fun loadNextData() {
        callback?.loadNextData()
    }

    fun addLoadResultListener(resultListener:ILoadDataResultListener?){
        this.resultListener = resultListener
    }

    fun updatePhoto(images:List<ImageInfo>,header:Boolean){
        resultListener?.updatePhoto(images,header)
    }

    fun release(){
        resultListener = null
        callback = null
    }
}