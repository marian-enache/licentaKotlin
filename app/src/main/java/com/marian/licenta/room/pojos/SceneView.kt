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
)/* : Parcelable*/ {

//    constructor(parcel: Parcel) : this(
//            parcel.readParcelable<Scene>(Scene::class.java.classLoader),
//            arrayListOf<Layer>().apply {
//                parcel.readList(this, Layer::class.java.classLoader)
//            }
//    )
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeParcelable(scene, flags)
//        parcel.writeList(layers)
//    }
//
//    override fun describeContents(): Int = 0
//
//
//    companion object CREATOR : Parcelable.Creator<SceneView> {
//        override fun createFromParcel(parcel: Parcel) = SceneView(parcel)
//
//        override fun newArray(size: Int): Array<SceneView?> = arrayOfNulls(size)
//
//    }
}