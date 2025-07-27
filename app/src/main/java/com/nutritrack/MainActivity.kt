package com.nutritrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// import androidx.camera.core.Preview // Remove this if not used elsewhere
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
// import androidx.navigation.NavGraph // You'll need to import or define NavGraph
import com.nutritrack.ui.theme.NutriTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                // If NavGraph is your main content, use it here
                // NavGraph()
                // Or, if Greeting is part of your initial layout within a NavGraph,
                // you might have a different structure.
                // For now, let's assume you want to display the Greeting first
                // or NavGraph handles the Scaffold itself.

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    // If NavGraph is meant to be the content *inside* the Scaffold:
                    // NavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Remove this duplicate setContent block
// setContent {
//    NutriTrackTheme {
//        NavGraph()
//    }
// }


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NutriTrackTheme {
        Greeting("Android")
    }
}

// TODO: Define your NavGraph composable function here or import it
// For example:
// @Composable
// fun NavGraph(modifier: Modifier = Modifier) {
//    // Your navigation graph setup using NavHost
// }
