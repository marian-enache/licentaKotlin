package com.marian.licenta.ui.fragments.camera

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.provider.MediaStore
import android.util.Log
import com.marian.licenta.adapters.ImagesAdapter
import com.marian.licenta.base.mvp.BaseMvpContract
import com.marian.licenta.base.mvp.BaseMvpPresenter
import com.marian.licenta.retrofit.ApiResponse
import com.marian.licenta.retrofit.RetroClient
import com.marian.licenta.room.AppDatabase
import com.marian.licenta.room.DbWorkerThread
import com.marian.licenta.room.models.Layer
import com.marian.licenta.room.models.Scene
import com.marian.licenta.utils.Constants
import com.marian.licenta.utils.GeneralAsyncTask
import com.marian.licenta.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        var image = getModel().getImagesList().get(position)

        holder.setFields(image)
    }

    override fun getItemCount(): Int {
        return getModel().getImagesList().size
    }

    override fun bindModel(): CameraModel {
        return CameraModel()
    }

    override fun onBind() {
        dbWorkerThread = DbWorkerThread("dbWorkerThread")
        dbWorkerThread.start()
    }

    override fun adapterItemsListInit() {

        var images : ArrayList<String> = ArrayList()
        val call = RetroClient.apiService.getLayersByWord(Constants.PIXABAY_API_KEY, "png")

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                Log.d("pnFailure", response.body().toString())
                var hits = response.body()?.hits

                hits?.let {
                    for (hit in hits) {
                        images.add(hit.previewURL)
                    }
                }

                getModel().getImagesList().addAll(images)
                getView().notifyAdaptorItemsChange()

            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("pnFailure", t.message)
            }
        })

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
                System.gc()
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