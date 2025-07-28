package com.nutritrack.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceInputManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null

    private val _voiceResults = MutableSharedFlow<String>()
    val voiceResults: SharedFlow<String> = _voiceResults.asSharedFlow()

    private val _voiceErrors = MutableSharedFlow<String>()
    val voiceErrors: SharedFlow<String> = _voiceErrors.asSharedFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _isListening.value = true
        }

        override fun onBeginningOfSpeech() {
            // Speech input detected
        }

        override fun onRmsChanged(rmsdB: Float) {
            // Voice level changed - could be used for voice visualization
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            // Partial audio data received
        }

        override fun onEndOfSpeech() {
            _isListening.value = false
        }

        override fun onError(error: Int) {
            _isListening.value = false
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "שגיאה באודיו"
                SpeechRecognizer.ERROR_CLIENT -> "שגיאה בלקוח"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "אין הרשאות מספיקות"
                SpeechRecognizer.ERROR_NETWORK -> "שגיאת רשת"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "זמן המתנה לרשת פג"
                SpeechRecognizer.ERROR_NO_MATCH -> "לא נמצאה התאמה"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "המזהה עסוק"
                SpeechRecognizer.ERROR_SERVER -> "שגיאת שרת"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "זמן ההמתנה לדיבור פג"
                else -> "שגיאה לא ידועה בזיהוי קול"
            }
            _voiceErrors.tryEmit(errorMessage)
        }

        override fun onResults(results: Bundle?) {
            _isListening.value = false
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val bestMatch = matches?.firstOrNull()
            if (bestMatch != null) {
                _voiceResults.tryEmit(bestMatch)
            } else {
                _voiceErrors.tryEmit("לא זוהה טקסט")
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            // Handle partial results if needed for real-time display
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            // Handle other events
        }
    }

    fun startListening() {
        try {
            // Clean up existing recognizer
            stopListening()

            // Create new speech recognizer
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(recognitionListener)
            }

            // Create recognition intent
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "he-IL") // Hebrew
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Enable partial results
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
            }

            // Start listening
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
            _voiceErrors.tryEmit("שגיאה בהפעלת זיהוי קול: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
            _isListening.value = false
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }

    fun cleanup() {
        stopListening()
    }

    // Check if speech recognition is available
    fun isSpeechRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }
}