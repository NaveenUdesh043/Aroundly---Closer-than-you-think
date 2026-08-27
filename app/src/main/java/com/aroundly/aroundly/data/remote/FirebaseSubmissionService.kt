package com.aroundly.aroundly.data.remote

import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.domain.model.SubmissionStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseSubmissionService(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    suspend fun uploadReceipt(ownerId: String, bytes: ByteArray): String {
        val path = "receipts/$ownerId/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(path)
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadPlaceImage(ownerId: String, bytes: ByteArray): String {
        val path = "places/$ownerId/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(path)
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun submitPlace(submission: PlaceSubmission): String {
        val data = hashMapOf(
            "ownerId" to submission.ownerId,
            "ownerEmail" to submission.ownerEmail,
            "placeType" to submission.placeType.name,
            "name" to submission.name,
            "city" to submission.city,
            "type" to submission.type,
            "description" to submission.description,
            "address" to submission.address,
            "phoneNumber" to submission.phoneNumber,
            "websiteUrl" to submission.websiteUrl,
            "receiptImageUrl" to submission.receiptImageUrl,
            "placeImages" to submission.placeImages,
            "status" to submission.status.name,
            "submittedAt" to System.currentTimeMillis(),
            "existingPlaceId" to submission.existingPlaceId
        )
        val doc = firestore.collection("place_submissions").add(data).await()
        return doc.id
    }

    suspend fun getSubmissionsByOwner(ownerId: String): List<PlaceSubmission> {
        val snapshot = firestore.collection("place_submissions")
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toPlaceSubmission(doc.id)
        }
    }

    suspend fun getPendingSubmissions(): List<PlaceSubmission> {
        val snapshot = firestore.collection("place_submissions")
            .whereEqualTo("status", SubmissionStatus.PENDING.name)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toPlaceSubmission(doc.id)
        }
    }

    suspend fun updateStatus(
        submissionId: String,
        status: SubmissionStatus,
        adminNotes: String,
        submission: PlaceSubmission
    ) {
        firestore.collection("place_submissions").document(submissionId)
            .update(
                mapOf(
                    "status" to status.name,
                    "reviewedAt" to System.currentTimeMillis(),
                    "adminNotes" to adminNotes
                )
            )
            .await()

        if (status == SubmissionStatus.APPROVED) {
            val collectionName = when (submission.placeType) {
                com.aroundly.aroundly.domain.model.PlaceType.RESTAURANT -> "restaurants"
                com.aroundly.aroundly.domain.model.PlaceType.HOTEL -> "hotels"
                com.aroundly.aroundly.domain.model.PlaceType.PLACE_TO_VISIT -> "places"
                com.aroundly.aroundly.domain.model.PlaceType.EQUIPMENT_SHOP -> "equipmentShops"
            }

            val placeData = when (submission.placeType) {
                com.aroundly.aroundly.domain.model.PlaceType.RESTAURANT -> {
                    val r = submission.toRestaurant()
                    hashMapOf(
                        "ownerId" to submission.ownerId,
                        "name" to r.name,
                        "city" to r.city,
                        "type" to r.type,
                        "description" to r.description,
                        "address" to r.address,
                        "phoneNumber" to r.phoneNumber,
                        "websiteUrl" to r.websiteUrl,
                        "images" to (r.images ?: emptyList())
                    )
                }
                com.aroundly.aroundly.domain.model.PlaceType.HOTEL -> {
                    val h = submission.toHotel()
                    hashMapOf(
                        "ownerId" to submission.ownerId,
                        "name" to h.name,
                        "city" to h.city,
                        "type" to h.type,
                        "description" to h.description,
                        "address" to h.address,
                        "phoneNumber" to h.phoneNumber,
                        "websiteUrl" to h.websiteUrl,
                        "images" to (h.images ?: emptyList())
                    )
                }
                com.aroundly.aroundly.domain.model.PlaceType.PLACE_TO_VISIT -> {
                    val p = submission.toPlaceToVisit()
                    hashMapOf(
                        "ownerId" to submission.ownerId,
                        "name" to p.name,
                        "city" to p.city,
                        "description" to p.description,
                        "address" to p.address,
                        "websiteUrl" to p.websiteUrl,
                        "images" to (p.images ?: emptyList())
                    )
                }
                com.aroundly.aroundly.domain.model.PlaceType.EQUIPMENT_SHOP -> {
                    val e = submission.toEquipmentShop()
                    hashMapOf(
                        "ownerId" to submission.ownerId,
                        "name" to e.name,
                        "city" to e.city,
                        "type" to e.type,
                        "description" to e.description,
                        "address" to e.address,
                        "phoneNumber" to e.phoneNumber,
                        "websiteUrl" to e.websiteUrl,
                        "images" to (e.images ?: emptyList())
                    )
                }
            }

            if (submission.existingPlaceId.isNotBlank()) {
                firestore.collection(collectionName).document(submission.existingPlaceId)
                    .update(placeData as Map<String, Any>)
                    .await()
            } else {
                firestore.collection(collectionName).add(placeData).await()
            }
        }
    }

    suspend fun getSubmissionById(id: String): PlaceSubmission? {
        val doc = firestore.collection("place_submissions").document(id).get().await()
        return doc.toPlaceSubmission(id)
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toPlaceSubmission(id: String): PlaceSubmission? {
        return PlaceSubmission(
            id = id,
            ownerId = getString("ownerId") ?: "",
            ownerEmail = getString("ownerEmail") ?: "",
            placeType = com.aroundly.aroundly.domain.model.PlaceType.valueOf(
                getString("placeType") ?: "RESTAURANT"
            ),
            name = getString("name") ?: "",
            city = getString("city") ?: "",
            type = getString("type") ?: "",
            description = getString("description") ?: "",
            address = getString("address") ?: "",
            phoneNumber = getString("phoneNumber") ?: "",
            websiteUrl = getString("websiteUrl") ?: "",
            receiptImageUrl = getString("receiptImageUrl") ?: "",
            placeImages = (get("placeImages") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            status = SubmissionStatus.valueOf(getString("status") ?: "PENDING"),
            submittedAt = (get("submittedAt") as? Number)?.toLong() ?: 0L,
            reviewedAt = (get("reviewedAt") as? Number)?.toLong() ?: 0L,
            adminNotes = getString("adminNotes") ?: "",
            existingPlaceId = getString("existingPlaceId") ?: ""
        )
    }
}
