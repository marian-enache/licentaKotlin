package com.marian.licenta.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import android.widget.LinearLayout


/**
 * Created by Marian on 15.04.2018.
 */
class ViewUtils {

    companion object {


        fun expand(v: View) {
            v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.layoutParams.height = 1
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height = if (interpolatedTime == 1f)
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    else
                        (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            val duration = (targetHeight / v.context.resources.displayMetrics.density).toInt()
            a.duration = (2 * duration).toLong() // 0.5dp/ms
            v.startAnimation(a)
        }

        fun collapse(v: View) {
            val initialHeight = v.measuredHeight

            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            val duration = (initialHeight / v.context.resources.displayMetrics.density).toInt()
            a.duration = (2 * duration).toLong()  // 0.5dp/ms
            v.startAnimation(a)
        }


        fun showFromRight(v: View) {
            v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val targetWidth = v.measuredWidth

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.layoutParams.width = 1
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.width = if (interpolatedTime == 1f)
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    else
                        (targetWidth * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            val duration = (targetWidth / v.context.resources.displayMetrics.density).toInt()
            a.duration = (2 * duration).toLong() // 0.5dp/ms
            v.startAnimation(a)
        }


        fun hideToRight(v: View) {
            val initialWidth = v.measuredWidth


            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.width = initialWidth - (initialWidth * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            val duration = (initialWidth / v.context.resources.displayMetrics.density).toInt()
            a.duration = (2 * duration).toLong() // 0.5dp/ms
            v.startAnimation(a)
        }


        fun rotateFrom0to180(v : View) {
            val rotate = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotate.duration = 250
            rotate.interpolator = LinearInterpolator()
            rotate.fillAfter = true

            v.startAnimation(rotate)
        }

        fun rotateFrom180to0(v : View) {
            val rotate = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotate.duration = 250
            rotate.interpolator = LinearInterpolator()
            rotate.fillAfter = true

            v.startAnimation(rotate)
        }




    }
}