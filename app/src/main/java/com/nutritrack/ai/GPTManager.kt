package com.nutritrack.ai

class GPTManager(private val apiKey: String, private val mealDao: MealDao) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val service = retrofit.create(OpenAIService::class.java)

    suspend fun getAdvice(userMessage: String): String {
        val recentMeals = mealDao.getMealsSince(System.currentTimeMillis() - 7.days)
        val systemPrompt = buildSystemPrompt(recentMeals)
        val request = ChatRequest(
            model = "gpt-4o-mini", // ‎mock
            messages = listOf(
                ChatMessage("system", systemPrompt),
                ChatMessage("user", userMessage)
            )
        )
        return service.chat(request).choices.first().message.content
    }

    private fun buildSystemPrompt(meals: List<MealEntity>): String =
        """הנך יועץ תזונה אישי. נתוני 7 הימים האחרונים: ${meals.map { it.description }}. השב בעברית בתמציתיות.""".trimIndent()
}
