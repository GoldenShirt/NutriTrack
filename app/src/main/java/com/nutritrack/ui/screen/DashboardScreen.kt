package com.nutritrack.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutritrack.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onChat: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("צריכה יומית", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MacroCard("קלוריות", state.calories)
            MacroCard("חלבון", state.protein)
            MacroCard("פחמימות", state.carbs)
            MacroCard("שומנים", state.fats)
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