package com.marian.licenta.room.pojos

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.marian.licenta.room.models.Layer
import com.marian.licenta.room.models.Scene

/**
 * Created by Marian on 30.05.2018.
 */
class SceneView(
        @Embedded var scene: Scene? = null,
        @Relation(parentColumn = "id", entityColumn = "scene_id", entity = Layer::class)
        var layers: List<Layer> = ArrayList()
)