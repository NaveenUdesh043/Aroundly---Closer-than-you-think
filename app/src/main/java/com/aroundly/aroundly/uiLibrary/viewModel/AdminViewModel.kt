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

    private val _approvedSubmissions = MutableStateFlow<List<PlaceSubmission>>(emptyList())
    val approvedSubmissions: StateFlow<List<PlaceSubmission>> = _approvedSubmissions.asStateFlow()

    private val _rejectedSubmissions = MutableStateFlow<List<PlaceSubmission>>(emptyList())
    val rejectedSubmissions: StateFlow<List<PlaceSubmission>> = _rejectedSubmissions.asStateFlow()

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

    fun loadApprovedSubmissions() {
        viewModelScope.launch {
            _isLoading.value = true
            _approvedSubmissions.value = submissionRepository.getSubmissionsByStatus(SubmissionStatus.APPROVED)
            _isLoading.value = false
        }
    }

    fun loadRejectedSubmissions() {
        viewModelScope.launch {
            _isLoading.value = true
            _rejectedSubmissions.value = submissionRepository.getSubmissionsByStatus(SubmissionStatus.REJECTED)
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
                    loadAll()
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
                    loadAll()
                },
                onFailure = {
                    _message.value = "Error: ${it.message}"
                }
            )
            _isLoading.value = false
        }
    }

    fun deletePlace(submission: PlaceSubmission) {
        viewModelScope.launch {
            _isLoading.value = true
            submissionRepository.deletePlace(submission).fold(
                onSuccess = {
                    _message.value = "Deleted: ${submission.name}"
                    loadAll()
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

    private fun loadAll() {
        viewModelScope.launch {
            _pendingSubmissions.value = submissionRepository.getPendingSubmissions()
            _approvedSubmissions.value = submissionRepository.getSubmissionsByStatus(SubmissionStatus.APPROVED)
            _rejectedSubmissions.value = submissionRepository.getSubmissionsByStatus(SubmissionStatus.REJECTED)
        }
    }
}