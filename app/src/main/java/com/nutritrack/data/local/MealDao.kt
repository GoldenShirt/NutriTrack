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
}