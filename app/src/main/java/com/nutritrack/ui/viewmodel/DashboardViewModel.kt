package com.nutritrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutritrack.data.repository.MealRepository
import com.nutritrack.data.local.MealEntity
import com.nutritrack.data.local.nutrition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class DashboardUiState(
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val meals: List<MealEntity> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadMealsForDate(LocalDate.now())
    }

    fun loadMealsForDate(date: LocalDate) {
        viewModelScope.launch {
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000

            val meals = mealRepository.getMealsBetween(startOfDay, endOfDay)

            // Explicitly ensure the lambda returns an Int
            val totalCalories = meals.sumOf { (it.nutrition?.calories ?: 0).toInt() }
            val totalProtein = meals.sumOf { (it.nutrition?.protein ?: 0).toInt() }
            val totalCarbs = meals.sumOf { (it.nutrition?.carbs ?: 0).toInt() }
            val totalFats = meals.sumOf { (it.nutrition?.fats ?: 0).toInt() }


            _uiState.value = DashboardUiState(
                calories = totalCalories,
                protein = totalProtein,
                carbs = totalCarbs,
                fats = totalFats,
                meals = meals
            )
        }
    }
}