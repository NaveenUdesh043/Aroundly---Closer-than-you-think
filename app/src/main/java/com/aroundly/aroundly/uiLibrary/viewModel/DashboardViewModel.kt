package com.aroundly.aroundly.uiLibrary.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.data.location.LocationProvider
import com.aroundly.aroundly.data.location.LocationResult
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _places = MutableStateFlow<List<PlaceToVisit>>(emptyList())
    val places: StateFlow<List<PlaceToVisit>> = _places.asStateFlow()

    private val _equipmentShops = MutableStateFlow<List<EquipmentShop>>(emptyList())
    val equipmentShops: StateFlow<List<EquipmentShop>> = _equipmentShops.asStateFlow()

    private val _availableCities = MutableStateFlow<List<String>>(emptyList())
    val availableCities: StateFlow<List<String>> = _availableCities.asStateFlow()

    private val _availableRestaurantTypes = MutableStateFlow<List<String>>(emptyList())
    val availableRestaurantTypes: StateFlow<List<String>> = _availableRestaurantTypes.asStateFlow()

    private val _availableHotelTypes = MutableStateFlow<List<String>>(emptyList())
    val availableHotelTypes: StateFlow<List<String>> = _availableHotelTypes.asStateFlow()

    private val _availableEquipmentTypes = MutableStateFlow<List<String>>(emptyList())
    val availableEquipmentTypes: StateFlow<List<String>> = _availableEquipmentTypes.asStateFlow()

    private val _selectedCity = MutableStateFlow("")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedRestaurantType = MutableStateFlow("")
    val selectedRestaurantType: StateFlow<String> = _selectedRestaurantType.asStateFlow()

    private val _selectedHotelType = MutableStateFlow("")
    val selectedHotelType: StateFlow<String> = _selectedHotelType.asStateFlow()

    private val _selectedEquipmentType = MutableStateFlow("")
    val selectedEquipmentType: StateFlow<String> = _selectedEquipmentType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationResult?>(null)
    val currentLocation: StateFlow<LocationResult?> = _currentLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation.asStateFlow()

    init {
        loadDropdownOptions()
        loadData()
    }

    fun loadDropdownOptions() {
        viewModelScope.launch {
            coroutineScope {
                launch {
                    _availableCities.value = listOf("All") + dashboardRepository.getAvailableCities()
                }
                launch {
                    _availableRestaurantTypes.value =
                        listOf("All") + dashboardRepository.getAvailableRestaurantTypes()
                }
                launch {
                    _availableHotelTypes.value =
                        listOf("All") + dashboardRepository.getAvailableHotelTypes()
                }
                launch {
                    _availableEquipmentTypes.value =
                        listOf("All") + dashboardRepository.getAvailableEquipmentTypes()
                }
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val city = _selectedCity.value.takeIf { it.isNotEmpty() && it != "All" }
            val restType = _selectedRestaurantType.value.takeIf { it.isNotEmpty() && it != "All" }
            val hotelType = _selectedHotelType.value.takeIf { it.isNotEmpty() && it != "All" }
            val eqType = _selectedEquipmentType.value.takeIf { it.isNotEmpty() && it != "All" }

            coroutineScope {
                launch { _restaurants.value = dashboardRepository.getRestaurants(city, restType) }
                launch { _hotels.value = dashboardRepository.getHotels(city, hotelType) }
                launch { _places.value = dashboardRepository.getPlacesToVisit(city) }
                launch {
                    _equipmentShops.value = dashboardRepository.getEquipmentShops(city, eqType)
                }
            }
            _isLoading.value = false
        }
    }

    fun setSelectedCity(city: String) {
        _selectedCity.value = city
        loadData()
    }

    fun setSelectedRestaurantType(type: String) {
        _selectedRestaurantType.value = type
        loadData()
    }

    fun setSelectedHotelType(type: String) {
        _selectedHotelType.value = type
        loadData()
    }

    fun setSelectedEquipmentType(type: String) {
        _selectedEquipmentType.value = type
        loadData()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun detectLocation() {
        viewModelScope.launch {
            _isLoadingLocation.value = true
            val result = locationProvider.getCurrentLocation()
            _currentLocation.value = result
            result?.cityHint?.let { city ->
                if (_availableCities.value.contains(city)) {
                    _selectedCity.value = city
                    loadData()
                } else {
                    _selectedCity.value = city
                    loadDropdownOptions()
                    loadData()
                }
            }
            _isLoadingLocation.value = false
        }
    }

    fun hasLocationPermission(): Boolean = locationProvider.hasLocationPermission()

    val filteredRestaurants: StateFlow<List<Restaurant>> =
        combine(_restaurants, _searchQuery) { list, query ->
            val q = query.lowercase()
            if (q.isEmpty()) list else list.filter {
                it.name.lowercase().contains(q) ||
                        it.type.lowercase().contains(q) ||
                        (it.description?.lowercase()?.contains(q) == true)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val filteredHotels: StateFlow<List<Hotel>> =
        combine(_hotels, _searchQuery) { list, query ->
            val q = query.lowercase()
            if (q.isEmpty()) list else list.filter {
                it.name.lowercase().contains(q) ||
                        it.type.lowercase().contains(q) ||
                        (it.description?.lowercase()?.contains(q) == true)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )


    val filteredPlaces: StateFlow<List<PlaceToVisit>> =
        combine(_places, _searchQuery) { list, query ->
            val q = query.lowercase()
            if (q.isEmpty()) list else list.filter {
                it.name.lowercase().contains(q) ||
                        (it.description?.lowercase()?.contains(q) == true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    val filteredEquipmentShops: StateFlow<List<EquipmentShop>> =
        combine(_equipmentShops, _searchQuery) { list, query ->
            val q = query.lowercase()
            if (q.isEmpty()) list else list.filter {
                it.name.lowercase().contains(q) ||
                        it.type.lowercase().contains(q) ||
                        (it.description?.lowercase()?.contains(q) == true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

}
