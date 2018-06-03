package com.marian.licenta.ui.fragments.camera

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.provider.MediaStore
import com.marian.licenta.R
import com.marian.licenta.adapters.ImagesAdapter
import com.marian.licenta.base.mvp.BaseMvpContract
import com.marian.licenta.base.mvp.BaseMvpPresenter
import com.marian.licenta.room.AppDatabase
import com.marian.licenta.room.DbWorkerThread
import com.marian.licenta.room.models.Layer
import com.marian.licenta.room.models.Scene
import com.marian.licenta.utils.GeneralAsyncTask
import com.marian.licenta.utils.Utils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class CameraPresenter(view: CameraContract.View) : BaseMvpPresenter<CameraContract.View, CameraModel>(view),
        CameraContract.Presenter,
        BaseMvpContract.MvpAdapterPresenter<ImagesAdapter.ViewHolder> {


    private lateinit var dbWorkerThread: DbWorkerThread

    private var appDatabase: AppDatabase? = null

    private lateinit var imageData: ByteArray

    private lateinit var imageFile: File
    private lateinit var scene: Scene

    private lateinit var layersList: MutableList<Layer>


    override fun onBindAdapterItems(position: Int, holder: ImagesAdapter.ViewHolder) {
        var image : Int = getModel().getImagesList().get(position)

        holder.setFields(image)
    }

    override fun getItemCount(): Int {
        return getModel().getImagesList().size
    }

    override fun bindModel(): CameraModel {
        return CameraModel()
    }

    override fun onBind() {
        adapterItemsListInit()
        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread.start()
    }

    private fun adapterItemsListInit() {
        var images : ArrayList<Int> = ArrayList()
        images.add(R.drawable.cat)
        images.add(R.drawable.dog)
        images.add(R.drawable.girl)
        images.add(R.drawable.hot_dog)
        images.add(R.drawable.stand)
        images.add(R.drawable.husky)

        getModel().getImagesList().addAll(images)
    }

    override fun storeScene(name: String, layersCount: Int, layersList: MutableList<Layer>) {
        scene = Scene()
        scene.id = System.currentTimeMillis()
        scene.name = name
        scene.backgroundImage = imageFile.absolutePath
        scene.layersCount = layersCount
        scene.layers = layersList

        appDatabase = AppDatabase.getInstance(getView().getContext())

        for (layer in layersList) {
            layer.sceneId = scene.id
        }

        val task = Runnable {
            appDatabase?.sceneDao()?.insert(scene)
            appDatabase?.layerDao()?.insertAll(layersList)
        }
        dbWorkerThread.postTask(task)
    }

    override fun initImageSavingThread() {
        GeneralAsyncTask(object : GeneralAsyncTask.Callback {
            override fun doInBackground() {
                savePhoto(imageData)
            }

            override fun onPostExecute() {
                hideProgress()
                getView().afterPictureTaken()
            }
        }).execute()
    }

    override fun setImageData(imageData: ByteArray) {
        this.imageData = imageData
    }

    fun savePhoto(data: ByteArray) {

        try {

            // convert byte array into bitmap
            val loadedImage = BitmapFactory.decodeByteArray(data, 0,
                    data.size)

            // rotate Image
            val rotateMatrix = Matrix()
            rotateMatrix.postRotate(90f)
            val rotatedBitmap = Bitmap.createBitmap(loadedImage, 0,
                    0, loadedImage.width, loadedImage.height,
                    rotateMatrix, false)

            imageFile = Utils.createNewFileForImages("WallpapersApp")


            val ostream = ByteArrayOutputStream()

            // save image into gallery
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, ostream)

            val fout = FileOutputStream(imageFile)
            fout.write(ostream.toByteArray())
            fout.close()
            val values = ContentValues()

            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.MediaColumns.DATA, imageFile.absolutePath)

            getView().getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        } catch (e: Exception) {
            e.printStackTrace()
            hideProgress()
            return
        }

    }

    override fun onDetachView() {
        dbWorkerThread.quit()
        AppDatabase.destroyInstance()
    }

}