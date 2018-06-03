package com.marian.licenta

import android.app.Application
import com.facebook.stetho.Stetho
import com.marian.licenta.room.AppDatabase

/**
 * Created by Marian on 23.03.2018.
 */
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    var surfaceViewWidth: Int? = 0

    var surfaceViewHeight: Int? = 0

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this)
        AppDatabase.getInstance(context = this)

    }


}