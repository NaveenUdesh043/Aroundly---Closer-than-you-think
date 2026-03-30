package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.PlaceReview

interface ReviewRepository {
    suspend fun getReviews(placeId: String, placeKind: String): List<PlaceReview>
    suspend fun addReview(review: PlaceReview): Result<String>
}
