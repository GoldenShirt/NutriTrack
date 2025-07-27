package com.nutritrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutritrack.ai.GPTManager
import com.nutritrack.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val gpt: GPTManager,
    private val mealRepo: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onUserMessage(text: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val reply = gpt.getAdvice(text)
            _uiState.update {
                it.copy(messages = it.messages + ChatMsg.User(text) + ChatMsg.Bot(reply), isLoading = false)
            }
        }
    }

    fun logMeal(text: String) {
        viewModelScope.launch {
            mealRepo.logMeal(text)
        }
    }
}

class ChatViewModel {
}