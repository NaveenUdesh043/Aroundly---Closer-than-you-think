package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.domain.model.SubmissionStatus

interface SubmissionRepository {
    suspend fun uploadReceipt(ownerId: String, bytes: ByteArray): Result<String>
    suspend fun uploadPlaceImage(ownerId: String, bytes: ByteArray): Result<String>
    suspend fun submitPlace(submission: PlaceSubmission): Result<String>
    suspend fun getSubmissionsByOwner(ownerId: String): List<PlaceSubmission>
    suspend fun getPendingSubmissions(): List<PlaceSubmission>
    suspend fun getSubmissionsByStatus(status: SubmissionStatus): List<PlaceSubmission>
    suspend fun updateSubmissionStatus(
        submissionId: String,
        submission: PlaceSubmission,
        status: SubmissionStatus,
        adminNotes: String = ""
    ): Result<Unit>
    suspend fun deletePlace(submission: PlaceSubmission): Result<Unit>
    suspend fun getSubmissionById(id: String): PlaceSubmission?
}
