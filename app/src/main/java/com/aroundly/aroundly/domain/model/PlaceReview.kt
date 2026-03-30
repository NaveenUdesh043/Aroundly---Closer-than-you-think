package com.aroundly.aroundly.domain.model

/** Firestore collection: place_reviews */
data class PlaceReview(
    val id: String = "",
    val placeId: String = "",
    /** One of: restaurants, hotels, places, equipmentShops */
    val placeKind: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val createdAt: Long = 0L
)

object PlaceReviewKinds {
    const val RESTAURANTS = "restaurants"
    const val HOTELS = "hotels"
    const val PLACES = "places"
    const val EQUIPMENT = "equipmentShops"
}
