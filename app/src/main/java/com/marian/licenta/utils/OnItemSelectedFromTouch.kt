package com.marian.licenta.utils

import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView

/**
 * Created by Marian on 04.06.2018.
 */
abstract class OnItemSelectedFromTouch: AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private var isFromTouch: Boolean = false

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

        isFromTouch = true

        return true
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>? , view: View?, position: Int, id: Long) {
        if (isFromTouch) {
            onItemSelectedAction(parent, view, position, id)
        }
    }

    abstract fun onItemSelectedAction(parent: AdapterView<*>? , view: View?, position: Int, id: Long)
}