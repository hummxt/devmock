package com.example.devmock.ui.screens.interview.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmock.data.repository.InterviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InterviewDetailViewModel(
    private val repository: InterviewRepository,
    private val topicId: String
) : ViewModel() {

    private val _state = MutableStateFlow<InterviewDetailState>(InterviewDetailState.Loading)
    val state: StateFlow<InterviewDetailState> = _state.asStateFlow()

    init {
        loadTopicDetail()
    }

    fun loadTopicDetail() {
        viewModelScope.launch {
            _state.value = InterviewDetailState.Loading

            repository.getTopicDetail(topicId).fold(
                onSuccess = { detail ->
                    _state.value = InterviewDetailState.Success(detail)
                },
                onFailure = { error ->
                    _state.value = InterviewDetailState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun startInterview() {
    }

    fun retryLoad() {
        loadTopicDetail()
    }
}