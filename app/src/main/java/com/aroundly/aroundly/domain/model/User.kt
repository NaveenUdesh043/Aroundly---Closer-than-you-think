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
    val websiteUrl: String? = null,
    val tags: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val openingHoursMonFri: String? = null,
    val openingHoursSaturday: String? = null,
    val openingHoursSunday: String? = null,
    val openingHoursSchedule: List<OpeningHoursDay> = emptyList()
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
    val websiteUrl: String? = null,
    val tags: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val openingHoursMonFri: String? = null,
    val openingHoursSaturday: String? = null,
    val openingHoursSunday: String? = null,
    val openingHoursSchedule: List<OpeningHoursDay> = emptyList()
)

data class PlaceToVisit(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val city: String? = "",
    val description: String? = null,
    val images: List<String>? = null,
    val address: String? = null,
    val websiteUrl: String? = null,
    val tags: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val openingHoursMonFri: String? = null,
    val openingHoursSaturday: String? = null,
    val openingHoursSunday: String? = null,
    val openingHoursSchedule: List<OpeningHoursDay> = emptyList()
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
    val websiteUrl: String? = null,
    val tags: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val openingHoursMonFri: String? = null,
    val openingHoursSaturday: String? = null,
    val openingHoursSunday: String? = null,
    val openingHoursSchedule: List<OpeningHoursDay> = emptyList()
)
