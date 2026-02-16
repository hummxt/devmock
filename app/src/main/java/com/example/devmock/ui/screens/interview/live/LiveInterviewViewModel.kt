package com.example.devmock.ui.screens.interview.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmock.data.model.AiQuestion
import com.example.devmock.data.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LiveInterviewUiState(
    val isLoading: Boolean = false,
    val questions: List<AiQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val score: Int = 0,
    val isCompleted: Boolean = false,
    val error: String? = null,
    val title: String = "AI Interview"
)

class LiveInterviewViewModel(
    private val repository: AIRepository,
    private val topic: String,
    private val questionCount: Int,
    private val difficulty: String
) : ViewModel(), InterviewViewModel {

    private val _uiState = MutableStateFlow(LiveInterviewUiState())
    override val uiState: StateFlow<LiveInterviewUiState> = _uiState.asStateFlow()

    init {
        generateInterview()
    }

    private fun generateInterview() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.generateInterview(topic, questionCount, difficulty)
                .onSuccess { questions ->
                    _uiState.update { it.copy(isLoading = false, questions = questions) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    override fun selectOption(index: Int) {
        if (_uiState.value.isAnswerRevealed) return
        _uiState.update { it.copy(selectedOptionIndex = index) }
    }

    override fun confirmAnswer() {
        val state = _uiState.value
        val selectedIndex = state.selectedOptionIndex ?: return
        val currentQuestion = state.questions[state.currentQuestionIndex]
        
        val isCorrect = selectedIndex == currentQuestion.correctAnswerIndex
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.update { 
            it.copy(
                isAnswerRevealed = true,
                score = newScore
            )
        }
    }

    override fun nextQuestion() {
        _uiState.update { state ->
            val nextIndex = state.currentQuestionIndex + 1
            if (nextIndex < state.questions.size) {
                state.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false
                )
            } else {
                state.copy(isCompleted = true)
            }
        }
    }
}
