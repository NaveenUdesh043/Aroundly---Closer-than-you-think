package com.aroundly.aroundly.data.repository

import android.util.Log
import com.aroundly.aroundly.domain.model.Review
import com.aroundly.aroundly.domain.repository.ReviewData
import com.aroundly.aroundly.domain.repository.ReviewRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    private val reviewsCollection = firestore.collection("reviews")

    override suspend fun getReviews(placeId: String): List<Review> =
        fetchReviews(placeId).sortedByDescending { it.timestamp }

    override suspend fun getReviewData(placeId: String, userId: String?): ReviewData {
        val reviews = fetchReviews(placeId).sortedByDescending { it.timestamp }
        val rated = reviews.map { it.rating }
        return ReviewData(
            reviews = reviews,
            averageRating = if (rated.isEmpty()) 0f else rated.average().toFloat(),
            reviewCount = reviews.size,
            userReview = userId?.let { uid -> reviews.firstOrNull { it.userId == uid } }
        )
    }

    override suspend fun addReview(review: Review): Result<Unit> {
        return try {
            val docRef = reviewsCollection.document()
            val reviewWithId = review.copy(id = docRef.id)
            docRef.set(reviewWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * One read serves the list, the average, the count and the signed-in user's
     * own review. Previously each of those was a separate full scan of the same
     * filtered set, so a place with reviews cost four round trips.
     *
     * Deliberately no server-side `orderBy`: an equality filter plus a sort
     * requires a composite index on (placeId, timestamp), and with no
     * `firestore.indexes.json` in the project that query fails at runtime and
     * was being swallowed into an empty list. Sorting the small in-memory result
     * keeps the feature working without depending on a deployed index.
     */
    private suspend fun fetchReviews(placeId: String): List<Review> = try {
        reviewsCollection
            .whereEqualTo("placeId", placeId)
            .get()
            .await()
            .documents
            .mapNotNull { doc -> doc.toObject(Review::class.java)?.copy(id = doc.id) }
    } catch (e: Exception) {
        // Logged rather than silently swallowed: a missing index or a rejected
        // rule used to look exactly like "this place has no reviews".
        Log.w(TAG, "Could not load reviews for place $placeId", e)
        emptyList()
    }

    private companion object {
        const val TAG = "ReviewRepository"
    }
}
