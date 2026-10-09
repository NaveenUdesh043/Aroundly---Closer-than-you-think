package com.aroundly.aroundly.uiLibrary.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.PlaceType
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.domain.model.SubmissionStatus
import com.aroundly.aroundly.domain.repository.DashboardRepository
import com.aroundly.aroundly.domain.repository.SubmissionRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val submissionRepository: SubmissionRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _mySubmissions = MutableStateFlow<List<PlaceSubmission>>(emptyList())
    val mySubmissions: StateFlow<List<PlaceSubmission>> = _mySubmissions.asStateFlow()

    private val _myRestaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val myRestaurants: StateFlow<List<Restaurant>> = _myRestaurants.asStateFlow()
    private val _myHotels = MutableStateFlow<List<Hotel>>(emptyList())
    val myHotels: StateFlow<List<Hotel>> = _myHotels.asStateFlow()
    private val _myPlaces = MutableStateFlow<List<PlaceToVisit>>(emptyList())
    val myPlaces: StateFlow<List<PlaceToVisit>> = _myPlaces.asStateFlow()
    private val _myEquipmentShops = MutableStateFlow<List<EquipmentShop>>(emptyList())
    val myEquipmentShops: StateFlow<List<EquipmentShop>> = _myEquipmentShops.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submitMessage = MutableStateFlow<String?>(null)
    val submitMessage: StateFlow<String?> = _submitMessage.asStateFlow()

    fun loadMySubmissions() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            _mySubmissions.value = submissionRepository.getSubmissionsByOwner(uid)
        }
    }

    fun loadMyApprovedPlaces() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            coroutineScope {
                launch {
                    _myRestaurants.value =
                        dashboardRepository.getRestaurantsByOwner(uid)
                }
                launch {
                    _myHotels.value =
                        dashboardRepository.getHotelsByOwner(uid)
                }
                launch {
                    _myPlaces.value =
                        dashboardRepository.getPlacesByOwner(uid)
                }
                launch {
                    _myEquipmentShops.value =
                        dashboardRepository.getEquipmentShopsByOwner(uid)
                }
            }
        }
    }

    fun updateRestaurant(restaurant: Restaurant, newImageBytesList: List<ByteArray> = emptyList()) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _submitMessage.value = "Not logged in"
                return@launch
            }
            _isSubmitting.value = true

            val placeImageUrls = mutableListOf<String>()
            restaurant.images?.let { placeImageUrls.addAll(it) }

            val uploaded = coroutineScope {
                newImageBytesList.map { bytes ->
                    async { submissionRepository.uploadPlaceImage(user.uid, bytes) }
                }.awaitAll()
            }


            uploaded.forEach { result ->
                result.getOrNull()?.let { placeImageUrls.add(it) }
            }

            val submission = PlaceSubmission(
                ownerId = user.uid,
                ownerEmail = user.email ?: "",
                placeType = PlaceType.RESTAURANT,
                name = restaurant.name,
                city = restaurant.city ?: "",
                type = restaurant.type,
                description = restaurant.description ?: "",
                address = restaurant.address ?: "",
                phoneNumber = restaurant.phoneNumber ?: "",
                websiteUrl = restaurant.websiteUrl ?: "",
                placeImages = placeImageUrls,
                status = SubmissionStatus.PENDING,
                submittedAt = System.currentTimeMillis(),
                existingPlaceId = restaurant.id
            )

            submissionRepository.submitPlace(submission).fold(
                onSuccess = {
                    _submitMessage.value = "Update submitted! Awaiting admin approval."
                    loadMySubmissions()
                },
                onFailure = { _submitMessage.value = "Submit failed: ${it.message}" }
            )
            _isSubmitting.value = false
        }
    }

    fun updateHotel(hotel: Hotel, newImageBytesList: List<ByteArray> = emptyList()) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _submitMessage.value = "Not logged in"
                return@launch
            }
            _isSubmitting.value = true

            val placeImageUrls = mutableListOf<String>()
            hotel.images?.let { placeImageUrls.addAll(it) }

            val uploaded = coroutineScope {
                newImageBytesList.map { bytes ->
                    async { submissionRepository.uploadPlaceImage(user.uid, bytes) }
                }.awaitAll()
            }
            uploaded.forEach { result ->
                result.getOrNull()?.let { placeImageUrls.add(it) }
            }

            val submission = PlaceSubmission(
                ownerId = user.uid,
                ownerEmail = user.email ?: "",
                placeType = PlaceType.HOTEL,
                name = hotel.name,
                city = hotel.city ?: "",
                type = hotel.type,
                description = hotel.description ?: "",
                address = hotel.address ?: "",
                phoneNumber = hotel.phoneNumber ?: "",
                websiteUrl = hotel.websiteUrl ?: "",
                placeImages = placeImageUrls,
                status = SubmissionStatus.PENDING,
                submittedAt = System.currentTimeMillis(),
                existingPlaceId = hotel.id
            )

            submissionRepository.submitPlace(submission).fold(
                onSuccess = {
                    _submitMessage.value = "Update submitted! Awaiting admin approval."
                    loadMySubmissions()
                },
                onFailure = { _submitMessage.value = "Submit failed: ${it.message}" }
            )
            _isSubmitting.value = false
        }
    }

    fun updatePlace(place: PlaceToVisit, newImageBytesList: List<ByteArray> = emptyList()) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _submitMessage.value = "Not logged in"
                return@launch
            }
            _isSubmitting.value = true

            val placeImageUrls = mutableListOf<String>()
            place.images?.let { placeImageUrls.addAll(it) }

            val uploaded = coroutineScope {
                newImageBytesList.map { bytes ->
                    async { submissionRepository.uploadPlaceImage(user.uid, bytes) }
                }.awaitAll()
            }
            uploaded.forEach { result ->
                result.getOrNull()?.let { placeImageUrls.add(it) }
            }

            val submission = PlaceSubmission(
                ownerId = user.uid,
                ownerEmail = user.email ?: "",
                placeType = PlaceType.PLACE_TO_VISIT,
                name = place.name,
                city = place.city ?: "",
                description = place.description ?: "",
                address = place.address ?: "",
                websiteUrl = place.websiteUrl ?: "",
                placeImages = placeImageUrls,
                status = SubmissionStatus.PENDING,
                submittedAt = System.currentTimeMillis(),
                existingPlaceId = place.id
            )

            submissionRepository.submitPlace(submission).fold(
                onSuccess = {
                    _submitMessage.value = "Update submitted! Awaiting admin approval."
                    loadMySubmissions()
                },
                onFailure = { _submitMessage.value = "Submit failed: ${it.message}" }
            )
            _isSubmitting.value = false
        }
    }

    fun updateEquipmentShop(shop: EquipmentShop, newImageBytesList: List<ByteArray> = emptyList()) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _submitMessage.value = "Not logged in"
                return@launch
            }
            _isSubmitting.value = true

            val placeImageUrls = mutableListOf<String>()
            shop.images?.let { placeImageUrls.addAll(it) }

            val uploaded = coroutineScope {
                newImageBytesList.map { bytes ->
                    async { submissionRepository.uploadPlaceImage(user.uid, bytes) }
                }.awaitAll()
            }
            uploaded.forEach { result ->
                result.getOrNull()?.let { placeImageUrls.add(it) }
            }

            val submission = PlaceSubmission(
                ownerId = user.uid,
                ownerEmail = user.email ?: "",
                placeType = PlaceType.EQUIPMENT_SHOP,
                name = shop.name,
                city = shop.city ?: "",
                type = shop.type,
                description = shop.description ?: "",
                address = shop.address ?: "",
                phoneNumber = shop.phoneNumber ?: "",
                websiteUrl = shop.websiteUrl ?: "",
                placeImages = placeImageUrls,
                status = SubmissionStatus.PENDING,
                submittedAt = System.currentTimeMillis(),
                existingPlaceId = shop.id
            )

            submissionRepository.submitPlace(submission).fold(
                onSuccess = {
                    _submitMessage.value = "Update submitted! Awaiting admin approval."
                    loadMySubmissions()
                },
                onFailure = { _submitMessage.value = "Submit failed: ${it.message}" }
            )
            _isSubmitting.value = false
        }
    }

    fun submitPlace(
        placeType: PlaceType,
        name: String,
        city: String,
        type: String,
        description: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
        receiptImageBytes: ByteArray?,
        placeImageBytesList: List<ByteArray>
    ) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _submitMessage.value = "Not logged in"
                return@launch
            }
            _isSubmitting.value = true
            _submitMessage.value = null

            val receiptUrlDeferred = receiptImageBytes?.let { bytes ->
                async { submissionRepository.uploadReceipt(user.uid, bytes) }
            }
            val placeImageUrlsDeferred = coroutineScope {
                placeImageBytesList.map { bytes ->
                    async { submissionRepository.uploadPlaceImage(user.uid, bytes) }
                }
            }

            var receiptUrl = ""
            receiptUrlDeferred?.await()?.fold(
                onSuccess = { receiptUrl = it },
                onFailure = {
                    _isSubmitting.value = false
                    _submitMessage.value = "Failed to upload receipt: ${it.message}"
                    return@launch
                }
            )

            val placeImageUrls = placeImageUrlsDeferred.awaitAll()
                .mapNotNull { it.getOrNull() }

            val submission = PlaceSubmission(
                ownerId = user.uid,
                ownerEmail = user.email ?: "",
                placeType = placeType,
                name = name,
                city = city,
                type = type,
                description = description,
                address = address,
                phoneNumber = phoneNumber,
                websiteUrl = websiteUrl,
                receiptImageUrl = receiptUrl,
                placeImages = placeImageUrls,
                status = SubmissionStatus.PENDING,
                submittedAt = System.currentTimeMillis()
            )

            submissionRepository.submitPlace(submission).fold(
                onSuccess = {
                    _submitMessage.value = "Submitted! Awaiting admin approval."
                    loadMySubmissions()
                },
                onFailure = {
                    _submitMessage.value = "Submit failed: ${it.message}"
                }
            )
            _isSubmitting.value = false
        }
    }

    fun clearMessage() {
        _submitMessage.value = null
    }
}
