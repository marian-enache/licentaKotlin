package com.marian.licenta.ui.fragments

import android.graphics.Canvas
import android.hardware.Camera
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.DragEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.marian.licenta.R
import com.marian.licenta.adapters.images.ImagesAdapter
import com.marian.licenta.base.mvp.BaseMvpFragment
import com.marian.licenta.extensions.cameraFocus
import com.marian.licenta.extensions.focusOnTouch
import com.marian.licenta.extensions.releaseCameraAndPreview
import com.marian.licenta.extensions.setUpCamera
import com.marian.licenta.ui.activities.main.MainActivity
import com.marian.licenta.utils.CustomImageView
import com.marian.licenta.utils.ViewUtils
import java.io.FileNotFoundException


class CameraFragment : BaseMvpFragment<CameraContract.Presenter>(), CameraContract.View, View.OnClickListener {


    companion object {
        open val TAG: String = CameraFragment.javaClass.simpleName

        private val FOCUS_AREA_SIZE = 300

        open fun newInstance(): CameraFragment = CameraFragment()
    }

    private var cameraId: Int = Camera.CameraInfo.CAMERA_FACING_BACK

    private lateinit var ivListArrow: ImageView
    private lateinit var rvImages: RecyclerView
    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var rlContainer: RelativeLayout
    var camera: Camera? = null

    private lateinit var imagesAdapter: ImagesAdapter


    private lateinit var layers: ArrayList<Map<Canvas, Int>>

    override fun initViews(view: View?) {

        layers = ArrayList()

        ivListArrow = view?.findViewById(R.id.ivListArrow)!!
        rvImages = view?.findViewById(R.id.rvImages)!!
        surfaceView = view?.findViewById(R.id.surfaceView)!!
        rlContainer = view?.findViewById(R.id.rlContainer)!!
        surfaceHolder = surfaceView?.holder

        surfaceHolder?.addCallback(shCallback)

        setOnTouchFocus()

        rvImages.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        imagesAdapter = ImagesAdapter(getPresenter() as CameraPresenter)
        imagesAdapter.callback = iaCallback
        rvImages.adapter = imagesAdapter

        ivListArrow.setOnClickListener({ v ->
            if (rvImages.visibility == View.VISIBLE) {
                ViewUtils.hideToRight(rvImages)
                ViewUtils.rotateFrom0to180(ivListArrow)
            } else {
                ViewUtils.showFromRight(rvImages)
                ViewUtils.rotateFrom180to0(ivListArrow)
            }

        })

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
                            (activity as MainActivity).onImageDragFinished()
                        }
                    }

                    val item = dragEvent.clipData.getItemAt(1)
                    val uri = item.uri

                    var civDraggedImage = CustomImageView(view.context)

                    civDraggedImage.callbacks = object : CustomImageView.Callbacks {
                        override fun onLayoutAttached() {
                            val width = civDraggedImage.getMeasuredWidth()
                            val height = civDraggedImage.getMeasuredHeight()

                            var params = civDraggedImage.layoutParams as RelativeLayout.LayoutParams

                            params.leftMargin = dragEvent.x.toInt() - width / 2
                            params.topMargin = dragEvent.y.toInt() - height / 2
                            civDraggedImage.layoutParams = params
                        }

                        override fun onDragStarted() {
                            onDragStartedVisibilitiesHandler()
                        }

                    }

                    try {
                        civDraggedImage.iv.setImageDrawable(ContextCompat.getDrawable(context, uri.toString().toInt()))
                        civDraggedImage.iv.setTag(uri.toString())
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }

                    civDraggedImage.hideControls()

                    rlContainer.addView(civDraggedImage)

                }
                DragEvent.ACTION_DRAG_ENDED -> {
                }
            }
            true
//        }
        }

    }


    private fun setOnTouchFocus() {
        surfaceView?.setOnTouchListener({ v, event ->
            camera?.focusOnTouch(event, surfaceView, FOCUS_AREA_SIZE)
            true
        })
    }

    @LayoutRes
    override fun bindLayout(): Int {
        return R.layout.fragment_camera
    }

    override fun bindPresenter(): CameraContract.Presenter {
        return CameraPresenter(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
        }
    }

    override fun onPause() {
        super.onPause()
        camera?.releaseCameraAndPreview()
        camera = null
    }

    private var iaCallback: ImagesAdapter.Callback = object : ImagesAdapter.Callback {
        override fun onDragStarted() {
            onDragStartedVisibilitiesHandler()

        }
    }

    private fun onDragStartedVisibilitiesHandler() {
        ivListArrow.visibility = View.GONE
        ViewUtils.hideToRight(rvImages)

        activity?.let {
            if (activity is MainActivity) {
                (activity as MainActivity).onImageDragStarted()
            }
        }
    }

    internal var shCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
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


}

