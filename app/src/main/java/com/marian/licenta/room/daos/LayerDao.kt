package com.marian.licenta.room.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.marian.licenta.room.models.Layer

/**
 * Created by Marian on 29.05.2018.
 */
@Dao
interface LayerDao : BaseDao<Layer> {
    @Query("SELECT * FROM layers")
    fun getAll(): List<Layer>

    @Query("SELECT * FROM layers WHERE id = :layerId LIMIT 1")
    fun getById(layerId: Int): Layer

    @Query("SELECT * FROM Layers WHERE scene_id = :sceneId")
    fun getLayersForScene(sceneId : Int) : List<Layer>

}