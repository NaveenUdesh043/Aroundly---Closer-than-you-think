package com.aroundly.aroundly.domain.model

data class PlaceSubmission(
    val id: String = "",
    val ownerId: String = "",
    val ownerEmail: String = "",
    val placeType: PlaceType = PlaceType.RESTAURANT,
    val name: String = "",
    val city: String = "",
    val type: String = "",
    val description: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val websiteUrl: String = "",
    val tags: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val openingHoursMonFri: String = "",
    val openingHoursSaturday: String = "",
    val openingHoursSunday: String = "",
    val openingHoursSchedule: List<OpeningHoursDay> = emptyList(),
    val receiptImageUrl: String = "",
    val placeImages: List<String> = emptyList(),
    val status: SubmissionStatus = SubmissionStatus.PENDING,
    val submittedAt: Long = 0L,
    val reviewedAt: Long = 0L,
    val adminNotes: String = ""
) {
    fun toRestaurant(): Restaurant = Restaurant(
        id = "",
        name = name,
        city = city,
        type = type,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        websiteUrl = websiteUrl.ifBlank { null },
        images = placeImages.ifEmpty { null },
        tags = tags,
        latitude = latitude,
        longitude = longitude,
        openingHoursMonFri = openingHoursMonFri.ifBlank { null },
        openingHoursSaturday = openingHoursSaturday.ifBlank { null },
        openingHoursSunday = openingHoursSunday.ifBlank { null },
        openingHoursSchedule = openingHoursSchedule
    )

    fun toHotel(): Hotel = Hotel(
        id = "",
        name = name,
        city = city,
        type = type,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        websiteUrl = websiteUrl.ifBlank { null },
        images = placeImages.ifEmpty { null },
        tags = tags,
        latitude = latitude,
        longitude = longitude,
        openingHoursMonFri = openingHoursMonFri.ifBlank { null },
        openingHoursSaturday = openingHoursSaturday.ifBlank { null },
        openingHoursSunday = openingHoursSunday.ifBlank { null },
        openingHoursSchedule = openingHoursSchedule
    )

    fun toPlaceToVisit(): PlaceToVisit = PlaceToVisit(
        id = "",
        name = name,
        city = city,
        description = description,
        address = address,
        websiteUrl = websiteUrl.ifBlank { null },
        images = placeImages.ifEmpty { null },
        tags = tags,
        latitude = latitude,
        longitude = longitude,
        openingHoursMonFri = openingHoursMonFri.ifBlank { null },
        openingHoursSaturday = openingHoursSaturday.ifBlank { null },
        openingHoursSunday = openingHoursSunday.ifBlank { null },
        openingHoursSchedule = openingHoursSchedule
    )

    fun toEquipmentShop(): EquipmentShop = EquipmentShop(
        id = "",
        name = name,
        city = city,
        type = type,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        websiteUrl = websiteUrl.ifBlank { null },
        images = placeImages.ifEmpty { null },
        tags = tags,
        latitude = latitude,
        longitude = longitude,
        openingHoursMonFri = openingHoursMonFri.ifBlank { null },
        openingHoursSaturday = openingHoursSaturday.ifBlank { null },
        openingHoursSunday = openingHoursSunday.ifBlank { null },
        openingHoursSchedule = openingHoursSchedule
    )
}

enum class PlaceType {
    RESTAURANT,
    HOTEL,
    PLACE_TO_VISIT,
    EQUIPMENT_SHOP
}

enum class SubmissionStatus {
    PENDING,
    APPROVED,
    REJECTED
}
