package com.example.hummet.data.model


data class AiQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val difficulty: String
)

data class AiInterviewResponse(
    val questions: List<AiQuestion>
)
