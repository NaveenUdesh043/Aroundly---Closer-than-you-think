package com.aroundly.aroundly.domain.model

data class Review(
    val id: String = "",
    val placeId: String = "",
    val placeType: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0f,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
