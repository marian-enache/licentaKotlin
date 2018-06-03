package com.marian.licenta.ui.fragments.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.hardware.Camera
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.marian.licenta.App
import com.marian.licenta.R
import com.marian.licenta.adapters.ImagesAdapter
import com.marian.licenta.base.mvp.BaseMvpFragment
import com.marian.licenta.extensions.cameraFocus
import com.marian.licenta.extensions.focusOnTouch
import com.marian.licenta.extensions.releaseCameraAndPreview
import com.marian.licenta.extensions.setUpCamera
import com.marian.licenta.room.models.Layer
import com.marian.licenta.ui.activities.main.MainActivity
import com.marian.licenta.utils.CustomImageView
import com.marian.licenta.utils.CustomSceneAcceptanceOptions
import com.marian.licenta.utils.ViewUtils
import java.io.FileNotFoundException






class CameraFragment : BaseMvpFragment<CameraContract.Presenter>(), CameraContract.View {

    companion object {
        open val TAG: String = CameraFragment::class.java.simpleName

        private val FOCUS_AREA_SIZE = 300

        open fun newInstance(): CameraFragment = CameraFragment()
    }

    private var cameraId: Int = Camera.CameraInfo.CAMERA_FACING_BACK

    private lateinit var ivListArrow: ImageView
    private lateinit var rvImages: RecyclerView
    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var rlContainer: RelativeLayout
    private lateinit var rlUiElements: RelativeLayout
    private lateinit var ivTakePicture: ImageView
    private lateinit var llAcceptance: LinearLayout
    private lateinit var viewHeader: View
    private lateinit var viewFooter: View

    var camera: Camera? = null

    private var pictureTaken: Boolean = false

    private lateinit var imagesAdapter: ImagesAdapter

    @LayoutRes
    override fun bindLayout(): Int {
        return R.layout.fragment_camera
    }

    override fun bindPresenter(): CameraContract.Presenter {
        return CameraPresenter(this)
    }

    override fun initViews(view: View?) {

        pictureTaken = false

        ivListArrow = view?.findViewById(R.id.ivListArrow)!!
        rvImages = view?.findViewById(R.id.rvImages)!!
        surfaceView = view?.findViewById(R.id.surfaceView)!!
        rlContainer = view?.findViewById(R.id.rlContainer)!!
        rlUiElements = view?.findViewById(R.id.rlUiElements)!!
        ivTakePicture = view?.findViewById(R.id.ivTakePicture)!!
        surfaceHolder = surfaceView?.holder
        surfaceHolder?.addCallback(surfaceHolderCallback)

        viewHeader = view?.findViewById(R.id.viewHeader)
        viewFooter = view?.findViewById(R.id.viewFooter)

        rvImages.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        imagesAdapter = ImagesAdapter(getPresenter() as CameraPresenter)
        imagesAdapter.callback = imageAdapterCallback
        rvImages.adapter = imagesAdapter

        initListeners()

    }

    private fun initListeners() {
        setOnTouchFocus()

        ivListArrow.setOnClickListener { v ->
            if (rvImages.visibility == View.VISIBLE) {
                ViewUtils.hideToRight(rvImages)
                ViewUtils.rotateFrom0to180(ivListArrow)
            } else {
                ViewUtils.showFromRight(rvImages)
                ViewUtils.rotateFrom180to0(ivListArrow)
            }

        }

        surfaceView.setOnDragListener { view, dragEvent ->
            var action = dragEvent.getAction()
            when (action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                }
                DragEvent.ACTION_DROP -> {
                    ivListArrow.visibility = View.VISIBLE
                    ViewUtils.showFromRight(rvImages)

                    activity?.let {
                        if (activity is MainActivity) {
                            (activity as MainActivity).showMenuButton()
                        }
                    }

                    val item = dragEvent.clipData.getItemAt(1)
                    val uri = item.uri

                    var civDraggedImage = CustomImageView(view.context)
                    civDraggedImage.layer = getLayersCount() + 1 // +1 as this represents a layer too

                    civDraggedImage.callbacks = object : CustomImageView.Callbacks {
                        override fun onLayoutAttached() {
                            val width = civDraggedImage.getMeasuredWidth()
                            val height = civDraggedImage.getMeasuredHeight()

                            var params = civDraggedImage.layoutParams as RelativeLayout.LayoutParams

                            params.leftMargin = dragEvent.x.toInt() - width / 2
                            params.topMargin = dragEvent.y.toInt() - height / 2
                            civDraggedImage.layoutParams = params

                            try {
                                civDraggedImage.iv.setImageDrawable(ContextCompat.getDrawable(context, uri.toString().toInt()))
                                civDraggedImage.iv.tag = uri.toString()
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            }

                            civDraggedImage.hideAllBorders()

                        }

                    }

                    rlContainer.addView(civDraggedImage)

                    refreshViewsHierarchy()

                }
                DragEvent.ACTION_DRAG_ENDED -> {
                }
            }
            true

        }

