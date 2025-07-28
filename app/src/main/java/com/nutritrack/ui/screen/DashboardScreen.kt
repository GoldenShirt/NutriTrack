package com.nutritrack.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutritrack.ui.viewmodel.DashboardViewModel
import com.nutritrack.ui.viewmodel.MealInputViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    onChat: () -> Unit = {},
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    mealInputViewModel: MealInputViewModel = hiltViewModel()
) {
    val dashState by dashboardViewModel.uiState.collectAsState()
    val inputState by mealInputViewModel.uiState.collectAsState()
    var mealText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Update Dashboard when date changes
    LaunchedEffect(selectedDate) {
        dashboardViewModel.loadMealsForDate(selectedDate)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Date Selector Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Day")
            }
            Text(selectedDate.format(formatter), style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Day")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Nutrition Summary
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MacroCard("קלוריות", dashState.calories)
            MacroCard("חלבון", dashState.protein)
            MacroCard("פחמימות", dashState.carbs)
            MacroCard("שומנים", dashState.fats)
        }

        Spacer(Modifier.height(24.dp))

        // Inline Meal Input Section
        Text("הוסף ארוחה", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = inputState.recognizedText.takeIf { it.isNotBlank() } ?: mealText,
            onValueChange = {
                mealText = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("תאר את הארוחה...") }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    if (inputState.isListening) mealInputViewModel.stopListening()
                    else mealInputViewModel.startListening()
                },
                enabled = !inputState.isProcessing
            ) {
                Icon(
                    imageVector = if (inputState.isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice input"
                )
            }
            Button(
                onClick = {
                    val finalMealText = inputState.recognizedText.takeIf { it.isNotBlank() } ?: mealText
                    if (finalMealText.isNotBlank()) {
                        mealInputViewModel.logMeal(finalMealText)
                        mealText = ""
                        // Refresh dashboard after logging
                        dashboardViewModel.loadMealsForDate(selectedDate)
                    }
                },
                enabled = !inputState.isProcessing
            ) {
                if (inputState.isProcessing) CircularProgressIndicator(Modifier.size(16.dp))
                else Text("הוסף")
            }
        }
        inputState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        // List Meals for Selected Day
        Text("ארוחות ליום זה", style = MaterialTheme.typography.titleMedium)
        // Assuming dashState.meals is available
        if (dashState.meals.isNullOrEmpty()) {
            Text("אין ארוחות ליום זה.", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column {
                dashState.meals.forEach { meal ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(meal.description, style = MaterialTheme.typography.bodyLarge)
                            meal.nutrition?.let {
                                Text("קלוריות: ${it.calories}, חלבון: ${it.protein}, פחמימות: ${it.carbs}, שומנים: ${it.fats}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onChat, modifier = Modifier.align(Alignment.End)) {
            Text("צ'אט עם העוזר")
        }
    }
}

@Composable
fun MacroCard(label: String, value: Int) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(60.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}