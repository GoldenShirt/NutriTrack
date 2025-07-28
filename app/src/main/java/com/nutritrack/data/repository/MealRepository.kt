package com.nutritrack.data.repository

import com.nutritrack.data.local.MealDao
import com.nutritrack.data.local.MealEntity
import com.nutritrack.ui.viewmodel.NutritionInfo
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

    suspend fun logMealWithNutrition(description: String, nutritionInfo: NutritionInfo) {
        val meal = MealEntity(
            timestamp = System.currentTimeMillis(),
            description = description,
            calories = nutritionInfo.calories,
            protein = nutritionInfo.protein,
            carbs = nutritionInfo.carbs,
            fats = nutritionInfo.fats
        )
        mealDao.insert(meal)
    }

    suspend fun getMealsSince(timestamp: Long): List<MealEntity> {
        return mealDao.getMealsSince(timestamp)
    }

    suspend fun getTodaysMeals(): List<MealEntity> {
        val startOfDay = getTodayStartTimestamp()
        return mealDao.getMealsSince(startOfDay)
    }

    private fun getTodayStartTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Add this function:
    suspend fun getMealsBetween(startMillis: Long, endMillis: Long): List<MealEntity> {
        return mealDao.getMealsBetween(startMillis, endMillis)
    }
}