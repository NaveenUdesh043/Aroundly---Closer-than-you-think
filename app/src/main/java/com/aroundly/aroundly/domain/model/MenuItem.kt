package com.aroundly.aroundly.domain.model

data class MenuItem(
    val id: String = "",
    val shopId: String = "",
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
