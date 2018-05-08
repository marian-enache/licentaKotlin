package com.marian.licenta.extensions

import android.content.Context
import com.marian.licenta.ui.activities.main.MainActivity
import org.jetbrains.anko.intentFor

/**
 * Created by Marian on 23.03.2018.
 */

fun Context.gotToMainAztivity() {
    startActivity(intentFor<MainActivity>())
}