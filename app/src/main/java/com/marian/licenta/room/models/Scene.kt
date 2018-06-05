package com.marian.licenta.room.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Marian on 28.05.2018.
 */
@Entity(tableName = "scenes")
class Scene( @PrimaryKey(autoGenerate = false) var id : Long?,
    @ColumnInfo var name : String,
    @ColumnInfo(name = "layers_count") var layersCount : Int,
    @ColumnInfo(name = "background_image") var backgroundImage : String
    ): Parcelable {

    @Ignore
    constructor() : this(null, "", 0, "")

    @Ignore
    var layers: List<Layer>? = null

    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()) {

        arrayListOf<Layer>().apply {
            parcel.readList(this, Layer::class.java.classLoader)
        }
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeInt(layersCount)
        parcel.writeString(backgroundImage)
        parcel.writeList(layers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Scene> {
        override fun createFromParcel(parcel: Parcel): Scene {
            return Scene(parcel)
        }

        override fun newArray(size: Int): Array<Scene?> {
            return arrayOfNulls(size)
        }
    }

}