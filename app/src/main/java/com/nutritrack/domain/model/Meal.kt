package com.nutritrack.domain.model

data class Meal(
    val id: Long = 0,
    val timestamp: Long,
    val description: String,
    val calories: Int? = null,
    val protein: Int? = null,
    val carbs: Int? = null,
    val fats: Int? = null
)