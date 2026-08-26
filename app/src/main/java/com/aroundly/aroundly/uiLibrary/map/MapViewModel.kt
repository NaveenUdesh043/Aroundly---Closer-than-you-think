package com.aroundly.aroundly.uiLibrary.map

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.data.location.LocationProvider
import com.aroundly.aroundly.data.location.LocationResult
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.domain.repository.DashboardRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class MapPlace(
    val id: String,
    val name: String,
    val category: PlaceCategory,
    val latLng: LatLng?,
    val address: String?
)

enum class PlaceCategory(val label: String) {
    RESTAURANT("Restaurant"),
    HOTEL("Hotel"),
    PLACE("Place to Visit"),
    RENTAL("Rental Shop")
}

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationProvider: LocationProvider,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _currentLocation = MutableStateFlow<LocationResult?>(null)
    val currentLocation: StateFlow<LocationResult?> = _currentLocation.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation.asStateFlow()

    private val _mapPlaces = MutableStateFlow<List<MapPlace>>(emptyList())
    val mapPlaces: StateFlow<List<MapPlace>> = _mapPlaces.asStateFlow()

    private val _isLoadingPlaces = MutableStateFlow(false)
    val isLoadingPlaces: StateFlow<Boolean> = _isLoadingPlaces.asStateFlow()

    private val _selectedCategory = MutableStateFlow<PlaceCategory?>(null)
    val selectedCategory: StateFlow<PlaceCategory?> = _selectedCategory.asStateFlow()

    private val geocoder by lazy { Geocoder(context) }

    init {
        fetchCurrentLocation()
        loadAllPlaces()
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _isLoadingLocation.value = true
            val result = locationProvider.getCurrentLocation()
            _currentLocation.value = result
            _isLoadingLocation.value = false
        }
    }

    fun toggleCategory(category: PlaceCategory) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun hasLocationPermission(): Boolean = locationProvider.hasLocationPermission()

    private fun loadAllPlaces() {
        viewModelScope.launch {
            _isLoadingPlaces.value = true
            val restaurants = dashboardRepository.getRestaurants(null, null)
            val hotels = dashboardRepository.getHotels(null, null)
            val places = dashboardRepository.getPlacesToVisit(null)
            val equipment = dashboardRepository.getEquipmentShops(null, null)

            val allPlaces = mutableListOf<MapPlace>()

            restaurants.forEach { r ->
                allPlaces.add(
                    MapPlace(
                        id = r.id,
                        name = r.name,
                        category = PlaceCategory.RESTAURANT,
                        latLng = geocodeAddress(r.address),
                        address = r.address
                    )
                )
            }

            hotels.forEach { h ->
                allPlaces.add(
                    MapPlace(
                        id = h.id,
                        name = h.name,
                        category = PlaceCategory.HOTEL,
                        latLng = geocodeAddress(h.address),
                        address = h.address
                    )
                )
            }

            places.forEach { p ->
                allPlaces.add(
                    MapPlace(
                        id = p.id,
                        name = p.name,
                        category = PlaceCategory.PLACE,
                        latLng = geocodeAddress(p.address),
                        address = p.address
                    )
                )
            }

            equipment.forEach { e ->
                allPlaces.add(
                    MapPlace(
                        id = e.id,
                        name = e.name,
                        category = PlaceCategory.RENTAL,
                        latLng = geocodeAddress(e.address),
                        address = e.address
                    )
                )
            }

            _mapPlaces.value = allPlaces.filter { it.latLng != null }
            _isLoadingPlaces.value = false
        }
    }

    private suspend fun geocodeAddress(address: String?): LatLng? {
        if (address.isNullOrBlank()) return null
        return withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val results = geocoder.getFromLocationName(address, 1)
                val loc = results?.firstOrNull() ?: return@withContext null
                LatLng(loc.latitude, loc.longitude)
            } catch (_: Exception) {
                null
            }
        }
    }
}
