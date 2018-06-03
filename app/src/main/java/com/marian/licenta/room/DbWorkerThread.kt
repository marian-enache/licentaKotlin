package com.marian.licenta.room

import android.os.Handler
import android.os.HandlerThread

/**
 * Created by Marian on 29.05.2018.
 */
class DbWorkerThread(threadName: String) : HandlerThread(threadName) {

    private var mWorkerHandler: Handler? = null

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mWorkerHandler = Handler(looper)
    }

    fun postTask(task: Runnable) {
        mWorkerHandler?.post(task)
    }

}