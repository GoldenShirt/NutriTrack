package com.nutritrack.domain.usecase

import com.nutritrack.data.repository.MealRepository
import javax.inject.Inject

class LogMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(description: String) {
        mealRepository.logMeal(description)
    }
}