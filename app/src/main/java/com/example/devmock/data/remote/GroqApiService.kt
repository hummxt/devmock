package com.example.devmock.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApiService {
    
    @POST("openai/v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: GroqChatRequest
    ): Response<GroqChatResponse>
}

data class GroqChatRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 4096,
    val response_format: ResponseFormat? = ResponseFormat("json_object")
)

data class ResponseFormat(
    val type: String
)

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqChatResponse(
    val id: String?,
    val choices: List<GroqChoice>?,
    val error: GroqError?
)

data class GroqChoice(
    val index: Int,
    val message: GroqMessage,
    val finish_reason: String?
)

data class GroqError(
    val message: String?,
    val type: String?,
    val code: String?
)
