package com.aroundly.aroundly.uiLibrary.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.domain.model.SubmissionStatus
import com.aroundly.aroundly.domain.repository.SubmissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
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

    /**
     * The three tab loads are launched together, so a single boolean would be
     * cleared by whichever returned first while the others were still running.
     * A counter keeps the spinner up until the last one lands.
     */
    private var inFlight = 0

    private fun <T> tracked(block: suspend () -> T, onResult: (T) -> Unit) {
        viewModelScope.launch {
            if (inFlight++ == 0) _isLoading.value = true
            try {
                onResult(block())
            } finally {
                if (--inFlight == 0) _isLoading.value = false
            }
        }
    }

    fun loadPendingSubmissions() {
        tracked(
            block = { submissionRepository.getPendingSubmissions() },
            onResult = { _pendingSubmissions.value = it }
        )
    }

    fun loadApprovedSubmissions() {
        tracked(
            block = { submissionRepository.getSubmissionsByStatus(SubmissionStatus.APPROVED) },
            onResult = { _approvedSubmissions.value = it }
        )
    }

    fun loadRejectedSubmissions() {
        tracked(
            block = { submissionRepository.getSubmissionsByStatus(SubmissionStatus.REJECTED) },
            onResult = { _rejectedSubmissions.value = it }
        )
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
            coroutineScope {
                launch { _pendingSubmissions.value = submissionRepository.getPendingSubmissions() }
                launch {
                    _approvedSubmissions.value =
                        submissionRepository.getSubmissionsByStatus(SubmissionStatus.APPROVED)
                }
                launch {
                    _rejectedSubmissions.value =
                        submissionRepository.getSubmissionsByStatus(SubmissionStatus.REJECTED)
                }
            }
        }
    }
}