package com.nutritrack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nutritrack.ui.viewmodel.NutritionInfo

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val description: String,
    val calories: Int? = null,
    val protein: Int? = null,
    val carbs: Int? = null,
    val fats: Int? = null
)

// Extension property to convert MealEntity to NutritionInfo
val MealEntity.nutrition: NutritionInfo?
    get() = if (calories != null && protein != null && carbs != null && fats != null) {
        NutritionInfo(
            calories = calories,
            protein = protein,
            carbs = carbs,
            fats = fats
        )
    } else {
        null
    }