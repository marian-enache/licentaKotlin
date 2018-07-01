package com.marian.licenta.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.marian.licenta.room.models.Scene
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by Marian on 30.06.2018.
 */
fun Bitmap.withDensity(density: Int): Bitmap {
    this.density = density
    return this
}

object BitmapUtils {


    enum class Orientation {
        PORTRAIT, LANDSCAPE
    }

    fun cropBitmapForSpecificCase(bitmap: Bitmap, scene: Scene, reqWidth: Int, reqHeight: Int): Bitmap {

        var tillWidth = (reqWidth * Constants.FRAME_SCREEN_MULTIPLIER).toInt()
        var tillHeight = (reqHeight * Constants.FRAME_SCREEN_MULTIPLIER).toInt()

        if (tillHeight > bitmap.height) {
            return cropBitmapForSpecificCase(bitmap, scene, reqWidth, reqHeight - 1)
        } else if (tillWidth > bitmap.width) {
            return cropBitmapForSpecificCase(bitmap, scene, reqWidth - 1, reqHeight)
        }

        return Bitmap.createScaledBitmap(Bitmap.createBitmap(bitmap,
                scene.frameMarginLeft,
                scene.frameMarginTop,
                tillWidth,
                tillHeight),
                reqWidth, reqHeight, true)
    }

    fun decodeFile(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val bOptions = BitmapFactory.Options()
        bOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, bOptions)
        bOptions.inSampleSize = if (reqWidth > reqHeight) calculateInSampleSize(bOptions, reqWidth, reqHeight) else calculateInSampleSize(bOptions, reqHeight, reqWidth)
        bOptions.inJustDecodeBounds = false
        bOptions.inDensity = if (reqWidth > reqHeight) reqWidth else reqHeight


        var bitmap = BitmapFactory.decodeFile(filePath, bOptions)

        bitmap = resizeBitmap(bitmap, reqWidth, reqHeight)

        return bitmap
    }

    fun getBitmapFromURL(src: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream

            val bOptions = BitmapFactory.Options()
            bOptions.inJustDecodeBounds = true
            bOptions.inSampleSize = if (reqWidth > reqHeight) calculateInSampleSize(bOptions, reqWidth, reqHeight) else calculateInSampleSize(bOptions, reqHeight, reqWidth)
            bOptions.inJustDecodeBounds = false
            bOptions.inScaled = true
            bOptions.inDensity = if (reqWidth > reqHeight) reqWidth else reqHeight

            return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(input, Rect(0, 0, reqWidth, reqHeight), bOptions),
                    reqWidth, reqHeight, true)

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }


    fun addImageToGallery(context: Context, path: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(path)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun bitmapToFile(context: Context, bitmap: Bitmap, quality: Int, fileName: String, compressFormat: Bitmap.CompressFormat): File? {
        var bos: ByteArrayOutputStream? = null
        var fos: FileOutputStream? = null

        try {
            val storageDir = context.getCacheDir()

            var directoryStructureOk = true
            if (!storageDir.exists()) {
                directoryStructureOk = false
                directoryStructureOk = storageDir.mkdirs()
            }

            if (!directoryStructureOk) {
                return null
            }

            val f = File.createTempFile(
                    fileName, /* prefix */
                    if (compressFormat == Bitmap.CompressFormat.PNG) ".png" else ".jpg", /* suffix */
                    storageDir      /* directory */
            )

            // Convert bitmap to byte array
            bos = ByteArrayOutputStream()
            bitmap.compress(compressFormat, quality, bos)
            val bitmapdata = bos!!.toByteArray()

            // write the bytes in file
            fos = FileOutputStream(f)
            fos!!.write(bitmapdata)

            return f
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos!!.close()
                }

                if (bos != null) {
                    bos!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return null
    }

    fun byteArrayToFile(context: Context?, data: ByteArray, fileName: String, fileSuffix: String?): File? {
        var fileSuffix = fileSuffix
        if (context == null) {
            return null
        }

        if (fileSuffix == null) {
            fileSuffix = ".jpg"
        }

        var fos: FileOutputStream? = null
        try {
            val storageDir = context!!.getCacheDir()
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val f = File.createTempFile(
                    fileName, /* prefix */
                    (if (fileSuffix.startsWith(".")) "" else ".") + fileSuffix, /* suffix */
                    storageDir                                                  /* directory */
            )

            // write the bytes in file
            fos = FileOutputStream(f)
            fos!!.write(data)

            return f
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return null
    }

    fun getBitmapFromParcelFileDescriptor(fd: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        val bOptions = BitmapFactory.Options()
        bOptions.inJustDecodeBounds = true
        bOptions.inSampleSize = calculateInSampleSize(bOptions, reqWidth, reqHeight)
        bOptions.inJustDecodeBounds = false

        val parcelFileDescriptor = ParcelFileDescriptor.adoptFd(fd)
        val fileDescriptor = parcelFileDescriptor.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)

        try {
            parcelFileDescriptor.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return image
    }

    fun adjustImageOrientation(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        var rotate = 0

        try {
            val imageFile = File(filePath)

            val exif = ExifInterface(imageFile.getAbsolutePath())
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var bitmap: Bitmap? = decodeFile(filePath, reqWidth, reqHeight)

        if (rotate == 0) {
            return bitmap
        }

        val matrix = Matrix()
        matrix.postRotate(rotate.toFloat())

        var adjustedBitmap: Bitmap? = null

        try {
            adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap!!.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            e.printStackTrace()

        }

        if (bitmap != null && bitmap != adjustedBitmap) {
            bitmap.recycle()
            bitmap = null
        }

        return if (adjustedBitmap != null) adjustedBitmap else bitmap
    }

    fun getMimeType(context: Context, filePath: String): String {
        var cursor: Cursor? = null
        try {
            cursor = context.getContentResolver().query(
                    Uri.fromFile(File(filePath)),
                    arrayOf(MediaStore.MediaColumns.MIME_TYPE), null, null, null)

            return if (cursor != null && cursor!!.moveToNext()) cursor!!.getString(0) else MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(filePath))
        } finally {
            if (cursor != null) {
                cursor!!.close()
            }
        }
    }

    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var newWidth = originalWidth
        var newHeight = originalHeight
        var ratio = 1.0f

        if (originalWidth >= maxWidth || originalHeight >= maxHeight) {
            if (originalWidth >= maxWidth) {
                ratio = originalWidth / maxWidth.toFloat()
                newWidth = maxWidth
                newHeight = (originalHeight / ratio).toInt()
            }

            if (newHeight >= maxHeight) {
                ratio = newHeight / maxHeight.toFloat()
                newHeight = maxHeight
                newWidth = (newWidth / ratio).toInt()
                if (newWidth >= maxWidth) {
                    ratio = newWidth / maxWidth.toFloat()
                    newWidth = maxWidth
                    newHeight = (newHeight / ratio).toInt()
                }
            }

            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }

        return bitmap
    }

    fun rotateToPortraitIfNeeded(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) {
            return null
        }

        if (bitmap.width > bitmap.height) {
            val matrix = Matrix()
            matrix.postRotate(90f)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        return bitmap
    }
}