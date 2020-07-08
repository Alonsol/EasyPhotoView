package com.yy.macrophotolib.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.yy.macrophotolib.callback.OnProcessFinishListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object LoadUtils {
    fun saveFile(context: Context, url: String,callback:OnProcessFinishListener) {
        try {
            Glide.with(context)
                .download(if (url.startsWith("http") || url.startsWith("https") ) url else LoadUtils.getImageContentUri(context, url))
                .into(object : Target<File> {
                    override fun onLoadStarted(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        callback.onResult(false)
                    }

                    override fun getSize(cb: SizeReadyCallback) {
                        cb.onSizeReady(480, 840)
                    }

                    override fun getRequest(): Request? {
                        return null
                    }

                    override fun onStop() {
                    }

                    override fun setRequest(request: Request?) {
                    }

                    override fun removeCallback(cb: SizeReadyCallback) {
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onStart() {
                    }

                    override fun onDestroy() {
                    }

                    override fun onResourceReady(
                        resource: File,
                        transition: Transition<in File>?
                    ) {
                        val destPath = "${Environment.getExternalStorageDirectory()}/DCIM/Camera/${System.currentTimeMillis()}.jpg"
                        if (FileUtils.copyFile(resource, File(destPath))) {
                            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(destPath))))
                            callback.onResult(true)
                        }
                    }


                })
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onResult(false)
        }
    }



    fun drawableToFile(
        drawable: Drawable?,
        filePath: String?,
        format: CompressFormat?
    ) {
        if (drawable == null) return
        try {
            val file = File(filePath)
            if (file.exists()) file.delete()
            if (!file.exists()) file.createNewFile()
            var out: FileOutputStream? = null
            out = FileOutputStream(file)
            (drawable as BitmapDrawable).bitmap.compress(CompressFormat.PNG, 100, out)
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun getImageContentUri(
        context: Context,
        path: String
    ): Uri? {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf(path),
            null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri =
                Uri.parse("content://media/external/images/media")
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (File(path).exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, path)
                context.contentResolver
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.RGB_565 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }
}