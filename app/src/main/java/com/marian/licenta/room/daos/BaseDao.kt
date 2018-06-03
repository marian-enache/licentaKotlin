package com.marian.licenta.room.daos

import android.arch.persistence.room.*

/**
 * Created by Marian on 31.05.2018.
 */
@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T)

    @Update
    fun update(item: T)

    @Delete
    fun delete(item: T)


}