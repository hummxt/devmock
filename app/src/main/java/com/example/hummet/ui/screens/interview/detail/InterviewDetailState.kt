package com.example.hummet.ui.screens.interview.detail

sealed class InterviewDetailState {
    object Loading : InterviewDetailState()
    data class Success(val detail: com.example.hummet.data.model.TopicDetail) : InterviewDetailState()
    data class Error(val message: String) : InterviewDetailState()
}