package com.aroundly.aroundly.data.repository

import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.OpeningHoursDay
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.domain.repository.DashboardRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private fun DocumentSnapshot.tagsList(): List<String> =
    (get("tags") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()

private fun DocumentSnapshot.optDouble(name: String): Double? =
    (get(name) as? Number)?.toDouble()

private fun DocumentSnapshot.openingHoursScheduleList(): List<OpeningHoursDay> {
    val raw = get("openingHoursSchedule") as? List<*> ?: return emptyList()
    return raw.mapNotNull { entry ->
        val m = entry as? Map<*, *> ?: return@mapNotNull null
        OpeningHoursDay(
            dayLabel = (m["dayLabel"] as? String) ?: "",
            openTime = (m["openTime"] as? String) ?: "",
            closeTime = (m["closeTime"] as? String) ?: "",
            isClosed = (m["isClosed"] as? Boolean) ?: false
        )
    }
}

private fun List<OpeningHoursDay>.toFirestoreScheduleMaps(): List<Map<String, Any?>> =
    map {
        mapOf(
            "dayLabel" to it.dayLabel,
            "openTime" to it.openTime,
            "closeTime" to it.closeTime,
            "isClosed" to it.isClosed
        )
    }

class DashboardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DashboardRepository {

    override suspend fun getRestaurants(city: String?, type: String?): List<Restaurant> {
        var query: Query = firestore.collection("restaurants")
        if (!city.isNullOrEmpty()) query = query.whereEqualTo("city", city)
        if (!type.isNullOrEmpty()) query = query.whereEqualTo("type", type)

        return query.get().await().documents.mapNotNull { doc ->
            val imagesField = doc.get("images")
            val imagesList = when (imagesField) {
                is List<*> -> imagesField.filterIsInstance<String>()
                is String -> listOf(imagesField)
                else -> emptyList()
            }

            Restaurant(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                description = doc.getString("description"),
                address = doc.getString("address"),
                phoneNumber = doc.getString("phoneNumber"),
                city = doc.getString("city"),
                websiteUrl = doc.getString("websiteUrl"),
                images = imagesList,
                tags = doc.tagsList(),
                latitude = doc.optDouble("latitude"),
                longitude = doc.optDouble("longitude"),
                openingHoursMonFri = doc.getString("openingHoursMonFri"),
                openingHoursSaturday = doc.getString("openingHoursSaturday"),
                openingHoursSunday = doc.getString("openingHoursSunday"),
                openingHoursSchedule = doc.openingHoursScheduleList()
            )
        }
    }

    override suspend fun getHotels(city: String?, type: String?): List<Hotel> {
        var query: Query = firestore.collection("hotels")
        if (!city.isNullOrEmpty()) query = query.whereEqualTo("city", city)
        if (!type.isNullOrEmpty()) query = query.whereEqualTo("type", type)

        return query.get().await().documents.mapNotNull { doc ->
            val imagesField = doc.get("images")
            val imagesList = when (imagesField) {
                is List<*> -> imagesField.filterIsInstance<String>()
                is String -> listOf(imagesField)
                else -> emptyList()
            }

            Hotel(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                description = doc.getString("description"),
                address = doc.getString("address"),
                phoneNumber = doc.getString("phoneNumber"),
                city = doc.getString("city"),
                websiteUrl = doc.getString("websiteUrl"),
                images = imagesList,
                tags = doc.tagsList(),
                latitude = doc.optDouble("latitude"),
                longitude = doc.optDouble("longitude"),
                openingHoursMonFri = doc.getString("openingHoursMonFri"),
                openingHoursSaturday = doc.getString("openingHoursSaturday"),
                openingHoursSunday = doc.getString("openingHoursSunday"),
                openingHoursSchedule = doc.openingHoursScheduleList()
            )
        }
    }

    override suspend fun getPlacesToVisit(city: String?): List<PlaceToVisit> {
        var query: Query = firestore.collection("places")
        if (!city.isNullOrEmpty()) query = query.whereEqualTo("city", city)

        return query.get().await().documents.mapNotNull { doc ->
            val imagesField = doc.get("images")
            val imagesList = when (imagesField) {
                is List<*> -> imagesField.filterIsInstance<String>()
                is String -> listOf(imagesField)
                else -> emptyList()
            }

            PlaceToVisit(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                name = doc.getString("name") ?: "",
                description = doc.getString("description"),
                address = doc.getString("address"),
                city = doc.getString("city"),
                websiteUrl = doc.getString("websiteUrl"),
                images = imagesList,
                tags = doc.tagsList(),
                latitude = doc.optDouble("latitude"),
                longitude = doc.optDouble("longitude"),
                openingHoursMonFri = doc.getString("openingHoursMonFri"),
                openingHoursSaturday = doc.getString("openingHoursSaturday"),
                openingHoursSunday = doc.getString("openingHoursSunday"),
                openingHoursSchedule = doc.openingHoursScheduleList()
            )
        }
    }

    override suspend fun getEquipmentShops(city: String?, type: String?): List<EquipmentShop> {
        var query: Query = firestore.collection("equipmentShops")
        if (!city.isNullOrEmpty()) query = query.whereEqualTo("city", city)
        if (!type.isNullOrEmpty()) query = query.whereEqualTo("type", type)

        return query.get().await().documents.mapNotNull { doc ->
            val imagesField = doc.get("images")
            val imagesList = when (imagesField) {
                is List<*> -> imagesField.filterIsInstance<String>()
                is String -> listOf(imagesField)
                else -> emptyList()
            }

            EquipmentShop(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                description = doc.getString("description"),
                address = doc.getString("address"),
                phoneNumber = doc.getString("phoneNumber"),
                city = doc.getString("city"),
                websiteUrl = doc.getString("websiteUrl"),
                images = imagesList,
                tags = doc.tagsList(),
                latitude = doc.optDouble("latitude"),
                longitude = doc.optDouble("longitude"),
                openingHoursMonFri = doc.getString("openingHoursMonFri"),
                openingHoursSaturday = doc.getString("openingHoursSaturday"),
                openingHoursSunday = doc.getString("openingHoursSunday"),
                openingHoursSchedule = doc.openingHoursScheduleList()
            )
        }
    }

    override suspend fun getRestaurantById(id: String): Restaurant? {
        val doc = firestore.collection("restaurants").document(id).get().await()
        val imagesField = doc.get("images")
        val imagesList = when (imagesField) {
            is List<*> -> imagesField.filterIsInstance<String>()
            is String -> listOf(imagesField)
            else -> emptyList()
        }

        return Restaurant(
            id = doc.id,
            ownerId = doc.getString("ownerId") ?: "",
            name = doc.getString("name") ?: "",
            type = doc.getString("type") ?: "",
            description = doc.getString("description"),
            address = doc.getString("address"),
            phoneNumber = doc.getString("phoneNumber"),
            city = doc.getString("city"),
            websiteUrl = doc.getString("websiteUrl"),
            images = imagesList,
            tags = doc.tagsList(),
            latitude = doc.optDouble("latitude"),
            longitude = doc.optDouble("longitude"),
            openingHoursMonFri = doc.getString("openingHoursMonFri"),
            openingHoursSaturday = doc.getString("openingHoursSaturday"),
            openingHoursSunday = doc.getString("openingHoursSunday"),
            openingHoursSchedule = doc.openingHoursScheduleList()
        )
    }

    override suspend fun getHotelById(id: String): Hotel? {
        val doc = firestore.collection("hotels").document(id).get().await()
        val imagesField = doc.get("images")
        val imagesList = when (imagesField) {
            is List<*> -> imagesField.filterIsInstance<String>()
            is String -> listOf(imagesField)
            else -> emptyList()
        }

        return Hotel(
            id = doc.id,
            ownerId = doc.getString("ownerId") ?: "",
            name = doc.getString("name") ?: "",
            type = doc.getString("type") ?: "",
            description = doc.getString("description"),
            address = doc.getString("address"),
            phoneNumber = doc.getString("phoneNumber"),
            city = doc.getString("city"),
            websiteUrl = doc.getString("websiteUrl"),
            images = imagesList,
            tags = doc.tagsList(),
            latitude = doc.optDouble("latitude"),
            longitude = doc.optDouble("longitude"),
            openingHoursMonFri = doc.getString("openingHoursMonFri"),
            openingHoursSaturday = doc.getString("openingHoursSaturday"),
            openingHoursSunday = doc.getString("openingHoursSunday"),
            openingHoursSchedule = doc.openingHoursScheduleList()
        )
    }

    override suspend fun getPlaceById(id: String): PlaceToVisit? {
        val doc = firestore.collection("places").document(id).get().await()
        val imagesField = doc.get("images")
        val imagesList = when (imagesField) {
            is List<*> -> imagesField.filterIsInstance<String>()
            is String -> listOf(imagesField)
            else -> emptyList()
        }

        return PlaceToVisit(
            id = doc.id,
            ownerId = doc.getString("ownerId") ?: "",
            name = doc.getString("name") ?: "",
            description = doc.getString("description"),
            address = doc.getString("address"),
            city = doc.getString("city"),
            websiteUrl = doc.getString("websiteUrl"),
            images = imagesList,
            tags = doc.tagsList(),
            latitude = doc.optDouble("latitude"),
            longitude = doc.optDouble("longitude"),
            openingHoursMonFri = doc.getString("openingHoursMonFri"),
            openingHoursSaturday = doc.getString("openingHoursSaturday"),
            openingHoursSunday = doc.getString("openingHoursSunday"),
            openingHoursSchedule = doc.openingHoursScheduleList()
        )
    }

    override suspend fun getEquipmentShopById(id: String): EquipmentShop? {
        val doc = firestore.collection("equipmentShops").document(id).get().await()
        val imagesField = doc.get("images")
        val imagesList = when (imagesField) {
            is List<*> -> imagesField.filterIsInstance<String>()
            is String -> listOf(imagesField)
            else -> emptyList()
        }

        return EquipmentShop(
            id = doc.id,
            ownerId = doc.getString("ownerId") ?: "",
            name = doc.getString("name") ?: "",
            type = doc.getString("type") ?: "",
            description = doc.getString("description"),
            address = doc.getString("address"),
            phoneNumber = doc.getString("phoneNumber"),
            city = doc.getString("city"),
            websiteUrl = doc.getString("websiteUrl"),
            images = imagesList,
            tags = doc.tagsList(),
            latitude = doc.optDouble("latitude"),
            longitude = doc.optDouble("longitude"),
            openingHoursMonFri = doc.getString("openingHoursMonFri"),
            openingHoursSaturday = doc.getString("openingHoursSaturday"),
            openingHoursSunday = doc.getString("openingHoursSunday"),
            openingHoursSchedule = doc.openingHoursScheduleList()
        )
    }

    override suspend fun getAvailableCities(): List<String> {
        val collections = listOf("restaurants", "hotels", "places", "equipmentShops")
        val cities = mutableSetOf<String>()

        collections.forEach { col ->
            val snapshot = firestore.collection(col).get().await()
            snapshot.documents.forEach { doc ->
                doc.getString("city")?.let { cities.add(it) }
            }
        }

        return cities.filter { it.isNotEmpty() }.distinct().sorted()
    }

    override suspend fun getAvailableRestaurantTypes(): List<String> {
        val snapshot = firestore.collection("restaurants").get().await()
        return snapshot.documents.mapNotNull { it.getString("type") }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
    }

    override suspend fun getAvailableHotelTypes(): List<String> {
        val snapshot = firestore.collection("hotels").get().await()
        return snapshot.documents.mapNotNull { it.getString("type") }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
    }

    override suspend fun getAvailableEquipmentTypes(): List<String> {
        val snapshot = firestore.collection("equipmentShops").get().await()
        return snapshot.documents.mapNotNull { it.getString("type") }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
    }

    override suspend fun getRestaurantsByOwner(ownerId: String): List<Restaurant> {
        val snapshot = firestore.collection("restaurants")
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val imagesField = doc.get("images")
            val imagesList = when (imagesField) {
                is List<*> -> imagesField.filterIsInstance<String>()
                is String -> listOf(imagesField)
                else -> emptyList()
            }
            Restaurant(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                description = doc.getString("description"),
                address = doc.getString("address"),
                phoneNumber = doc.getString("phoneNumber"),
                city = doc.getString("city"),
                websiteUrl = doc.getString("websiteUrl"),
                images = imagesList,
                tags = doc.tagsList(),
                latitude = doc.optDouble("latitude"),
                longitude = doc.optDouble("longitude"),
                openingHoursMonFri = doc.getString("openingHoursMonFri"),
                openingHoursSaturday = doc.getString("openingHoursSaturday"),
                openingHoursSunday = doc.getString("openingHoursSunday"),
                openingHoursSchedule = doc.openingHoursScheduleList()
            )
        }
    }

    override suspend fun getHotelsByOwner(ownerId: String): List<Hotel> {
        val snapshot = firestore.collection("hotels")
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val imagesField = doc.get("images")
            val imagesList = when (imagesField) {
                is List<*> -> imagesField.filterIsInstance<String>()
                is String -> listOf(imagesField)
                else -> emptyList()
            }
            Hotel(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                description = doc.getString("description"),
                address = doc.getString("address"),
                phoneNumber = doc.getString("phoneNumber"),
                city = doc.getString("city"),
                websiteUrl = doc.getString("websiteUrl"),
                images = imagesList,
                tags = doc.tagsList(),
                latitude = doc.optDouble("latitude"),
                longitude = doc.optDouble("longitude"),
                openingHoursMonFri = doc.getString("openingHoursMonFri"),
                openingHoursSaturday = doc.getString("openingHoursSaturday"),
                openingHoursSunday = doc.getString("openingHoursSunday"),
                openingHoursSchedule = doc.openingHoursScheduleList()
            )
        }
    }

    override suspend fun getPlacesByOwner(ownerId: String): List<PlaceToVisit> {
        val snapshot = firestore.collection("places")
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val imagesField = doc.get("images")
            val imagesList = when (imagesField) {
                is List<*> -> imagesField.filterIsInstance<String>()
                is String -> listOf(imagesField)
                else -> emptyList()
            }
            PlaceToVisit(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                name = doc.getString("name") ?: "",
                description = doc.getString("description"),
                address = doc.getString("address"),
                city = doc.getString("city"),
                websiteUrl = doc.getString("websiteUrl"),
                images = imagesList,
                tags = doc.tagsList(),
                latitude = doc.optDouble("latitude"),
                longitude = doc.optDouble("longitude"),
                openingHoursMonFri = doc.getString("openingHoursMonFri"),
                openingHoursSaturday = doc.getString("openingHoursSaturday"),
                openingHoursSunday = doc.getString("openingHoursSunday"),
                openingHoursSchedule = doc.openingHoursScheduleList()
            )
        }
    }

    override suspend fun getEquipmentShopsByOwner(ownerId: String): List<EquipmentShop> {
        val snapshot = firestore.collection("equipmentShops")
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val imagesField = doc.get("images")
            val imagesList = when (imagesField) {
                is List<*> -> imagesField.filterIsInstance<String>()
                is String -> listOf(imagesField)
                else -> emptyList()
            }
            EquipmentShop(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                description = doc.getString("description"),
                address = doc.getString("address"),
                phoneNumber = doc.getString("phoneNumber"),
                city = doc.getString("city"),
                websiteUrl = doc.getString("websiteUrl"),
                images = imagesList,
                tags = doc.tagsList(),
                latitude = doc.optDouble("latitude"),
                longitude = doc.optDouble("longitude"),
                openingHoursMonFri = doc.getString("openingHoursMonFri"),
                openingHoursSaturday = doc.getString("openingHoursSaturday"),
                openingHoursSunday = doc.getString("openingHoursSunday"),
                openingHoursSchedule = doc.openingHoursScheduleList()
            )
        }
    }

    override suspend fun updateRestaurant(restaurant: Restaurant): Result<Unit> = runCatching {
        firestore.collection("restaurants").document(restaurant.id).update(
            mapOf(
                "name" to restaurant.name,
                "city" to restaurant.city,
                "type" to restaurant.type,
                "description" to (restaurant.description ?: ""),
                "address" to (restaurant.address ?: ""),
                "phoneNumber" to (restaurant.phoneNumber ?: ""),
                "websiteUrl" to (restaurant.websiteUrl ?: ""),
                "images" to (restaurant.images ?: emptyList()),
                "tags" to restaurant.tags,
                "latitude" to restaurant.latitude,
                "longitude" to restaurant.longitude,
                "openingHoursMonFri" to (restaurant.openingHoursMonFri ?: ""),
                "openingHoursSaturday" to (restaurant.openingHoursSaturday ?: ""),
                "openingHoursSunday" to (restaurant.openingHoursSunday ?: ""),
                "openingHoursSchedule" to restaurant.openingHoursSchedule.toFirestoreScheduleMaps()
            )
        ).await()
    }

    override suspend fun updateHotel(hotel: Hotel): Result<Unit> = runCatching {
        firestore.collection("hotels").document(hotel.id).update(
            mapOf(
                "name" to hotel.name,
                "city" to hotel.city,
                "type" to hotel.type,
                "description" to (hotel.description ?: ""),
                "address" to (hotel.address ?: ""),
                "phoneNumber" to (hotel.phoneNumber ?: ""),
                "websiteUrl" to (hotel.websiteUrl ?: ""),
                "images" to (hotel.images ?: emptyList()),
                "tags" to hotel.tags,
                "latitude" to hotel.latitude,
                "longitude" to hotel.longitude,
                "openingHoursMonFri" to (hotel.openingHoursMonFri ?: ""),
                "openingHoursSaturday" to (hotel.openingHoursSaturday ?: ""),
                "openingHoursSunday" to (hotel.openingHoursSunday ?: ""),
                "openingHoursSchedule" to hotel.openingHoursSchedule.toFirestoreScheduleMaps()
            )
        ).await()
    }

    override suspend fun updatePlace(place: PlaceToVisit): Result<Unit> = runCatching {
        firestore.collection("places").document(place.id).update(
            mapOf(
                "name" to place.name,
                "city" to place.city,
                "description" to (place.description ?: ""),
                "address" to (place.address ?: ""),
                "websiteUrl" to (place.websiteUrl ?: ""),
                "images" to (place.images ?: emptyList()),
                "tags" to place.tags,
                "latitude" to place.latitude,
                "longitude" to place.longitude,
                "openingHoursMonFri" to (place.openingHoursMonFri ?: ""),
                "openingHoursSaturday" to (place.openingHoursSaturday ?: ""),
                "openingHoursSunday" to (place.openingHoursSunday ?: ""),
                "openingHoursSchedule" to place.openingHoursSchedule.toFirestoreScheduleMaps()
            )
        ).await()
    }

    override suspend fun updateEquipmentShop(shop: EquipmentShop): Result<Unit> = runCatching {
        firestore.collection("equipmentShops").document(shop.id).update(
            mapOf(
                "name" to shop.name,
                "city" to shop.city,
                "type" to shop.type,
                "description" to (shop.description ?: ""),
                "address" to (shop.address ?: ""),
                "phoneNumber" to (shop.phoneNumber ?: ""),
                "websiteUrl" to (shop.websiteUrl ?: ""),
                "images" to (shop.images ?: emptyList()),
                "tags" to shop.tags,
                "latitude" to shop.latitude,
                "longitude" to shop.longitude,
                "openingHoursMonFri" to (shop.openingHoursMonFri ?: ""),
                "openingHoursSaturday" to (shop.openingHoursSaturday ?: ""),
                "openingHoursSunday" to (shop.openingHoursSunday ?: ""),
                "openingHoursSchedule" to shop.openingHoursSchedule.toFirestoreScheduleMaps()
            )
        ).await()
    }
}
