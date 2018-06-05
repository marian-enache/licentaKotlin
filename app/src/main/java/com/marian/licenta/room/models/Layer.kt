package com.marian.licenta.room.models

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Marian on 28.05.2018.
 */
@Entity(tableName = "layers",
        indices = arrayOf(Index(value = "scene_id", name = "idx_layer_scene_id")),
        foreignKeys = arrayOf(ForeignKey(entity = Scene::class,
                                         parentColumns = arrayOf("id"),
                                         childColumns = arrayOf("scene_id"),
                                         onDelete = ForeignKey.CASCADE,
                                         onUpdate = ForeignKey.CASCADE
                                         )
                             )
        )

class Layer (@PrimaryKey(autoGenerate = true) var id : Int?,
             @ColumnInfo(name = "scene_id") var sceneId : Long?,
             @ColumnInfo var source : String?,
             @ColumnInfo(name = "layer_number") var layerNo : Int,
             @ColumnInfo(name = "margin_top") var marginTop: Int,
             @ColumnInfo(name = "margin_left") var marginLeft: Int,
             @ColumnInfo var width: Int,
             @ColumnInfo var height: Int): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()) {
    }

    @Ignore
    constructor() : this(null, null, null, 0, 0, 0, 0, 0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(sceneId)
        parcel.writeString(source)
        parcel.writeInt(layerNo)
        parcel.writeInt(marginTop)
        parcel.writeInt(marginLeft)
        parcel.writeInt(width)
        parcel.writeInt(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Layer> {
        override fun createFromParcel(parcel: Parcel): Layer {
            return Layer(parcel)
        }

        override fun newArray(size: Int): Array<Layer?> {
            return arrayOfNulls(size)
        }
    }
}