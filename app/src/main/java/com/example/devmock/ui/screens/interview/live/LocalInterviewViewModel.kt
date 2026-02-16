package com.example.devmock.ui.screens.interview.live

import androidx.lifecycle.ViewModel
import com.example.devmock.data.model.AiQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocalInterviewViewModel(
    questions: List<AiQuestion>
) : ViewModel(), InterviewViewModel {

    private val _uiState = MutableStateFlow(LiveInterviewUiState(questions = questions, title = "Library Interview"))
    override val uiState: StateFlow<LiveInterviewUiState> = _uiState.asStateFlow()

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
