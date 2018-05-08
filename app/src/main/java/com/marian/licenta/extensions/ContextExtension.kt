package com.marian.licenta.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.widget.Toast

/**
 * Created by Marian on 23.03.2018.
 */

fun Context.drawable(@DrawableRes res: Int): Drawable? = ContextCompat.getDrawable(this, res)

fun Context.color(@ColorRes res: Int): Int = ContextCompat.getColor(this, res)

fun Context.shortToast(text : String) = Toast.makeText(this, text, Toast.LENGTH_SHORT)

fun Context.longToast(text : String) = Toast.makeText(this, text, Toast.LENGTH_LONG)

fun Context.dpFromPx(px: Float): Float = px / resources.displayMetrics.density

fun Context.pxFromDp(dp: Float): Float = dp * resources.displayMetrics.density
