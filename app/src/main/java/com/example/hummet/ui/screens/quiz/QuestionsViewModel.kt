package com.example.hummet.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hummet.data.repository.QuestionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

val topicData = listOf(
    TopicGroup(
        id = "android-basics",
        topicTitle = "Android Basics",
        category = "Mobile",
        difficulty = "Easy",
        questions = listOf("What is an Activity?", "Describe the Fragment lifecycle", "What is an Intent?"),
        accentColor = Color(0xFF10B981),
        tags = listOf("Android", "Basics", "Mobile"),
        companyHistory = "Foundational for any Android role"
    ),
    TopicGroup(
        id = "kotlin-coroutines",
        topicTitle = "Kotlin Coroutines",
        category = "Mobile",
        difficulty = "Medium",
        questions = listOf("What is a CoroutineScope?", "Explain suspend functions", "Difference between launch and async"),
        accentColor = Color(0xFF3B82F6),
        tags = listOf("Kotlin", "Async", "Mobile"),
        companyHistory = "Commonly asked at Meta and Google"
    )
)

data class QuestionsUiState(
    val isLoading: Boolean = false,
    val topics: List<TopicGroup> = emptyList(),
    val error: String? = null
)

class QuestionsViewModel(private val repository: QuestionsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(QuestionsUiState())
    val uiState: StateFlow<QuestionsUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getQuestionsLibrary()
                .onSuccess { topics ->

                    val finalTopics = if (topics.isEmpty()) topicData else topics
                    _uiState.update { it.copy(isLoading = false, topics = finalTopics) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message, topics = topicData) }
                }
        }
    }
}
