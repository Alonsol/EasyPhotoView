package com.yy.macrophotolib.callback

import com.yy.macrophotolib.ImageInfo

interface ILoadDataResultListener {
    fun updatePhoto(images:List<ImageInfo>, header:Boolean)
}