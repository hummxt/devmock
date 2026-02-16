package com.example.devmock.data.model

sealed class AiError(
    val title: String,
    val message: String,
    val suggestion: String
) {
    data object QuotaExceeded : AiError(
        title = "Quota Exceeded",
        message = "You've reached the API usage limit for today.",
        suggestion = "Please try again tomorrow when the quota resets, or contact support for a plan upgrade."
    )

    data object InvalidApiKey : AiError(
        title = "Authentication Failed",
        message = "The API key is invalid or has expired.",
        suggestion = "Please check your API configuration or generate a new API key."
    )

    data class ModelNotFound(val modelName: String) : AiError(
        title = "Model Not Available",
        message = "The AI model '$modelName' is not available.",
        suggestion = "The model may have been updated. Please update the app or try again later."
    )

    data class RateLimited(val retryAfterSeconds: Int?) : AiError(
        title = "Too Many Requests",
        message = "You're sending requests too quickly.",
        suggestion = if (retryAfterSeconds != null) 
            "Please wait $retryAfterSeconds seconds before trying again." 
        else 
            "Please wait a moment before trying again."
    )

    data object NetworkError : AiError(
        title = "No Connection",
        message = "Unable to reach the AI service.",
        suggestion = "Please check your internet connection and try again."
    )

    data class ServerError(val code: Int) : AiError(
        title = "Server Error",
        message = "The AI service is temporarily unavailable (Error $code).",
        suggestion = "Please try again in a few minutes."
    )

    data object ParseError : AiError(
        title = "Response Error",
        message = "Failed to understand the AI response.",
        suggestion = "Please try again. The AI may have produced an unexpected format."
    )

    data object ContentBlocked : AiError(
        title = "Content Blocked",
        message = "The request was blocked by safety filters.",
        suggestion = "Please try a different topic or rephrase your request."
    )

    data class Unknown(val originalMessage: String?) : AiError(
        title = "Something Went Wrong",
        message = originalMessage ?: "An unexpected error occurred.",
        suggestion = "Please try again. If the problem persists, contact support."
    )

    fun toDisplayString(): String = buildString {
        append("$title\n\n")
        append("$message\n\n")
        append("💡 $suggestion")
    }

    companion object {
        fun fromException(exception: Throwable): AiError {
            val message = exception.message ?: ""
            
            return when {
                message.contains("quota", ignoreCase = true) ||
                message.contains("exceeded", ignoreCase = true) ||
                message.contains("limit", ignoreCase = true) -> QuotaExceeded
                
                message.contains("API_KEY_INVALID", ignoreCase = true) ||
                message.contains("API key not valid", ignoreCase = true) ||
                message.contains("401", ignoreCase = true) ||
                message.contains("UNAUTHENTICATED", ignoreCase = true) -> InvalidApiKey
                
                message.contains("not found", ignoreCase = true) && 
                message.contains("model", ignoreCase = true) -> {
                    val modelName = Regex("models?/([\\w.-]+)").find(message)?.groupValues?.getOrNull(1) ?: "unknown"
                    ModelNotFound(modelName)
                }
                
                message.contains("429", ignoreCase = true) ||
                message.contains("rate limit", ignoreCase = true) ||
                message.contains("too many requests", ignoreCase = true) -> {
                    val retryAfter = Regex("(\\d+)\\s*s").find(message)?.groupValues?.getOrNull(1)?.toIntOrNull()
                    RateLimited(retryAfter)
                }
                
                message.contains("unable to resolve host", ignoreCase = true) ||
                message.contains("network", ignoreCase = true) ||
                message.contains("connection", ignoreCase = true) ||
                message.contains("timeout", ignoreCase = true) ||
                exception is java.net.UnknownHostException ||
                exception is java.net.SocketTimeoutException -> NetworkError
                
                message.contains("500", ignoreCase = true) ||
                message.contains("502", ignoreCase = true) ||
                message.contains("503", ignoreCase = true) ||
                message.contains("internal server", ignoreCase = true) -> {
                    val code = Regex("(5\\d{2})").find(message)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 500
                    ServerError(code)
                }
                
                message.contains("blocked", ignoreCase = true) ||
                message.contains("safety", ignoreCase = true) ||
                message.contains("HARM_CATEGORY", ignoreCase = true) -> ContentBlocked
                
                message.contains("parse", ignoreCase = true) ||
                message.contains("json", ignoreCase = true) ||
                message.contains("deserialization", ignoreCase = true) ||
                message.contains("serialization", ignoreCase = true) ||
                exception is com.google.gson.JsonSyntaxException -> ParseError
                
                else -> Unknown(message.takeIf { it.isNotBlank() })
            }
        }
    }
}
