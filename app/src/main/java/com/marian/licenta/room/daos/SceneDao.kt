package com.marian.licenta.room.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.marian.licenta.room.models.Scene
import com.marian.licenta.room.pojos.SceneView

/**
 * Created by Marian on 28.05.2018.
 */
@Dao
interface SceneDao : BaseDao<Scene> {

    @Query("SELECT * FROM scenes")
    fun getAll(): List<Scene>

    @Query("SELECT * FROM scenes WHERE id = :sceneId LIMIT 1")
    fun getById(sceneId: Long): Scene

    @Query("SELECT * FROM scenes ORDER BY id DESC")
    fun getSceneViews(): List<SceneView>

    @Query("SELECT * FROM scenes WHERE id = :sceneId LIMIT 1")
    fun getSceneView(sceneId: Long): SceneView

}