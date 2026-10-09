package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.Review

data class ReviewData(
    val reviews: List<Review> = emptyList(),
    val averageRating: Float = 0f,
    val reviewCount: Int = 0,
    val userReview: Review? = null
)

interface ReviewRepository {
    suspend fun getReviews(placeId: String): List<Review>

    suspend fun getReviewData(placeId: String, userId: String?): ReviewData

    suspend fun addReview(review: Review): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>
}
