package com.crp.wikiAppNew.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.crp.wikiAppNew.dao.PlaceDao
import com.crp.wikiAppNew.datamodel.PlaceEntity
import com.crp.wikiAppNew.utils.Converters

@Database(entities = [PlaceEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun PlaceDao(): PlaceDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "places-database"
                    ).build()
                }
            }
            return instance!!
        }
    }
}