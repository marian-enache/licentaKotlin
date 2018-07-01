package com.marian.licenta.retrofit

import com.google.gson.annotations.Expose
import com.marian.licenta.room.models.Layer

/**
 * Created by Marian on 24.06.2018.
 */
class ApiResponse {

    @Expose
    var total: Int = -1

    @Expose
    var hits: List<Layer> = ArrayList()
}