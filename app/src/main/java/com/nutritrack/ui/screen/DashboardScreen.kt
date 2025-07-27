package com.nutritrack.ui.screen

import androidx.compose.runtime.Composable

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
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
        Button(onClick = { /* פתיחת ChatScreen */ }, modifier = Modifier.align(Alignment.End)) {
            Text("צ'אט עם העוזר")
        }
    }
}
