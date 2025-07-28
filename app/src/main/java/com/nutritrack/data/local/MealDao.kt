package com.nutritrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE timestamp >= :from")
    suspend fun getMealsSince(from: Long): List<MealEntity>

    // Add this function:
    @Query("SELECT * FROM meals WHERE timestamp >= :startMillis AND timestamp < :endMillis")
    suspend fun getMealsBetween(startMillis: Long, endMillis: Long): List<MealEntity>
}