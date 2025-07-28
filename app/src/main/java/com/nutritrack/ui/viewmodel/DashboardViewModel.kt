package com.nutritrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutritrack.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadTodaysMeals()
    }

    private fun loadTodaysMeals() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            val meals = mealRepository.getMealsSince(startOfDay)

            // Mock calculation - in real app you'd parse nutrition from meal descriptions
            val totalCalories = meals.sumOf { it.calories ?: 0 }
            val totalProtein = meals.sumOf { it.protein ?: 0 }
            val totalCarbs = meals.sumOf { it.carbs ?: 0 }
            val totalFats = meals.sumOf { it.fats ?: 0 }

            _uiState.value = DashboardUiState(
                calories = totalCalories,
                protein = totalProtein,
                carbs = totalCarbs,
                fats = totalFats
            )
        }
    }
}