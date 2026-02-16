package com.example.devmock.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmock.data.repository.LocalQuestionsRepository
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

class QuestionsViewModel(private val repository: LocalQuestionsRepository) : ViewModel() {
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
                    _uiState.update { it.copy(isLoading = false, topics = topics) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}
