package com.example.devmock.ui.screens.quiz

import androidx.compose.ui.graphics.Color
import com.example.devmock.data.model.AiQuestion

data class TopicGroup(
    val id: String,
    val topicTitle: String,
    val category: String,
    val difficulty: String,
    val questions: List<String>,
    val accentColor: Color,
    val tags: List<String>,
    val companyHistory: String,
    val fullQuestions: List<AiQuestion> = emptyList()
)
