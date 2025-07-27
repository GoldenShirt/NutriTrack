package com.nutritrack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

class MealEntity {
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

}