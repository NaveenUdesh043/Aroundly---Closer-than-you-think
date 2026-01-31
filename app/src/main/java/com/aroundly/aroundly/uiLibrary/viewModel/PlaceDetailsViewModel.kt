package com.aroundly.aroundly.uiLibrary.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val id: String = savedStateHandle.get<String>("id") ?: ""
    private val _restaurant = MutableStateFlow<Restaurant?>(null)
    val restaurant: StateFlow<Restaurant?> = _restaurant.asStateFlow()
    init { viewModelScope.launch { _restaurant.value = dashboardRepository.getRestaurantById(id) } }
}

@HiltViewModel
class HotelDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val id: String = savedStateHandle.get<String>("id") ?: ""
    private val _hotel = MutableStateFlow<Hotel?>(null)
    val hotel: StateFlow<Hotel?> = _hotel.asStateFlow()
    init { viewModelScope.launch { _hotel.value = dashboardRepository.getHotelById(id) } }
}

@HiltViewModel
class PlaceDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val id: String = savedStateHandle.get<String>("id") ?: ""
    private val _place = MutableStateFlow<PlaceToVisit?>(null)
    val place: StateFlow<PlaceToVisit?> = _place.asStateFlow()
    init { viewModelScope.launch { _place.value = dashboardRepository.getPlaceById(id) } }
}

@HiltViewModel
class EquipmentDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val id: String = savedStateHandle.get<String>("id") ?: ""
    private val _equipment = MutableStateFlow<EquipmentShop?>(null)
    val equipment: StateFlow<EquipmentShop?> = _equipment.asStateFlow()
    init { viewModelScope.launch { _equipment.value = dashboardRepository.getEquipmentShopById(id) } }
}
