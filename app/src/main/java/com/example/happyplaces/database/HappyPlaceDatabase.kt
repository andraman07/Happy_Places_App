package com.example.happyplaces.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [HappyPlaceEntity::class])
abstract class HappyPlaceDatabase: RoomDatabase() {

    abstract fun happyPlaceDao():HappyPlaceDao

    companion object {

        @Volatile
        private var INSTANCE: HappyPlaceDatabase? = null

        fun getInstance(context: Context): HappyPlaceDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HappyPlaceDatabase::class.java,
                        "employee-Database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE=instance
                }
                return instance
            }
        }
    }

}