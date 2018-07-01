package com.marian.licenta.ui.activities.scene

import android.app.WallpaperManager
import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.DragEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.marian.licenta.App
import com.marian.licenta.R
import com.marian.licenta.base.mvp.BaseMvpActivity
import com.marian.licenta.room.models.Layer
import com.marian.licenta.room.models.Scene
import com.marian.licenta.utils.Constants
import com.marian.licenta.wallpaper.MyWallpaperService
import com.squareup.picasso.Picasso
import java.lang.Math.*


class SceneActivity : BaseMvpActivity<SceneContract.Presenter>(), SceneContract.View {

    private lateinit var rlScene: RelativeLayout
    private lateinit var ivBackground: ImageView
    private lateinit var tvSetWallpaper: TextView

    private lateinit var scene: Scene
    private var sceneWidth: Int = 0
    private var sceneHeight: Int = 0

    private var layers = ArrayList<Layer>(0)

    private var layerViews = ArrayList<Pair<ImageView, Layer>>(0)

    private var sensorManager: SensorManager? = null
    private var gravitySensor: Sensor? = null

    private var gravityFilterK = 0.8
    private var gravityVector = Array(3, { _ -> 0.0 })
    private var lastGravity = Array(2, { _ -> 0.0 })

    private var dgX = 1.0
    private var dgY = 1.0

    private var sensorFirstInit = false

    private var mathPI = PI

    private var screenWidth = -1
    private var screenHeight = -1

    private lateinit var frameView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @LayoutRes
    override fun bindLayout() = R.layout.activity_scene

    override fun bindPresenter() = ScenePresenter(this)

    @IdRes
    override fun bindLoadingScreen(): Int = R.id.rlLoading


    override fun initViews() {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y

        rlScene = findViewById(R.id.rlScene)
        ivBackground = findViewById(R.id.ivBackground)
        tvSetWallpaper = findViewById(R.id.tvSetWallpaper)

        frameView = findViewById(R.id.frameView)

        scene = intent.getParcelableExtra(Constants.INTENT_EXTRA_SCENE)
        layers.addAll(scene.layers)

        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

        initListeners()

        setFields(scene)
    }

    private fun initListeners() {
        var vto = frameView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    frameView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    frameView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }

                var layoutParams = frameView.layoutParams as RelativeLayout.LayoutParams
                layoutParams.height = (screenHeight * Constants.FRAME_SCREEN_MULTIPLIER).toInt()
                layoutParams.width = (sceneWidth * Constants.FRAME_SCREEN_MULTIPLIER).toInt()

