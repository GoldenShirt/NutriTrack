package com.nutritrack.ui.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutritrack.ai.GPTManager
import com.nutritrack.data.repository.MealRepository
import com.nutritrack.voice.VoiceInputManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MealInputUiState(
    val recognizedText: String = "",
    val isListening: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val nutritionInfo: NutritionInfo? = null
)

data class NutritionInfo(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int
)

@HiltViewModel
class MealInputViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mealRepository: MealRepository,
    private val gptManager: GPTManager,
    private val voiceInputManager: VoiceInputManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealInputUiState())
    val uiState: StateFlow<MealInputUiState> = _uiState.asStateFlow()

    init {
        // Listen to voice input results
        viewModelScope.launch {
            voiceInputManager.voiceResults.collect { result ->
                _uiState.update {
                    it.copy(
                        recognizedText = result,
                        isListening = false,
                        error = null
                    )
                }
            }
        }

        // Listen to voice input errors
        viewModelScope.launch {
            voiceInputManager.voiceErrors.collect { error ->
                _uiState.update {
                    it.copy(
                        isListening = false,
                        error = error
                    )
                }
            }
        }

        // Listen to listening state
        viewModelScope.launch {
            voiceInputManager.isListening.collect { isListening ->
                _uiState.update {
                    it.copy(isListening = isListening)
                }
            }
        }
    }

    fun startListening() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _uiState.update {
                it.copy(error = "אין הרשאה להקלטת קול")
            }
            return
        }

        _uiState.update {
            it.copy(
                error = null,
                recognizedText = ""
            )
        }
        voiceInputManager.startListening()
    }

    fun stopListening() {
        voiceInputManager.stopListening()
    }

    fun logMeal(description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            try {
                // Parse nutrition info from GPT
                val nutritionInfo = parseNutritionInfo(description)

                // Log meal to database
                mealRepository.logMealWithNutrition(description, nutritionInfo)

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        nutritionInfo = nutritionInfo
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = "שגיאה בשמירת הארוחה: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun parseNutritionInfo(description: String): NutritionInfo {
        val prompt = """
            נתח את המידע התזונתי של הארוחה הבאה: "$description"
            
            השב בפורמט JSON בדיוק כמו כך:
            {
                "calories": מספר_קלוריות,
                "protein": גרמי_חלבון,
                "carbs": גרמי_פחמימות,
                "fats": גרמי_שומנים
            }
            
            אם לא ברור, תעריך בהתבסס על מנות ממוצעות.
            השב רק ב-JSON, בלי הסברים נוספים.
        """.trimIndent()

        val response = gptManager.getAdvice(prompt)

        return try {
            parseNutritionFromJson(response)
        } catch (e: Exception) {
            // Fallback to default values if parsing fails
            NutritionInfo(
                calories = estimateCalories(description),
                protein = 15,
                carbs = 30,
                fats = 10
            )
        }
    }

    private fun parseNutritionFromJson(jsonResponse: String): NutritionInfo {
        // Simple JSON parsing - in production use a proper JSON library
        val cleanJson = jsonResponse.trim()
            .removePrefix("```json")
            .removeSuffix("```")
            .trim()

        val caloriesRegex = """"calories":\s*(\d+)""".toRegex()
        val proteinRegex = """"protein":\s*(\d+)""".toRegex()
        val carbsRegex = """"carbs":\s*(\d+)""".toRegex()
        val fatsRegex = """"fats":\s*(\d+)""".toRegex()

        val calories = caloriesRegex.find(cleanJson)?.groupValues?.get(1)?.toIntOrNull() ?: 200
        val protein = proteinRegex.find(cleanJson)?.groupValues?.get(1)?.toIntOrNull() ?: 15
        val carbs = carbsRegex.find(cleanJson)?.groupValues?.get(1)?.toIntOrNull() ?: 30
        val fats = fatsRegex.find(cleanJson)?.groupValues?.get(1)?.toIntOrNull() ?: 10

        return NutritionInfo(calories, protein, carbs, fats)
    }

    private fun estimateCalories(description: String): Int {
        // Simple estimation based on keywords
        val lowerDesc = description.lowercase()
        return when {
            lowerDesc.contains("סלט") -> 150
            lowerDesc.contains("פיצה") -> 400
            lowerDesc.contains("המבורגר") -> 500
            lowerDesc.contains("פסטה") -> 350
            lowerDesc.contains("אורז") -> 200
            lowerDesc.contains("לחם") -> 100
            lowerDesc.contains("עוף") -> 250
            lowerDesc.contains("דג") -> 200
            else -> 200
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceInputManager.cleanup()
    }
}