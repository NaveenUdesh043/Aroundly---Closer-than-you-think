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
        images = placeImages.ifEmpty { null }
    )

    fun toHotel(): Hotel = Hotel(
        id = "",
        name = name,
        city = city,
        type = type,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        images = placeImages.ifEmpty { null }
    )

    fun toPlaceToVisit(): PlaceToVisit = PlaceToVisit(
        id = "",
        name = name,
        city = city,
        description = description,
        address = address,
        images = placeImages.ifEmpty { null }
    )

    fun toEquipmentShop(): EquipmentShop = EquipmentShop(
        id = "",
        name = name,
        city = city,
        type = type,
        description = description,
        address = address,
        phoneNumber = phoneNumber,
        images = placeImages.ifEmpty { null }
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
