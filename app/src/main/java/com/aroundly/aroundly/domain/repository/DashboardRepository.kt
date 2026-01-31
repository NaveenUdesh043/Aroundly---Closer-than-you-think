package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant

interface DashboardRepository {
    suspend fun getRestaurants(city: String?, type: String?): List<Restaurant>
    suspend fun getHotels(city: String?, type: String?): List<Hotel>
    suspend fun getPlacesToVisit(city: String?): List<PlaceToVisit>
    suspend fun getEquipmentShops(city: String?, type: String?): List<EquipmentShop>
    suspend fun getRestaurantById(id: String): Restaurant?
    suspend fun getHotelById(id: String): Hotel?
    suspend fun getPlaceById(id: String): PlaceToVisit?
    suspend fun getEquipmentShopById(id: String): EquipmentShop?
    suspend fun getAvailableCities(): List<String>
    suspend fun getAvailableRestaurantTypes(): List<String>
    suspend fun getAvailableHotelTypes(): List<String>
    suspend fun getAvailableEquipmentTypes(): List<String>
    suspend fun getRestaurantsByOwner(ownerId: String): List<Restaurant>
    suspend fun getHotelsByOwner(ownerId: String): List<Hotel>
    suspend fun getPlacesByOwner(ownerId: String): List<PlaceToVisit>
    suspend fun getEquipmentShopsByOwner(ownerId: String): List<EquipmentShop>
    suspend fun updateRestaurant(restaurant: Restaurant): Result<Unit>
    suspend fun updateHotel(hotel: Hotel): Result<Unit>
    suspend fun updatePlace(place: PlaceToVisit): Result<Unit>
    suspend fun updateEquipmentShop(shop: EquipmentShop): Result<Unit>
}
