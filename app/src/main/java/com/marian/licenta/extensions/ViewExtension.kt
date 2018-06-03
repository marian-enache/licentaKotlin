package com.marian.licenta.extensions

import android.view.View
import android.view.ViewTreeObserver

/**
 * Created by Marian on 02.06.2018.
 */
inline fun <T: View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
//            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
//            }
        }
    })
}