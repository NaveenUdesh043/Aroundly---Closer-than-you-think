package com.aroundly.aroundly.data.repository

import com.aroundly.aroundly.domain.model.Review
import com.aroundly.aroundly.domain.repository.ReviewRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    private val reviewsCollection = firestore.collection("reviews")

    override suspend fun getReviews(placeId: String): List<Review> {
        return try {
            reviewsCollection
                .whereEqualTo("placeId", placeId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Review::class.java)
        } catch (e: Exception) {
            emptyList()
        }
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

    override suspend fun getAverageRating(placeId: String): Float {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("placeId", placeId)
                .get()
                .await()
            val reviews = snapshot.toObjects(Review::class.java)
            if (reviews.isEmpty()) 0f
            else reviews.map { it.rating }.average().toFloat()
        } catch (e: Exception) {
            0f
        }
    }

    override suspend fun getReviewCount(placeId: String): Int {
        return try {
            reviewsCollection
                .whereEqualTo("placeId", placeId)
                .get()
                .await()
                .size()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getUserReviewForPlace(userId: String, placeId: String): Review? {
        return try {
            reviewsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("placeId", placeId)
                .limit(1)
                .get()
                .await()
                .toObjects(Review::class.java)
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