                layoutParams.topMargin = (rlScene.measuredHeight - layoutParams.height) / 2
                layoutParams.leftMargin = (rlScene.measuredWidth - layoutParams.width) / 2
                frameView.layoutParams = layoutParams
            }
        })

        tvSetWallpaper.setOnClickListener {
            when (tvSetWallpaper.text) {
                getString(R.string.set_wallpaper) -> {
                    frameView.visibility = View.VISIBLE
                    tvSetWallpaper.text = getText(R.string.ok)
                }
                getString(R.string.ok) -> {

                    scene.frameMarginTop = (frameView.layoutParams as RelativeLayout.LayoutParams).topMargin
                    scene.frameMarginLeft = (frameView.layoutParams as RelativeLayout.LayoutParams).leftMargin

                    getPresenter().updateScene(scene)

                    var editor = getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit()
                    editor.putLong(Constants.CURRENT_WALLPAPER_ID, scene.id ?: -1)
                    editor.putInt(Constants.SCREEN_FULL_WIDTH, screenWidth)
                    editor.putInt(Constants.SCREEN_FULL_HEIGHT, screenHeight)
                    editor.commit()

                    val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(this, MyWallpaperService::class.java))
                    startActivity(intent)
                    tvSetWallpaper.text = getText(R.string.set_wallpaper)
                    frameView.visibility = View.GONE
                }
            }
        }

        frameView.setOnLongClickListener {
            val item = ClipData.Item("")

            val data = ClipData.newPlainText("", "")
            data.addItem(item)
            val shadowBuilder = View.DragShadowBuilder(frameView)
            frameView.startDrag(data, shadowBuilder, frameView, 0)

            frameView.visibility = View.INVISIBLE

            true
        }

        rlScene.setOnDragListener { view, dragEvent ->
            var action = dragEvent.action
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {}
                DragEvent.ACTION_DRAG_ENTERED -> {}
                DragEvent.ACTION_DRAG_EXITED -> {}
                DragEvent.ACTION_DROP -> {
                    frameView.visibility = View.VISIBLE

                    var posX = dragEvent.x.toInt()
                    var posY = dragEvent.y.toInt()

                    var params = frameView.layoutParams as RelativeLayout.LayoutParams

                    params.leftMargin = posX - frameView.measuredWidth / 2
                    params.topMargin = posY - frameView.measuredHeight / 2

                    if (posX - (frameView.measuredWidth / 2) < 0) {
                        params.leftMargin = 0
                    }
                    if (posY - (frameView.measuredHeight / 2) < 0) {
                        params.topMargin = 0
                    }
                    if (posX + (frameView.measuredWidth /2) > rlScene.measuredWidth) {
                        params.leftMargin = rlScene.measuredWidth - frameView.measuredWidth
                    }
                    if (posY + (frameView.measuredHeight /2 )> rlScene.measuredHeight) {
                        params.topMargin = rlScene.measuredHeight - frameView.measuredHeight
                    }

                    frameView.layoutParams = params

                }
                DragEvent.ACTION_DRAG_ENDED -> {}
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        System.gc()
        sensorManager?.unregisterListener(sensorEventListener)
        super.onPause()
    }

    override fun onBackPressed() {
        when (tvSetWallpaper.text) {
            getString(R.string.ok) -> {
                frameView.visibility = View.GONE
                tvSetWallpaper.text = getText(R.string.set_wallpaper)
            }

            else -> {
                super.onBackPressed()
            }
        }
    }

    private var sensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            gravityVector[0] = gravityVector[0] * gravityFilterK + (1 - gravityFilterK) * x
            gravityVector[1] = gravityVector[1] * gravityFilterK + (1 - gravityFilterK) * y
            gravityVector[2] = gravityVector[2] * gravityFilterK + (1 - gravityFilterK) * z

            var gX = gravityVector[0]
            var gY = gravityVector[1]
            var gZ = gravityVector[2]

            var roll = atan2(gX, gZ) * 180 / mathPI
            var pitch: Double;// = atan2(gY, sqrt(gX*gX + gZ*gZ)) * 180/mathPI

            // normalize gravity vector at first
            val gSum = sqrt(gX * gX + gY * gY + gZ * gZ)
            if (gSum != 0.0) {
                gX /= gSum
                gY /= gSum
                gZ /= gSum
            }

            pitch = sqrt(gX * gX + gZ * gZ)
            if (gZ != 0.0) roll = atan2(gX, gZ) * 180 / mathPI
            if (pitch != 0.0) pitch = atan2(gY, pitch) * 180 / mathPI

            dgX = (roll - lastGravity[0])
            dgY = (pitch - lastGravity[1])

            // if device orientation is close to vertical – rotation around x is almost undefined – skip!
            if (gY > 0.99) dgX = 0.0

            // if rotation was too intensive – more than 180 degrees – skip it
            if (dgX !in -180..180) dgX = 0.0
            if (dgY !in -180..180) dgY = 0.0

            lastGravity[0] = roll
            lastGravity[1] = pitch

            if (dgX != 0.0 || dgY != 0.0) {
                for (pair in layerViews) {

                    if (!sensorFirstInit) {
                        adjustFirstTimeLayersViewedPositions(pair.first,
                                (dgX * getLayerGravityValue(layer = pair.second) * gZ).toInt(),
                                -(dgY * getLayerGravityValue(layer = pair.second) * gZ).toInt()
                        )
                    }
                    setLayerPosition(pair.first,
                            (dgX * (getLayerGravityValue(pair.second) * gZ)).toInt(),
                            -(dgY * (getLayerGravityValue(pair.second) * gZ)).toInt()
                    )
                }

                if (layerViews.size == scene.layersCount) {
                    sensorFirstInit = true
                }

            }

