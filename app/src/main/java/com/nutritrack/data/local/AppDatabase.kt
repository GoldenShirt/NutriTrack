package com.nutritrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

class AppDatabase {
    @Database(entities = [MealEntity::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun mealDao(): MealDao

        companion object {
            @Volatile private var INSTANCE: AppDatabase? = null
            fun get(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "nutritrack.db"
                    ).build().also { INSTANCE = it }
                }
        }
    }

}