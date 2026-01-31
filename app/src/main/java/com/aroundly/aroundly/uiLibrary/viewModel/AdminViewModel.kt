package com.aroundly.aroundly.uiLibrary.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.domain.model.SubmissionStatus
import com.aroundly.aroundly.domain.repository.SubmissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val submissionRepository: SubmissionRepository
) : ViewModel() {

    private val _pendingSubmissions = MutableStateFlow<List<PlaceSubmission>>(emptyList())
    val pendingSubmissions: StateFlow<List<PlaceSubmission>> = _pendingSubmissions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun loadPendingSubmissions() {
        viewModelScope.launch {
            _isLoading.value = true
            _pendingSubmissions.value = submissionRepository.getPendingSubmissions()
            _isLoading.value = false
        }
    }

    fun approveSubmission(submission: PlaceSubmission, adminNotes: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            submissionRepository.updateSubmissionStatus(
                submissionId = submission.id,
                submission = submission,
                status = SubmissionStatus.APPROVED,
                adminNotes = adminNotes
            ).fold(
                onSuccess = {
                    _message.value = "Approved: ${submission.name}"
                    loadPendingSubmissions()
                },
                onFailure = {
                    _message.value = "Error: ${it.message}"
                }
            )
            _isLoading.value = false
        }
    }

    fun rejectSubmission(submission: PlaceSubmission, adminNotes: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            submissionRepository.updateSubmissionStatus(
                submissionId = submission.id,
                submission = submission,
                status = SubmissionStatus.REJECTED,
                adminNotes = adminNotes
            ).fold(
                onSuccess = {
                    _message.value = "Rejected: ${submission.name}"
                    loadPendingSubmissions()
                },
                onFailure = {
                    _message.value = "Error: ${it.message}"
                }
            )
            _isLoading.value = false
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
