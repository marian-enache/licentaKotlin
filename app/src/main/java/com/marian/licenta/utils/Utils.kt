package com.marian.licenta.utils

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException

/**
 * Created by Marian on 29.05.2018.
 */
class Utils {

    companion object {

        fun createNewFileForImages(prefix: String?): File {
            var prefix = prefix
            if (prefix == null || "".equals(prefix, ignoreCase = true)) {
                prefix = "IMG_"
            }
            val newDirectory = File(Environment.getExternalStorageDirectory().toString() + "/WallpapersApp/")
            if (!newDirectory.exists()) {
                if (newDirectory.mkdir()) {
                    Log.d("path", newDirectory.absolutePath + " directory created")
                }
            }
            val imageFile: File

            imageFile = File(newDirectory.absolutePath, prefix + System.currentTimeMillis() + ".jpg")

            Log.d("path to img", imageFile.absolutePath)
            if (imageFile.exists()) {
                //this wont be executed
                imageFile.delete()
                try {
                    imageFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return imageFile
        }
    }
}