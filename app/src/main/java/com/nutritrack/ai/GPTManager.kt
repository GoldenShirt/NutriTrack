package com.nutritrack.ai

import com.nutritrack.data.local.MealDao
import com.nutritrack.data.local.MealEntity
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import com.squareup.moshi.JsonClass

// Data classes for OpenAI API
@JsonClass(generateAdapter = true)
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    val choices: List<Choice>
)

@JsonClass(generateAdapter = true)
data class Choice(
    val message: ChatMessage
)

interface OpenAIService {
    @POST("chat/completions")
    suspend fun chat(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): ChatResponse
}

class GPTManager(private val apiKey: String, private val mealDao: MealDao) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val service = retrofit.create(OpenAIService::class.java)

    suspend fun getAdvice(userMessage: String): String {
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val recentMeals = mealDao.getMealsSince(sevenDaysAgo)
        val systemPrompt = buildSystemPrompt(recentMeals)
        val request = ChatRequest(
            model = "gpt-4o-mini",
            messages = listOf(
                ChatMessage("system", systemPrompt),
                ChatMessage("user", userMessage)
            )
        )
        return try {
            service.chat("Bearer $apiKey", request).choices.first().message.content
        } catch (e: Exception) {
            "Sorry, I'm having trouble connecting to the nutrition advisor right now."
        }
    }

    private fun buildSystemPrompt(meals: List<MealEntity>): String =
        """הנך יועץ תזונה אישי. נתוני 7 הימים האחרונים: ${meals.map { it.description }}. השב בעברית בתמציתיות.""".trimIndent()
}