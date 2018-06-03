package com.marian.licenta.utils

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.marian.licenta.R

/**
 * Created by Marian on 29.05.2018.
 */
class CustomSceneAcceptanceOptions : LinearLayout {

    private lateinit var tvCancel: TextView
    private lateinit var tvOk: TextView

    lateinit var callbacks: Callbacks

    interface Callbacks {
        fun onOk()
        fun onCancel()

    }

    constructor(context: Context) : super(context) {
        setupView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setupView()
    }

    private fun setupView() {
        LayoutInflater.from(context).inflate(R.layout.custom_scene_acceptance_options, this, true)

        tvCancel = findViewById(R.id.tvCancel)
        tvOk = findViewById(R.id.tvOk)

        tvCancel.setOnClickListener {
            callbacks?.let {
                callbacks.onCancel()
            }
        }

        tvOk.setOnClickListener {
            callbacks?.let {
                callbacks.onOk()
            }
        }

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                }

                var params = layoutParams as RelativeLayout.LayoutParams
                params.width = RelativeLayout.LayoutParams.MATCH_PARENT
                layoutParams = params

            }
        })
    }

}
