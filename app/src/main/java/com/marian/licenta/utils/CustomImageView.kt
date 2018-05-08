package com.marian.licenta.utils

import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import com.marian.licenta.R

/**
 * Created by Marian on 19.04.2018.
 */
class CustomImageView : RelativeLayout {


    lateinit var iv: ImageView
        private set
    lateinit var ivRemove: ImageView
        private set
    lateinit var ivExpand: ImageView
        private set
    lateinit var ivRotate: ImageView
        private set
    lateinit var spinnerLayer: Spinner
        private set

    lateinit var callbacks: Callbacks

    interface Callbacks {
        fun onLayoutAttached()
        fun onDragStarted()

    }

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setupView()
    }

    private fun setupView() {

        LayoutInflater.from(context).inflate(R.layout.custom_image_view_with_options, this, true)
        iv = findViewById(R.id.iv)

        ivRemove = findViewById(R.id.ivRemove)
        ivExpand = findViewById(R.id.ivExpand)
        ivRotate = findViewById(R.id.ivRotate)
        spinnerLayer = findViewById(R.id.spinnerLayer)

        ivRemove.setOnClickListener(OnClickListener {
            if (this.parent is RelativeLayout) {
                (this.parent as RelativeLayout).removeView(this)
            }
        })

        iv.setOnLongClickListener {

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

                        callbacks?.let {
                            callbacks.onLayoutAttached()
                        }

                    }
                })

    }


}