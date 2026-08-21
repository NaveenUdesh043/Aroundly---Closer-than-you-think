package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.Review

interface ReviewRepository {
    suspend fun getReviews(placeId: String): List<Review>
    suspend fun addReview(review: Review): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>
    suspend fun getAverageRating(placeId: String): Float
    suspend fun getReviewCount(placeId: String): Int
    suspend fun getUserReviewForPlace(userId: String, placeId: String): Review?
}
