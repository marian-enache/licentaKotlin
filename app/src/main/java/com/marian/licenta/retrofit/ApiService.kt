package com.marian.licenta.retrofit

import com.marian.licenta.room.models.Layer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Marian on 24.06.2018.
 */
interface ApiService {
    @GET("api/")
    fun getLayersByWord(
            @Query("key") key: String,
            @Query("q") searchWords: String
    ): Call<ApiResponse>

    @GET
    fun getLayersByCategory(
            @Query("key") key: String,
            @Query("category") category: String
    ): Call<Layer>

    @GET
    fun getLayersByWordsAndCategory(
            @Query("key") key: String,
            @Query("q") searchWords: String,
            @Query("category") category: String
    ): Call<Layer>
}