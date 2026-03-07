package com.aroundly.aroundly.domain.model

data class User(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: UserRole = UserRole.USER
)
data class Restaurant(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val city: String? = "",
    val type: String = "",
    val description: String? = null,
    val images: List<String>? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val websiteUrl: String? = null
)

data class Hotel(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val city: String? = "",
    val type: String = "",
    val description: String? = null,
    val images: List<String>? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val websiteUrl: String? = null
)

data class PlaceToVisit(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val city: String? = "",
    val description: String? = null,
    val images: List<String>? = null,
    val address: String? = null,
    val websiteUrl: String? = null
)

data class EquipmentShop(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val city: String? = "",
    val type: String = "",
    val description: String? = null,
    val images: List<String>? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val websiteUrl: String? = null
)
