package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.Review

/** Everything a place's review section needs, derived from a single read. */
data class ReviewData(
    val reviews: List<Review> = emptyList(),
    val averageRating: Float = 0f,
    val reviewCount: Int = 0,
    val userReview: Review? = null
)

interface ReviewRepository {
    suspend fun getReviews(placeId: String): List<Review>

    /** Replaces the separate list/average/count/user-review reads. */
    suspend fun getReviewData(placeId: String, userId: String?): ReviewData

    suspend fun addReview(review: Review): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>
}
