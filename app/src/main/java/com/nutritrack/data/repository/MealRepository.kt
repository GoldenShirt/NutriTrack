package com.nutritrack.data.repository

import com.nutritrack.data.local.MealDao
import com.nutritrack.data.local.MealEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {
    suspend fun logMeal(description: String) {
        val meal = MealEntity(
            timestamp = System.currentTimeMillis(),
            description = description
        )
        mealDao.insert(meal)
    }

    suspend fun getMealsSince(timestamp: Long): List<MealEntity> {
        return mealDao.getMealsSince(timestamp)
    }
}