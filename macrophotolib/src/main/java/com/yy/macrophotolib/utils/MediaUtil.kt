package com.yy.macrophotolib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object MediaUtil {

    private val TAG = MediaUtil::class.java.simpleName

    @SuppressLint("NewApi")
    fun getMediaUriPath(context: Context, uri: Uri?): String? {
        val path = getFilePathFromUri(context, uri)
        Log.d(TAG, "getMediaUriPath path:$path")
        return path
    }

    fun getFilePathFromUri(context: Context, uri: Uri?): String? {
        val file: File? = getFileFromUri(context, uri)
        return file?.path
    }

    fun getFileFromUri(context: Context, uri: Uri?): File? {
        uri ?: return null
        return when (uri.scheme) {
            "content" -> getFileFromContentUri(context, uri)
            "file" -> File(uri.path)
            else -> null
        }
    }

    private fun getFileFromContentUri(context: Context, uri: Uri): File? {
        var file: File? = null
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        try {
            context.contentResolver.query(uri, projection, null, null, null)?.use {
                it.moveToFirst()
                if (it.count > 0) {
                    val filePathIndex = it.getColumnIndex(MediaStore.MediaColumns.DATA)
                    val fileNameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    val filePath = if (filePathIndex > -1) it.getString(filePathIndex) else ""
                    val fileName = if (fileNameIndex > -1) it.getString(fileNameIndex) else ""

                    if (!filePath.isNullOrEmpty()) {
                        file = File(filePath)
                    }

                    if (file?.exists() != true) {
                        file = getFileFromInputStreamUri(context, uri, fileName)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    private fun getFileFromInputStreamUri(context: Context, uri: Uri, fileName: String): File? {
        var file: File? = null
        if (uri.authority != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream.use {
                file = copyTempFileFromOtherApp(context, inputStream, fileName)
            }
        }

        return file
    }

    private fun copyTempFileFromOtherApp(context: Context, inputStream: InputStream?, fileName: String): File? {
        var targetFile: File? = null
        inputStream?.let {
            try {
                var read = 0
                val buffer = ByteArray(8 * 1024)
                val cachePath =
                        if (context.externalCacheDir == null) {
                            context.cacheDir.path
                        } else {
                            context.externalCacheDir!!.path
                        }
                targetFile = File(cachePath + File.separator + "temp", fileName)
                if (targetFile?.exists() == true) {
                    targetFile?.delete()
                }
                if (targetFile != null) {
                    ensureCreated(targetFile!!)
                }
                val outputStream = FileOutputStream(targetFile)
                outputStream.use {
                    read = inputStream.read(buffer)
                    while (read != -1) {
                        outputStream.write(buffer, 0, read)
                        read = inputStream.read(buffer)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                targetFile = null
            }
        }
        return targetFile
    }

    fun ensureCreated(targetFile: File): Boolean {

        if (!targetFile.exists()) {
            if (targetFile.parentFile != null && !targetFile.parentFile.exists()) {
                if (!targetFile.parentFile.mkdirs()) {
                    return false
                }
            }
            return targetFile.createNewFile()
        }

        return true
    }
}
