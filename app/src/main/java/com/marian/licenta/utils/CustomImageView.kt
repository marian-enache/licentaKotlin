package com.marian.licenta.utils

import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.RelativeLayout
import com.marian.licenta.R


/**
 * Created by Marian on 19.04.2018.
 */
class CustomImageView : RelativeLayout {

    lateinit var iv: ImageView
        private set

    lateinit var callbacks: Callbacks

    private var runnable: Runnable? = null

    var layer: Int = 0

    interface Callbacks {
        fun onLayoutAttached()
    }

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setupView()
    }

    private fun setupView() {

        LayoutInflater.from(context).inflate(R.layout.custom_image_view, this, true)
        iv = findViewById(R.id.iv)

        iv.setOnClickListener{
            if (!borderShown()) {
                showBorder()
                showOptions()
                autoHideBorderAndOptions()
            } else {
                hideBorder()
                removeOptionsLayout()
            }
        }

        iv.setOnLongClickListener {

            hideAllBorders()
            removeOptionsLayout()

            val item = ClipData.Item(Uri.parse(iv.getTag().toString()))

            val data = ClipData.newPlainText("", "")
            data.addItem(item)
            val shadowBuilder = View.DragShadowBuilder(iv)
            iv.startDrag(data, shadowBuilder, iv, 0)

            if (this@CustomImageView.parent is RelativeLayout) {
                (this@CustomImageView.parent as RelativeLayout).removeView(this@CustomImageView)
            }

            true
        }

        viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                        } else {
                            viewTreeObserver.removeGlobalOnLayoutListener(this)
                        }

                        handler.removeCallbacksAndMessages(null)

                        callbacks?.let {
                            callbacks.onLayoutAttached()
                        }

                    }
                })

    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null)

        super.onDetachedFromWindow()
    }

    fun showBorder() {
        hideAllBorders()
        iv.background = ContextCompat.getDrawable(context, R.drawable.dotted_margin_background)
    }

    fun showOptions() {
        removeOptionsLayout()
        (parent as RelativeLayout).addView(CustomLayerOptionsLayout(context, this@CustomImageView))
    }

    fun hideBorder() {
        iv.background = null
    }

    fun hideAllBorders() {
        var rlParent = parent as RelativeLayout
        var child: View
        for (i in 0 until rlParent.childCount) {
            child = rlParent.getChildAt(i)
            if (child is CustomImageView && child.borderShown()) {
                child.hideBorder()
            }
        }
    }

    fun removeOptionsLayout() {
        var rlParent = parent as RelativeLayout
        var child: View?
        for (i in 0 until rlParent.childCount) {
            child = rlParent.getChildAt(i)
            if (child != null && child is CustomLayerOptionsLayout) {
                rlParent.removeView(child)
            }
        }
    }

    private fun borderShown() : Boolean {
        return iv.background != null
    }

    private fun autoHideBorderAndOptions() {
        handler.removeCallbacksAndMessages(null)


        runnable = Runnable {
            try {
                hideAllBorders()
                removeOptionsLayout()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        handler.postDelayed(runnable, 5 * 1000)

    }

    fun getAllLayersCount(): Int {
        var layersCount = 0
        var rlParent = parent as RelativeLayout

        for (i in 0 until rlParent.childCount) {
            if (rlParent.getChildAt(i) is CustomImageView) {
                layersCount++
            }
        }
        return layersCount
    }

    fun setLayerNo(finalLayer: Int) {
        reorderLayersAfterChanging(finalLayer, layer)
        layer = finalLayer
    }

    private fun reorderLayersAfterChanging(finalLayer: Int, initialLayer: Int) {
        var rlParent = parent as RelativeLayout

        var child: View
        if (initialLayer < finalLayer) {
            for (i in 0 until rlParent.childCount) {
                child = rlParent.getChildAt(i)
                if (child is CustomImageView) {
                    if (child.layer in (initialLayer + 1)..finalLayer) {
                        child.layer--
                    }
                }
            }
        }
        if (initialLayer > finalLayer) {
            for (i in 0 until rlParent.childCount) {
                child = rlParent.getChildAt(i)
                if (child is CustomImageView) {
                    if (child.layer in finalLayer until initialLayer) {
                        child.layer++
                    }
                }
            }
        }

    }


    fun softHideControls() {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator() //and this
        fadeOut.startOffset = 1000
        fadeOut.setDuration(1000)

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {}

            override fun onAnimationStart(p0: Animation?) {
                if (borderShown()) {
                    hideBorder()
                    removeOptionsLayout()
                }
            }
        })
    }

}