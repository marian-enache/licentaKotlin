package com.marian.licenta.utils

import android.os.AsyncTask

/**
 * Created by Marian on 29.05.2018.
 */
class GeneralAsyncTask(var callback: Callback) : AsyncTask<Any, Any, Any>() {
    interface Callback {
        fun onPostExecute()
        fun doInBackground()
    }

    override fun doInBackground(vararg p0: Any?) {
        callback?.doInBackground()
    }

    override fun onPostExecute(result: Any?) {
        callback?.onPostExecute()
    }
}
