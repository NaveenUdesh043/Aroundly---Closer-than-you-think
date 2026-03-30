package com.aroundly.aroundly.data.remote

import com.aroundly.aroundly.domain.model.OpeningHoursDay
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
            "tags" to submission.tags,
            "latitude" to submission.latitude,
            "longitude" to submission.longitude,
            "openingHoursMonFri" to submission.openingHoursMonFri,
            "openingHoursSaturday" to submission.openingHoursSaturday,
            "openingHoursSunday" to submission.openingHoursSunday,
            "openingHoursSchedule" to submission.openingHoursSchedule.toFirestoreMaps(),
            "receiptImageUrl" to submission.receiptImageUrl,
            "placeImages" to submission.placeImages,
            "status" to submission.status.name,
            "submittedAt" to System.currentTimeMillis()
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
            when (submission.placeType) {
                com.aroundly.aroundly.domain.model.PlaceType.RESTAURANT -> {
                    val r = submission.toRestaurant()
                    firestore.collection("restaurants").add(
                        hashMapOf(
                            "ownerId" to submission.ownerId,
                            "name" to r.name,
                            "city" to r.city,
                            "type" to r.type,
                            "description" to r.description,
                            "address" to r.address,
                            "phoneNumber" to r.phoneNumber,
                            "websiteUrl" to r.websiteUrl,
                            "images" to (r.images ?: emptyList()),
                            "tags" to r.tags,
                            "latitude" to r.latitude,
                            "longitude" to r.longitude,
                            "openingHoursMonFri" to r.openingHoursMonFri,
                            "openingHoursSaturday" to r.openingHoursSaturday,
                            "openingHoursSunday" to r.openingHoursSunday,
                            "openingHoursSchedule" to r.openingHoursSchedule.toFirestoreMaps()
                        )
                    ).await()
                }
                com.aroundly.aroundly.domain.model.PlaceType.HOTEL -> {
                    val h = submission.toHotel()
                    firestore.collection("hotels").add(
                        hashMapOf(
                            "ownerId" to submission.ownerId,
                            "name" to h.name,
                            "city" to h.city,
                            "type" to h.type,
                            "description" to h.description,
                            "address" to h.address,
                            "phoneNumber" to h.phoneNumber,
                            "websiteUrl" to h.websiteUrl,
                            "images" to (h.images ?: emptyList()),
                            "tags" to h.tags,
                            "latitude" to h.latitude,
                            "longitude" to h.longitude,
                            "openingHoursMonFri" to h.openingHoursMonFri,
                            "openingHoursSaturday" to h.openingHoursSaturday,
                            "openingHoursSunday" to h.openingHoursSunday,
                            "openingHoursSchedule" to h.openingHoursSchedule.toFirestoreMaps()
                        )
                    ).await()
                }
                com.aroundly.aroundly.domain.model.PlaceType.PLACE_TO_VISIT -> {
                    val p = submission.toPlaceToVisit()
                    firestore.collection("places").add(
                        hashMapOf(
                            "ownerId" to submission.ownerId,
                            "name" to p.name,
                            "city" to p.city,
                            "description" to p.description,
                            "address" to p.address,
                            "websiteUrl" to p.websiteUrl,
                            "images" to (p.images ?: emptyList()),
                            "tags" to p.tags,
                            "latitude" to p.latitude,
                            "longitude" to p.longitude,
                            "openingHoursMonFri" to p.openingHoursMonFri,
                            "openingHoursSaturday" to p.openingHoursSaturday,
                            "openingHoursSunday" to p.openingHoursSunday,
                            "openingHoursSchedule" to p.openingHoursSchedule.toFirestoreMaps()
                        )
                    ).await()
                }
                com.aroundly.aroundly.domain.model.PlaceType.EQUIPMENT_SHOP -> {
                    val e = submission.toEquipmentShop()
                    firestore.collection("equipmentShops").add(
                        hashMapOf(
                            "ownerId" to submission.ownerId,
                            "name" to e.name,
                            "city" to e.city,
                            "type" to e.type,
                            "description" to e.description,
                            "address" to e.address,
                            "phoneNumber" to e.phoneNumber,
                            "websiteUrl" to e.websiteUrl,
                            "images" to (e.images ?: emptyList()),
                            "tags" to e.tags,
                            "latitude" to e.latitude,
                            "longitude" to e.longitude,
                            "openingHoursMonFri" to e.openingHoursMonFri,
                            "openingHoursSaturday" to e.openingHoursSaturday,
                            "openingHoursSunday" to e.openingHoursSunday,
                            "openingHoursSchedule" to e.openingHoursSchedule.toFirestoreMaps()
                        )
                    ).await()
                }
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
            tags = (get("tags") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            latitude = (get("latitude") as? Number)?.toDouble(),
            longitude = (get("longitude") as? Number)?.toDouble(),
            openingHoursMonFri = getString("openingHoursMonFri") ?: "",
            openingHoursSaturday = getString("openingHoursSaturday") ?: "",
            openingHoursSunday = getString("openingHoursSunday") ?: "",
            openingHoursSchedule = parseOpeningHoursSchedule(),
            receiptImageUrl = getString("receiptImageUrl") ?: "",
            placeImages = (get("placeImages") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            status = SubmissionStatus.valueOf(getString("status") ?: "PENDING"),
            submittedAt = (get("submittedAt") as? Number)?.toLong() ?: 0L,
            reviewedAt = (get("reviewedAt") as? Number)?.toLong() ?: 0L,
            adminNotes = getString("adminNotes") ?: ""
        )
    }
}

private fun List<OpeningHoursDay>.toFirestoreMaps(): List<Map<String, Any?>> =
    map {
        mapOf(
            "dayLabel" to it.dayLabel,
            "openTime" to it.openTime,
            "closeTime" to it.closeTime,
            "isClosed" to it.isClosed
        )
    }

private fun com.google.firebase.firestore.DocumentSnapshot.parseOpeningHoursSchedule(): List<OpeningHoursDay> {
    val raw = get("openingHoursSchedule") as? List<*> ?: return emptyList()
    return raw.mapNotNull { entry ->
        val m = entry as? Map<*, *> ?: return@mapNotNull null
        OpeningHoursDay(
            dayLabel = (m["dayLabel"] as? String) ?: "",
            openTime = (m["openTime"] as? String) ?: "",
            closeTime = (m["closeTime"] as? String) ?: "",
            isClosed = (m["isClosed"] as? Boolean) ?: false
        )
    }
}
