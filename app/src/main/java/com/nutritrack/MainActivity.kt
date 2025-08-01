package com.nutritrack

import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.nutritrack.ui.theme.NutriTrackTheme

class MainActivity : ComponentActivity() {
    lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.setWebContentsDebuggingEnabled(true) // Enable WebView debugging
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                WebViewScreen(url = "http://192.168.31.217:3000",
                    onWebViewCreated = { createdWebView ->
                        webView = createdWebView
                    }
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

@Composable
fun WebViewScreen(url: String, modifier: Modifier = Modifier, onWebViewCreated: (WebView) -> Unit) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            WebView(it).apply {
                settings.javaScriptEnabled = true // Enable JavaScript
                settings.domStorageEnabled = true // Enable DOM Storage (localStorage, sessionStorage)
                
                webViewClient = WebViewClient() // Standard WebViewClient
                
                loadUrl(url)
                onWebViewCreated(this)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}
