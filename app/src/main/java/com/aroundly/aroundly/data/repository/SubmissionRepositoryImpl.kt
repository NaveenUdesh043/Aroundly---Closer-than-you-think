package com.aroundly.aroundly.data.repository

import com.aroundly.aroundly.data.remote.FirebaseSubmissionService
import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.domain.model.SubmissionStatus
import com.aroundly.aroundly.domain.repository.SubmissionRepository
import javax.inject.Inject

class SubmissionRepositoryImpl @Inject constructor(
    private val submissionService: FirebaseSubmissionService
) : SubmissionRepository {

    override suspend fun uploadReceipt(ownerId: String, bytes: ByteArray): Result<String> =
        runCatching { submissionService.uploadReceipt(ownerId, bytes) }

    override suspend fun uploadPlaceImage(ownerId: String, bytes: ByteArray): Result<String> =
        runCatching { submissionService.uploadPlaceImage(ownerId, bytes) }

    override suspend fun submitPlace(submission: PlaceSubmission): Result<String> =
        runCatching { submissionService.submitPlace(submission) }

    override suspend fun getSubmissionsByOwner(ownerId: String): List<PlaceSubmission> =
        submissionService.getSubmissionsByOwner(ownerId)

    override suspend fun getPendingSubmissions(): List<PlaceSubmission> =
        submissionService.getPendingSubmissions()

    override suspend fun getSubmissionsByStatus(status: SubmissionStatus): List<PlaceSubmission> =
        submissionService.getSubmissionsByStatus(status)

    override suspend fun updateSubmissionStatus(
        submissionId: String,
        submission: PlaceSubmission,
        status: SubmissionStatus,
        adminNotes: String
    ): Result<Unit> = runCatching {
        submissionService.updateStatus(submissionId, status, adminNotes, submission)
    }

    override suspend fun deletePlace(submission: PlaceSubmission): Result<Unit> =
        runCatching { submissionService.deletePlace(submission) }

    override suspend fun getSubmissionById(id: String): PlaceSubmission? =
        submissionService.getSubmissionById(id)
}
