package com.marian.licenta.room.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Marian on 28.05.2018.
 */
@Entity(tableName = "scenes")
class Scene( @PrimaryKey(autoGenerate = false) var id : Long?,
    @ColumnInfo var name : String,
    @ColumnInfo(name = "layers_count") var layersCount : Int,
    @ColumnInfo(name = "background_image") var backgroundImage : String
    ) {

    @Ignore
    constructor() : this(null, "", 0, "")

    @Ignore
    var layers: List<Layer>? = null

}