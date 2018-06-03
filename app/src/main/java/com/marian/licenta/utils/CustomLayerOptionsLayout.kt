package com.marian.licenta.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.marian.licenta.R

/**
 * Created by Marian on 03.06.2018.
 */
class CustomLayerOptionsLayout : RelativeLayout {

    lateinit var ivDelete: ImageView
    lateinit var ivZoom: ImageView
    lateinit var ivRotate: ImageView
    lateinit var spinnerLayer: Spinner

    constructor(context: Context) : super(context) {
        setupView(null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setupView(null)
    }

    constructor(context: Context, customImageView: CustomImageView) : super(context) {
        setupView(customImageView)
    }

    private fun setLayerSpinner(customImageView: CustomImageView){

        val layersList = ArrayList<String>()
        for (i in 1..customImageView.getAllLayersCount()) {
            layersList.add("Layer $i")
        }

        var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.spinner_text_view, layersList)

        spinnerLayer.adapter = spinnerAdapter
        spinnerAdapter.notifyDataSetChanged()

        spinnerLayer.setSelection(customImageView.layer - 1)

        spinnerLayer.onItemSelectedListener =object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>? , view: View?, position: Int, id: Long) {
                customImageView.setLayerNo(position + 1)
            }

        }
    }

    private fun setupView(customImageView: CustomImageView?) {
        LayoutInflater.from(context).inflate(R.layout.custom_layer_options, this, true)

        ivDelete = findViewById(R.id.ivDelete)
        ivZoom = findViewById(R.id.ivZoom)
        ivRotate = findViewById(R.id.ivRotate)
        spinnerLayer = findViewById(R.id.spinnerLayout)

        ivDelete.setOnClickListener{v ->
            (customImageView?.parent as RelativeLayout).removeView(customImageView)
            (parent as RelativeLayout).removeView(this)
        }

        customImageView?.let {
            setLayerSpinner(customImageView)
        }

    }

}