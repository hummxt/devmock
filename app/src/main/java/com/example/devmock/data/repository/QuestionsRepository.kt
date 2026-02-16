package com.example.devmock.data.repository

import com.example.devmock.ui.screens.quiz.TopicGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.Color

class QuestionsRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val questionsCollection = firestore.collection("questions_library")

    suspend fun getQuestionsLibrary(): Result<List<TopicGroup>> {
        return try {
            val snapshot = questionsCollection.get().await()
            val topics = snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val title = doc.getString("topicTitle") ?: ""
                val category = doc.getString("category") ?: ""
                val difficulty = doc.getString("difficulty") ?: ""
                val questions = doc.get("questions") as? List<String> ?: emptyList()
                val colorHex = doc.getString("accentColor") ?: "#FFFFFF"
                val tags = doc.get("tags") as? List<String> ?: emptyList()
                val companyHistory = doc.getString("companyHistory") ?: ""
                
                TopicGroup(
                    id = id,
                    topicTitle = title,
                    category = category,
                    difficulty = difficulty,
                    questions = questions,
                    accentColor = Color(android.graphics.Color.parseColor(colorHex)),
                    tags = tags,
                    companyHistory = companyHistory
                )
            }
            Result.success(topics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