        val surfaceViewViewTreeObserver = surfaceView.viewTreeObserver
        surfaceViewViewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    surfaceView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    surfaceView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
                 val surfaceViewViewTreeObserver = surfaceView.viewTreeObserver

                camera?.let{

                    val cameraDimensionsRatio: Double = camera!!.parameters.pictureSize.width.toDouble() / camera!!.parameters.pictureSize.height.toDouble()

                    var params = rlUiElements.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.CENTER_IN_PARENT)

                    params.height = (surfaceView.measuredWidth * cameraDimensionsRatio).toInt()

                    rlUiElements.layoutParams = params

                }
            }
        })

        rlUiElements.addOnLayoutChangeListener{ view: View, i: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int ->
            App.instance.surfaceViewWidth = rlUiElements.measuredWidth
            App.instance.surfaceViewHeight = rlUiElements.measuredHeight
        }


        ivTakePicture.setOnClickListener { _ ->
            pictureTaken = true
            showProgress()
            takeImage()

        }
    }

    override fun onResume() {
        super.onResume()
        surfaceHolderCallback.surfaceCreated(surfaceHolder)
    }

    override fun onPause() {
        surfaceHolderCallback.surfaceDestroyed(surfaceHolder)
        super.onPause()
    }

    private fun refreshViewsHierarchy() {
        viewHeader.bringToFront()
        viewFooter.bringToFront()
        ivTakePicture.bringToFront()
    }

    private fun getLayersCount(): Int {
        var layersCount = 0
        for (i in 0 until rlContainer.childCount) {
            if (rlContainer.getChildAt(i) is CustomImageView) {
                layersCount++
            }
        }
        return layersCount
    }

    private fun photoTakenVisibilities() {
        activity?.let {
            (activity as MainActivity).hideMenuButton()
        }

        ivListArrow.visibility = View.GONE
        rvImages.visibility = View.GONE
        ivTakePicture.visibility = View.GONE

        llAcceptance = sceneAcceptanceInit(context)

        rlContainer.addView(llAcceptance)

    }

    private fun photoUntakenVisibilities() {
        activity?.let {
            (activity as MainActivity).showMenuButton()
        }

        ivListArrow.visibility = View.VISIBLE
        rvImages.visibility = View.VISIBLE
        ivTakePicture.visibility = View.VISIBLE

        llAcceptance?.let {
            rlContainer.removeView(llAcceptance)
        }

    }

    private fun sceneAcceptanceInit(context: Context?): LinearLayout {
        var llAcceptance = CustomSceneAcceptanceOptions(context!!)
        llAcceptance.callbacks = object : CustomSceneAcceptanceOptions.Callbacks {
            override fun onOk() {
                showProgress()

                getPresenter().initImageSavingThread()
                pictureTaken = false

            }

            override fun onCancel() {
                pictureTaken = false
                if (!openCamera(cameraId)) {
                    Log.d("exceptie", "surfaceCreated exception")
                }
                photoUntakenVisibilities()
            }
        }
        return llAcceptance
    }

    override fun afterPictureTaken() {
        if (!openCamera(cameraId)) {
            Log.d("exceptie", "surfaceCreated exception")
        }
        this@CameraFragment.run {
            createAndStoreScene()
            photoUntakenVisibilities()
            activity?.let {
                (activity as MainActivity).setShouldRefreshGallery(true)
            }
        }
    }

    private fun createAndStoreScene() {

        var layersList: MutableList<Layer> = ArrayList()

        var layersCount: Int = 0
        for (i in 0..rlContainer.childCount) {
            if (rlContainer.getChildAt(i) is CustomImageView) {
                layersCount++
                layersList.add(getLayerFromCustomImageView(rlContainer.getChildAt(i) as CustomImageView))
            }
        }

        getPresenter().storeScene(name = "", layersCount = layersCount, layersList = layersList)
    }

    private fun getLayerFromCustomImageView(civ: CustomImageView): Layer {
        var layer = Layer()

        layer.layerNo = civ.layer
        layer.source = civ.iv.tag.toString()
        layer.marginLeft = (civ.layoutParams as RelativeLayout.LayoutParams).leftMargin
        layer.marginTop = (civ.layoutParams as RelativeLayout.LayoutParams).topMargin - viewHeader.measuredHeight
        layer.width = civ.measuredWidth
        layer.height = civ.measuredHeight

        return layer

    }

    private var imageAdapterCallback: ImagesAdapter.Callback = object : ImagesAdapter.Callback {
        override fun onDragStarted() {
            onDragStartedVisibilitiesHandler()
        }
    }

    private fun onDragStartedVisibilitiesHandler() {
        ivListArrow.visibility = View.GONE
        ViewUtils.hideToRight(rvImages)

        activity?.let {
            if (activity is MainActivity) {
                (activity as MainActivity).hideMenuButton()
            }
        }
    }

    /*
    * Camera related methods
     */

    fun openCamera(cameraId: Int): Boolean {

        var result = false
        camera?.releaseCameraAndPreview()
        camera = null
        try {
            camera = Camera.open(cameraId)

            Log.d("Camera", "Camera.open(id)")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Camera", " exception to openCamera from Camera.open(id)" + e.toString())
        }

        camera?.run {
            try {
                setUpCamera(this@CameraFragment, cameraId)
                setErrorCallback(Camera.ErrorCallback { error, camera ->
                    //to show the error message.
                })
                setPreviewDisplay(surfaceHolder)
                startPreview()
                cancelAutoFocus()
                cameraFocus()
                result = true
            } catch (e: java.io.IOException) {
                e.printStackTrace()
                result = false
                releaseCameraAndPreview()
            }

        }

        return result
    }

    private fun setOnTouchFocus() {
        surfaceView?.setOnTouchListener({ v, event ->
            if (!pictureTaken) {
                camera?.focusOnTouch(event, surfaceView, FOCUS_AREA_SIZE)
            }
            true
        })
    }

    private fun takeImage() {

        camera?.takePicture(null, null, Camera.PictureCallback { data1, camera ->

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                camera?.releaseCameraAndPreview()

                val options = BitmapFactory.Options()
                options.inScaled = true
                options.inSampleSize = 4
                options.inMutable = true
                val bmp = BitmapFactory.decodeByteArray(data1, 0, data1.size, options)
                val canvas = Canvas(bmp)

                surfaceView.draw(canvas)
            }

            photoTakenVisibilities()

            hideProgress()

            getPresenter().setImageData(data1)

        })
    }


    /* Camera related methods */

    internal var surfaceHolderCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            Log.v("Camera", " surfaceCreated")

            if (!openCamera(cameraId)) {
                Log.d("exceptie", "surfaceCreated exception")
            }
        }

        override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
            Log.v("Camera", " surfaceChanged")

        }

        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
            Log.v("Camera", " surfaceDestroyed")
            camera?.releaseCameraAndPreview()
            camera = null
        }

    }

}

