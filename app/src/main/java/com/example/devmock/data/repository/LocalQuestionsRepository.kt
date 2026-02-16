package com.example.devmock.data.repository

import android.content.Context
import com.example.devmock.ui.screens.quiz.TopicGroup
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color
import com.example.devmock.data.model.AiQuestion
import com.example.devmock.data.model.AiInterviewResponse

class LocalQuestionsRepository(private val context: Context) {
    private val gson = Gson()

    suspend fun getQuestionsLibrary(): Result<List<TopicGroup>> = withContext(Dispatchers.IO) {
        try {
            val assetManager = context.assets
            val files = assetManager.list("questions") ?: emptyArray()
            val topics = files.filter { it.endsWith(".json") }.map { fileName ->
                val jsonString = assetManager.open("questions/$fileName").bufferedReader().use { it.readText() }
                parseTopic(jsonString)
            }
            Result.success(topics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseTopic(jsonString: String): TopicGroup {
        val data = gson.fromJson(jsonString, Map::class.java)
        
        val id = data["id"] as? String ?: ""
        val title = data["topicTitle"] as? String ?: ""
        val category = data["category"] as? String ?: ""
        val difficulty = data["difficulty"] as? String ?: ""
        val questions = (data["questions"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        val accentHex = data["accentColor"] as? String ?: "#040C4C"
        val tags = (data["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        val companyHistory = data["companyHistory"] as? String ?: ""
        
        val fullQuestionsJson = gson.toJson(data["fullQuestions"])
        val fullQuestions = gson.fromJson(fullQuestionsJson, Array<AiQuestion>::class.java).toList()

        return TopicGroup(
            id = id,
            topicTitle = title,
            category = category,
            difficulty = difficulty,
            questions = questions,
            accentColor = Color(android.graphics.Color.parseColor(accentHex)),
            tags = tags,
            companyHistory = companyHistory,
            fullQuestions = fullQuestions
        )
    }

    suspend fun getTopicById(id: String): TopicGroup? {
        return getQuestionsLibrary().getOrNull()?.find { it.id == id }
    }
}
