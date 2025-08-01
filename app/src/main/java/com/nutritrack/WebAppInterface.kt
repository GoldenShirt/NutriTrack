package com.nutritrack

import android.webkit.JavascriptInterface

/**
 * Instantiate the interface and set the context.
 * @param onSettingsClickedAction Lambda to be invoked when settings is clicked from JS.
 * @param onNutritionChatClickedAction Lambda to be invoked when nutrition chat is clicked from JS.
 */
class WebAppInterface(
    private val onSettingsClickedAction: () -> Unit,
    private val onNutritionChatClickedAction: () -> Unit
) {

    @JavascriptInterface
    fun onSettingsClicked() {
        onSettingsClickedAction()
    }

    @JavascriptInterface
    fun onNutritionChatClicked() {
        onNutritionChatClickedAction()
    }
}