package com.marian.licenta.extensions

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Marian on 06.06.2018.
 */
// Inline function to create Parcel Creator
inline fun <reified T : Parcelable> createParcel(crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
            override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
        }

// custom readParcelable to avoid reflection
fun <T : Parcelable> Parcel.readParcelable(creator: Parcelable.Creator<T>): T? {
    if (readString() != null) return creator.createFromParcel(this) else return null
}