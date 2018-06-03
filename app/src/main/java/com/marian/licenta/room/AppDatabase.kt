package com.marian.licenta.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.marian.licenta.room.daos.LayerDao
import com.marian.licenta.room.daos.SceneDao
import com.marian.licenta.room.models.Layer
import com.marian.licenta.room.models.Scene



/**
 * Created by Marian on 29.05.2018.
 */
@Database(entities = arrayOf(Scene::class, Layer::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sceneDao() : SceneDao
    abstract fun layerDao() : LayerDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    if (INSTANCE == null) {
                        // Create database here
                        INSTANCE = Room.databaseBuilder(context,
                                AppDatabase::class.java!!, "wallpapers-db")
                                .build()
                    }
                }
            }
            return INSTANCE

        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

