package com.marian.licenta.room.models

import android.arch.persistence.room.*
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.marian.licenta.extensions.createParcel

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

class Layer (@Expose @PrimaryKey(autoGenerate = true) var id : Int?,
             @ColumnInfo(name = "scene_id") var sceneId : Long?,
             @ColumnInfo(name = "layer_number") var layerNo : Int,
             @ColumnInfo(name = "margin_top") var marginTop: Int,
             @ColumnInfo(name = "margin_left") var marginLeft: Int,
             @ColumnInfo var width: Int,
             @ColumnInfo var height: Int,
             @Expose @SerializedName("webformatURL") @ColumnInfo(name = "image_url") var previewURL: String
): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString())


    @Ignore
    constructor() : this(null, null, 0, 0, 0, 0, 0, "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id ?: 0)
        parcel.writeLong(sceneId ?: 0)
        parcel.writeInt(layerNo)
        parcel.writeInt(marginTop)
        parcel.writeInt(marginLeft)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(previewURL)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { Layer(it) }
    }
}