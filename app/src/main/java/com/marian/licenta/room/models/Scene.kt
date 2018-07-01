package com.marian.licenta.room.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.marian.licenta.extensions.createParcel

/**
 * Created by Marian on 28.05.2018.
 */
@Entity(tableName = "scenes")
class Scene(@PrimaryKey(autoGenerate = false) var id: Long?,
            @ColumnInfo var name: String,
            @ColumnInfo(name = "layers_count") var layersCount: Int,
            @ColumnInfo(name = "background_image") var backgroundImage: String,
            @ColumnInfo(name = "frame_margin_left") var frameMarginLeft: Int,
            @ColumnInfo(name = "frame_margin_top") var frameMarginTop: Int,
            @Ignore var layers: List<Layer>
) : Parcelable {


    constructor() : this(null, "", 0, "", 0, 0, listOf<Layer>())


    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            mutableListOf<Layer>().apply {
                parcel.readTypedList(this, Layer.CREATOR)
            }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id ?: 0)
        parcel.writeString(name)
        parcel.writeInt(layersCount)
        parcel.writeString(backgroundImage)
        parcel.writeInt(frameMarginLeft)
        parcel.writeInt(frameMarginTop)
        parcel.writeTypedList(layers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { Scene(it) }
    }

}