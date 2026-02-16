package com.example.hummet.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hummet.data.repository.QuestionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
