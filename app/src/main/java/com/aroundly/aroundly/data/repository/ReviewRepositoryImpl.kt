package com.aroundly.aroundly.data.repository

import com.aroundly.aroundly.domain.model.PlaceReview
import com.aroundly.aroundly.domain.repository.ReviewRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    override suspend fun getReviews(placeId: String, placeKind: String): List<PlaceReview> {
        val snapshot = firestore.collection(COLLECTION)
            .whereEqualTo("placeId", placeId)
            .whereEqualTo("placeKind", placeKind)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            PlaceReview(
                id = doc.id,
                placeId = doc.getString("placeId") ?: "",
                placeKind = doc.getString("placeKind") ?: "",
                userId = doc.getString("userId") ?: "",
                userName = doc.getString("userName") ?: "",
                rating = (doc.getDouble("rating") ?: 0.0).toFloat(),
                comment = doc.getString("comment") ?: "",
                createdAt = (doc.get("createdAt") as? Number)?.toLong() ?: 0L
            )
        }.sortedByDescending { it.createdAt }
    }

    override suspend fun addReview(review: PlaceReview): Result<String> = runCatching {
        val data = hashMapOf(
            "placeId" to review.placeId,
            "placeKind" to review.placeKind,
            "userId" to review.userId,
            "userName" to review.userName,
            "rating" to review.rating.toDouble(),
            "comment" to review.comment,
            "createdAt" to review.createdAt
        )
        val ref = firestore.collection(COLLECTION).add(data).await()
        ref.id
    }

    companion object {
        private const val COLLECTION = "place_reviews"
    }
}
