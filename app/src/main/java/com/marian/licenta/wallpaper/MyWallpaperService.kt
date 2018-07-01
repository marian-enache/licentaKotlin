package com.marian.licenta.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.marian.licenta.App
import com.marian.licenta.room.AppDatabase
import com.marian.licenta.room.DbWorkerThread
import com.marian.licenta.room.models.Layer
import com.marian.licenta.room.pojos.SceneView
import com.marian.licenta.utils.BitmapUtils
import com.marian.licenta.utils.Constants
import java.lang.Exception
import java.lang.ref.WeakReference


/**
 * Created by Marian on 28.06.2018.
 */
class MyWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return MyWallpaperEngine()
    }


    inner class MyWallpaperEngine : Engine() {

        var width = 0
        var height = 0

        private var visible = false

        private var sceneView: SceneView? = null
        private var layersAndBitmaps: HashMap<Int, Bitmap?> = HashMap()
        private var layersAndPositions: HashMap<Int, Pair<Int, Int>> = HashMap()

        private var sensorManager: SensorManager? = null
        private var gravitySensor: Sensor? = null

        private var dbWorkerThread: DbWorkerThread? = null

        private var appDatabase: AppDatabase? = null

        private var gravityFilterK = 0.8
        private var gravityVector = Array(3, { _ -> 0.0 })
        private var lastGravity = Array(2, { _ -> 0.0 })

        private var dgX = 1.0
        private var dgY = 1.0

        private var sensorFirstInit = false

        private var mathPI = Math.PI

        private var wrBackgroundBitmap: WeakReference<Bitmap>? = null
//        private var layerBitmap: Bitmap? = null
        private var frameWidth: Int = -1
        private var frameHeight: Int = -1

        private var frameObjectsMultiplier = 1 / Constants.FRAME_SCREEN_MULTIPLIER

        private var canvas: Canvas? = null
        private var holder: SurfaceHolder? = null

        private var firstScreenOpenCounter = 0

        var gX = 0.0
        var gY = 0.0
        var gZ = 0.0

        var roll = 0.0
        var pitch = 0.0

        init {
            appDatabase = AppDatabase.getInstance(applicationContext)

            dbWorkerThread = DbWorkerThread("dbWorkerThread")
            dbWorkerThread?.start()

            sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

            val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            var sceneId = sharedPreferences.getLong(Constants.CURRENT_WALLPAPER_ID, -1)
            frameWidth = sharedPreferences.getInt(Constants.SCREEN_FULL_WIDTH, 0)// * Constants.FRAME_SCREEN_MULTIPLIER).toInt()
            frameHeight = sharedPreferences.getInt(Constants.SCREEN_FULL_HEIGHT, 0)// * Constants.FRAME_SCREEN_MULTIPLIER).toInt()

            var task = Runnable {
                sceneView = getScene(sceneId)

                sceneView?.let {
                    for (layer in sceneView!!.layers) {
                        layersAndBitmaps[layer.id!!]?.recycle()
                        layersAndBitmaps[layer.id!!] = BitmapUtils.getBitmapFromURL(layer.previewURL,
                                (layer.width * frameObjectsMultiplier).toInt(),
                                (layer.height * frameObjectsMultiplier).toInt())

                        layersAndPositions[layer.id!!] = Pair(((layer.marginLeft - sceneView?.scene!!.frameMarginLeft) * frameObjectsMultiplier).toInt(),
                                ((layer.marginTop - sceneView?.scene!!.frameMarginTop) * frameObjectsMultiplier).toInt())
                    }

                    wrBackgroundBitmap?.get()?.recycle()
                    wrBackgroundBitmap?.clear()
                    wrBackgroundBitmap = null
                    wrBackgroundBitmap = WeakReference(BitmapUtils.decodeFile(sceneView!!.scene!!.backgroundImage, App.instance.surfaceViewWidth!!, App.instance.surfaceViewHeight!!))
                }

                dbWorkerThread?.quit()
            }
            dbWorkerThread?.postTask(task)

        }

        private var sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                sceneView?.let { draw(event) }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                sensorManager?.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
            } else {
                sensorManager?.unregisterListener(sensorEventListener)
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            sensorManager?.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            this.width = width
            this.height = height

            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            sensorManager?.unregisterListener(sensorEventListener)
            this.visible = false
            super.onSurfaceDestroyed(holder)
        }

        override fun onDestroy() {
            AppDatabase.destroyInstance()
            super.onDestroy()
        }

        private fun draw(event: SensorEvent) {

            gravityVector[0] = gravityVector[0] * gravityFilterK + (1 - gravityFilterK) * event.values[0]
            gravityVector[1] = gravityVector[1] * gravityFilterK + (1 - gravityFilterK) * event.values[1]
            gravityVector[2] = gravityVector[2] * gravityFilterK + (1 - gravityFilterK) * event.values[2]

            gX = gravityVector[0]
            gY = gravityVector[1]
            gZ = gravityVector[2]

            roll = Math.atan2(gX, gZ) * 180 / mathPI

            // normalize gravity vector at first
            val gSum = Math.sqrt(gX * gX + gY * gY + gZ * gZ)
            if (gSum != 0.0) {
                gX /= gSum
                gY /= gSum
                gZ /= gSum
            }

            pitch = Math.sqrt(gX * gX + gZ * gZ)
            if (gZ != 0.0) roll = Math.atan2(gX, gZ) * 180 / mathPI
            if (pitch != 0.0) pitch = Math.atan2(gY, pitch) * 180 / mathPI

            dgX = (roll - lastGravity[0])
            dgY = (pitch - lastGravity[1])

            // if device orientation is close to vertical – rotation around x is almost undefined – skip!
            if (gY > 0.99) dgX = 0.0

            // if rotation was too intensive – more than 180 degrees – skip it
            if (dgX !in -180..180) dgX = 0.0
            if (dgY !in -180..180) dgY = 0.0

            lastGravity[0] = roll
            lastGravity[1] = pitch

            canvas?.setBitmap(null)
            canvas = null

            try {
                holder = surfaceHolder
                canvas = holder?.lockCanvas()
                if (canvas != null && wrBackgroundBitmap != null
                   && wrBackgroundBitmap!!.get() != null) {

                    canvas!!.drawBitmap(BitmapUtils.cropBitmapForSpecificCase(wrBackgroundBitmap?.get()!!, sceneView?.scene!!, frameWidth, frameHeight), 0f, 0f, null)

                    firstScreenOpenCounter = if (sceneView!!.layers.isNotEmpty()) 0 else -1
                    for (layer in sceneView!!.layers) {
                        if (firstScreenOpenCounter <= sceneView!!.layers.size) {
                            firstScreenOpenCounter++
                        }
                        layersAndBitmaps[layer.id]?.let {

                            if (!sensorFirstInit) {
                                drawFirstTimeLayerByPositions(canvas!!, layersAndBitmaps[layer.id]!!, layer,
                                        (dgX * getLayerGravityValue(layer) * gZ).toInt(),
                                        -(dgY * getLayerGravityValue(layer) * gZ).toInt()
                                )
                            } else {
                                drawLayerByPositions(canvas!!, layersAndBitmaps[layer.id]!!, layer,
                                        (dgX * getLayerGravityValue(layer) * gZ).toInt(),
                                        -(dgY * getLayerGravityValue(layer) * gZ).toInt()
                                )
                            }
                        }
                    }

                    if (firstScreenOpenCounter == sceneView!!.layers.size) {
                        sensorFirstInit = true
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null)
                    try {
                        holder?.unlockCanvasAndPost(canvas)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    }
            }

        }

        private fun drawFirstTimeLayerByPositions(canvas: Canvas, bitmap: Bitmap, layer: Layer, posX: Int, posY: Int) {

            var finalPosX = layersAndPositions[layer.id]!!.first - posX
            var finalPosY = layersAndPositions[layer.id]!!.second - posY

            layersAndPositions[layer.id!!] = Pair(finalPosX, finalPosY)

            canvas.drawBitmap(bitmap,
                    finalPosX.toFloat(),
                    finalPosY.toFloat(),
                    null)
        }

        private fun drawLayerByPositions(canvas: Canvas, bitmap: Bitmap, layer: Layer, posX: Int, posY: Int) {
            var finalPosX = layersAndPositions[layer.id]!!.first + posX
            var finalPosY = layersAndPositions[layer.id]!!.second + posY

            layersAndPositions[layer.id!!] = Pair(finalPosX, finalPosY)

            canvas.drawBitmap(bitmap,
                    finalPosX.toFloat(),   // (the margin from the image being the size of the screen
                    // minus the margin of the frame from the same image)
                    // multiplied with the multiplier  1 / frame multiplier
                    finalPosY.toFloat(),
                    null)
        }

        fun getLayerGravityValue(layer: Layer): Double = (sceneView?.scene?.layersCount!!.toDouble() + 100) / 100 * ((1 + 4 * sceneView?.scene?.layersCount!! - layer.layerNo) / sceneView?.scene?.layersCount!!)

        private fun getScene(sceneId: Long): SceneView? {
            return appDatabase?.sceneDao()?.getSceneView(sceneId)
        }

    }
}