package com.marian.licenta.extensions

import android.graphics.Rect
import android.hardware.Camera
import android.support.v4.app.Fragment
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import java.util.*

/**
 * Created by Marian on 09.04.2018.
 */


fun Camera?.openCamera(fragment : Fragment, cameraId: Int, surfaceHolder : SurfaceHolder) : Boolean {
    var result = false
    this?.releaseCameraAndPreview()
    try {
        this.apply {

            Camera.open(cameraId)

        }

        Log.d("Camera", "Camera.open(id)")
    } catch (e: Exception) {
        e.printStackTrace()
        Log.d("Camera", " exception to openCamera from Camera.open(id)" + e.toString())
    }

    this?.run {
        try {
            setUpCamera(fragment, cameraId)
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


fun Camera.cameraFocus() {
    try {
        this?.startPreview()
        this?.autoFocus(Camera.AutoFocusCallback { success, camera ->
            if (camera.parameters.focusMode == Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                val parameters = camera.parameters
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

                //                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                if (parameters.maxNumFocusAreas > 0) {
                    parameters.focusAreas = null
                }
                camera.parameters = parameters
                camera.startPreview()
            }
        })
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun Camera.releaseCameraAndPreview() {
    try {
        this?.setPreviewCallback(null)
        this?.setErrorCallback(null)
        this?.stopPreview()
        this?.release()
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("error", e.toString())
    }

}

fun Camera.focusOnTouch(event: MotionEvent,  view : View, focusAreaSize : Int) {
    this?.let {
        this.cancelAutoFocus()
        val focusRect = calculateFocusArea(event.x, event.y, view, focusAreaSize)

        val parameters = this.parameters
        if (parameters?.focusMode == Camera.Parameters.FOCUS_MODE_AUTO) {
            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }

        if (parameters?.maxNumFocusAreas!! > 0) {
            val mylist = ArrayList<Camera.Area>()
            mylist.add(Camera.Area(focusRect, 1000))
            parameters?.focusAreas = mylist
        }
        this?.cancelAutoFocus()
        parameters?.let {
            try {
                this?.parameters = parameters
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        this.cameraFocus()

    }
}


fun Camera.setUpCamera(fragment : Fragment, cameraId: Int) {
    var rotation : Int
    val info = Camera.CameraInfo()
    Camera.getCameraInfo(cameraId, info)
    rotation = fragment?.view?.rotation?.toInt()!!

    var degree = 0
    when (rotation) {
        Surface.ROTATION_0 -> degree = 0
        Surface.ROTATION_90 -> degree = 90
        Surface.ROTATION_180 -> degree = 180
        Surface.ROTATION_270 -> degree = 270

        else -> {
        }
    }

    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        // frontFacing
        rotation = (info.orientation + degree) % 330
        rotation = (360 - rotation) % 360
    } else {
        // Back-facing
        rotation = (info.orientation - degree + 360) % 360
    }
    this.setDisplayOrientation(rotation)
    val params = this.parameters

    val focusModes = params.supportedFlashModes
    if (focusModes != null) {
        if (focusModes
                        .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.flashMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        }
    }

    params.setRotation(rotation)

}

fun calculateFocusArea(x: Float, y: Float, view : View, focusAreaSize : Int): Rect {
    val left = clamp(java.lang.Float.valueOf(x / view?.getWidth()!! * 2000 - 1000)!!.toInt(), focusAreaSize)
    val top = clamp(java.lang.Float.valueOf(y / view?.getHeight()!! * 2000 - 1000)!!.toInt(), focusAreaSize)

    return Rect(left, top, left + focusAreaSize, top + focusAreaSize)
}


fun clamp(touchCoordinateInCameraReper: Int, focusAreaSize: Int): Int {
    val result: Int
    if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
        if (touchCoordinateInCameraReper > 0) {
            result = 1000 - focusAreaSize / 2
        } else {
            result = -1000 + focusAreaSize / 2
        }
    } else {
        result = touchCoordinateInCameraReper - focusAreaSize / 2
    }
    return result
}


