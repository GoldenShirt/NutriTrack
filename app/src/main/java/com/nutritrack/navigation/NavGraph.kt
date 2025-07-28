package com.nutritrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nutritrack.ui.screen.ChatScreen
import com.nutritrack.ui.screen.DashboardScreen

@Composable
fun NavGraph(startDestination: String = "dashboard") {
    val nav = rememberNavController()
    NavHost(nav, startDestination) {
        composable("dashboard") { DashboardScreen(onChat = { nav.navigate("chat") }) }
        composable("chat") { ChatScreen() }
        // add more routes…
    }
}