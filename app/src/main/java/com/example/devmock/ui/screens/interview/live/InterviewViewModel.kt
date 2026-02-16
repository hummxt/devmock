package com.example.devmock.ui.screens.interview.live

import kotlinx.coroutines.flow.StateFlow

interface InterviewViewModel {
    val uiState: StateFlow<LiveInterviewUiState>
    fun selectOption(index: Int)
    fun confirmAnswer()
    fun nextQuestion()
}