//            Log.d("Coordonates", /*"x:$x  y:$y  z:$z*/  "gx:$gX  gy:$gY  gz:$gZ  roll:$roll  pitch:$pitch  dgX:$dgX  dgY:$dgY")

        }

    }

    fun getLayerGravityValue(layer: Layer): Double = (scene.layersCount.toDouble() + 100) / 100 * ((1 + 2 * scene.layersCount - layer.layerNo) / scene.layersCount)

    fun setFields(scene: Scene) {

        var bitmapOption: BitmapFactory.Options = BitmapFactory.Options()
        bitmapOption.inScaled = true
        bitmapOption.inSampleSize = 2
        bitmapOption.inDensity = ivBackground.width

        val backgroundBitmap = BitmapFactory.decodeFile(scene.backgroundImage, bitmapOption)
        ivBackground.setImageBitmap(backgroundBitmap)

        rlScene.removeAllViews()

        scene.layers?.let {
            for (layer in scene.layers) {
                var ivLayer = ImageView(this@SceneActivity)

                var viewTreeObserver = ivLayer.viewTreeObserver
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ivLayer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        } else {
                            ivLayer.viewTreeObserver.removeGlobalOnLayoutListener(this)
                        }

                        if (sceneWidth == 0 || sceneHeight == 0) {
                            sceneWidth = ivBackground.measuredWidth
                            sceneHeight = ivBackground.measuredHeight
                        }

                        var ivParams = RelativeLayout.LayoutParams((layer.width.toLong() * sceneWidth.toLong() / App.instance.surfaceViewWidth!!.toLong()).toInt(),
                                (layer.height.toLong() * sceneHeight.toLong() / App.instance.surfaceViewHeight!!.toLong()).toInt())

                        ivParams.leftMargin = (layer.marginLeft.toLong() * sceneWidth.toLong() / App.instance.surfaceViewWidth!!.toLong()).toInt()
                        ivParams.topMargin = (layer.marginTop.toLong() * sceneHeight.toLong() / App.instance.surfaceViewHeight!!.toLong()).toInt()

                        ivLayer.layoutParams = ivParams

                        Picasso.get()
                                .load(layer.previewURL)
                                .into(ivLayer)

                        layerViews.add(Pair(ivLayer, layer))
                    }
                })

                rlScene.addView(ivLayer)
            }

        }
    }


    private fun adjustFirstTimeLayersViewedPositions(imageView: ImageView, posX: Int, posY: Int) {
        var ivParams = imageView.layoutParams as RelativeLayout.LayoutParams

        ivParams.leftMargin -= posX
        ivParams.topMargin -= posY
        if (ivParams.leftMargin > 0 &&
                imageView.measuredWidth > 0 &&
                (imageView.parent as RelativeLayout).measuredWidth in 1..ivParams.leftMargin + imageView.measuredWidth) {
            ivParams.rightMargin = (imageView.parent as RelativeLayout).measuredWidth - ivParams.leftMargin - imageView.measuredWidth
        }
        if (ivParams.topMargin > 0 &&
                imageView.measuredHeight > 0 &&
                (imageView.parent as RelativeLayout).measuredHeight in 1..ivParams.topMargin + imageView.measuredHeight) {
            ivParams.bottomMargin = (imageView.parent as RelativeLayout).measuredHeight - ivParams.topMargin - imageView.measuredHeight
        }
        imageView.layoutParams = ivParams

    }


    private fun setLayerPosition(imageView: ImageView, posX: Int, posY: Int) {
        var ivParams = imageView.layoutParams as RelativeLayout.LayoutParams

        ivParams.leftMargin += posX
        ivParams.topMargin += posY

        if (ivParams.leftMargin > 0 &&
                imageView.measuredWidth > 0 &&
                (imageView.parent as RelativeLayout).measuredWidth in 1..ivParams.leftMargin + imageView.measuredWidth) {
            ivParams.rightMargin = (imageView.parent as RelativeLayout).measuredWidth - ivParams.leftMargin - imageView.measuredWidth
        }
        if (ivParams.topMargin > 0 &&
                imageView.measuredHeight > 0 &&
                (imageView.parent as RelativeLayout).measuredHeight in 1..ivParams.topMargin + imageView.measuredHeight) {
            ivParams.bottomMargin = (imageView.parent as RelativeLayout).measuredHeight - ivParams.topMargin - imageView.measuredHeight
        }

        imageView.layoutParams = ivParams
    }


}
