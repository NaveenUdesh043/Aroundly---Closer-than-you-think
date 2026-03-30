package com.aroundly.aroundly.uiLibrary.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.data.location.LocationProvider
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceReview
import com.aroundly.aroundly.domain.model.PlaceReviewKinds
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.domain.repository.DashboardRepository
import com.aroundly.aroundly.domain.repository.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository,
    private val reviewRepository: ReviewRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val id: String = savedStateHandle.get<String>("id") ?: ""
    private val _restaurant = MutableStateFlow<Restaurant?>(null)
    val restaurant: StateFlow<Restaurant?> = _restaurant.asStateFlow()
    private val _reviews = MutableStateFlow<List<PlaceReview>>(emptyList())
    val reviews: StateFlow<List<PlaceReview>> = _reviews.asStateFlow()
    val averageRating: StateFlow<Float> = _reviews.map { list ->
        if (list.isEmpty()) 0f else list.map { it.rating }.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)
    private val _distanceKm = MutableStateFlow<Double?>(null)
    val distanceKm: StateFlow<Double?> = _distanceKm.asStateFlow()
    private val _reviewRating = MutableStateFlow(5f)
    val reviewRating: StateFlow<Float> = _reviewRating.asStateFlow()
    private val _reviewComment = MutableStateFlow("")
    val reviewComment: StateFlow<String> = _reviewComment.asStateFlow()
    private val _reviewMessage = MutableStateFlow<String?>(null)
    val reviewMessage: StateFlow<String?> = _reviewMessage.asStateFlow()
    private val _submittingReview = MutableStateFlow(false)
    val submittingReview: StateFlow<Boolean> = _submittingReview.asStateFlow()

    init {
        viewModelScope.launch {
            _restaurant.value = dashboardRepository.getRestaurantById(id)
            refreshReviews()
            computeDistance()
        }
    }

    private suspend fun computeDistance() {
        val r = _restaurant.value ?: return
        _distanceKm.value = locationProvider.distanceFromUserToPlaceKm(
            r.address,
            r.latitude,
            r.longitude
        )
    }

    fun refreshDistance() {
        viewModelScope.launch { computeDistance() }
    }

    private suspend fun refreshReviews() {
        _reviews.value = reviewRepository.getReviews(id, PlaceReviewKinds.RESTAURANTS)
    }

    fun setReviewRating(v: Float) {
        _reviewRating.value = v.coerceIn(1f, 5f)
    }

    fun setReviewComment(c: String) {
        _reviewComment.value = c
    }

    fun submitReview() {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _reviewMessage.value = "Sign in to leave a review"
                return@launch
            }
            val comment = _reviewComment.value.trim()
            if (comment.isEmpty()) {
                _reviewMessage.value = "Please write a comment"
                return@launch
            }
            _submittingReview.value = true
            _reviewMessage.value = null
            val review = PlaceReview(
                placeId = id,
                placeKind = PlaceReviewKinds.RESTAURANTS,
                userId = user.uid,
                userName = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                rating = _reviewRating.value,
                comment = comment,
                createdAt = System.currentTimeMillis()
            )
            reviewRepository.addReview(review).fold(
                onSuccess = {
                    _reviewComment.value = ""
                    refreshReviews()
                },
                onFailure = { _reviewMessage.value = it.message ?: "Failed to post review" }
            )
            _submittingReview.value = false
        }
    }
}

@HiltViewModel
class HotelDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository,
    private val reviewRepository: ReviewRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val id: String = savedStateHandle.get<String>("id") ?: ""
    private val _hotel = MutableStateFlow<Hotel?>(null)
    val hotel: StateFlow<Hotel?> = _hotel.asStateFlow()
    private val _reviews = MutableStateFlow<List<PlaceReview>>(emptyList())
    val reviews: StateFlow<List<PlaceReview>> = _reviews.asStateFlow()
    val averageRating: StateFlow<Float> = _reviews.map { list ->
        if (list.isEmpty()) 0f else list.map { it.rating }.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)
    private val _distanceKm = MutableStateFlow<Double?>(null)
    val distanceKm: StateFlow<Double?> = _distanceKm.asStateFlow()
    private val _reviewRating = MutableStateFlow(5f)
    val reviewRating: StateFlow<Float> = _reviewRating.asStateFlow()
    private val _reviewComment = MutableStateFlow("")
    val reviewComment: StateFlow<String> = _reviewComment.asStateFlow()
    private val _reviewMessage = MutableStateFlow<String?>(null)
    val reviewMessage: StateFlow<String?> = _reviewMessage.asStateFlow()
    private val _submittingReview = MutableStateFlow(false)
    val submittingReview: StateFlow<Boolean> = _submittingReview.asStateFlow()

    init {
        viewModelScope.launch {
            _hotel.value = dashboardRepository.getHotelById(id)
            _reviews.value = reviewRepository.getReviews(id, PlaceReviewKinds.HOTELS)
            computeDistance()
        }
    }

    private suspend fun computeDistance() {
        val h = _hotel.value ?: return
        _distanceKm.value = locationProvider.distanceFromUserToPlaceKm(
            h.address,
            h.latitude,
            h.longitude
        )
    }

    fun refreshDistance() {
        viewModelScope.launch { computeDistance() }
    }

    fun setReviewRating(v: Float) {
        _reviewRating.value = v.coerceIn(1f, 5f)
    }

    fun setReviewComment(c: String) {
        _reviewComment.value = c
    }

    fun submitReview() {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _reviewMessage.value = "Sign in to leave a review"
                return@launch
            }
            val comment = _reviewComment.value.trim()
            if (comment.isEmpty()) {
                _reviewMessage.value = "Please write a comment"
                return@launch
            }
            _submittingReview.value = true
            _reviewMessage.value = null
            val review = PlaceReview(
                placeId = id,
                placeKind = PlaceReviewKinds.HOTELS,
                userId = user.uid,
                userName = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                rating = _reviewRating.value,
                comment = comment,
                createdAt = System.currentTimeMillis()
            )
            reviewRepository.addReview(review).fold(
                onSuccess = {
                    _reviewComment.value = ""
                    _reviews.value = reviewRepository.getReviews(id, PlaceReviewKinds.HOTELS)
                },
                onFailure = { _reviewMessage.value = it.message ?: "Failed to post review" }
            )
            _submittingReview.value = false
        }
    }
}

@HiltViewModel
class PlaceDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository,
    private val reviewRepository: ReviewRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val id: String = savedStateHandle.get<String>("id") ?: ""
    private val _place = MutableStateFlow<PlaceToVisit?>(null)
    val place: StateFlow<PlaceToVisit?> = _place.asStateFlow()
    private val _reviews = MutableStateFlow<List<PlaceReview>>(emptyList())
    val reviews: StateFlow<List<PlaceReview>> = _reviews.asStateFlow()
    val averageRating: StateFlow<Float> = _reviews.map { list ->
        if (list.isEmpty()) 0f else list.map { it.rating }.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)
    private val _distanceKm = MutableStateFlow<Double?>(null)
    val distanceKm: StateFlow<Double?> = _distanceKm.asStateFlow()
    private val _reviewRating = MutableStateFlow(5f)
    val reviewRating: StateFlow<Float> = _reviewRating.asStateFlow()
    private val _reviewComment = MutableStateFlow("")
    val reviewComment: StateFlow<String> = _reviewComment.asStateFlow()
    private val _reviewMessage = MutableStateFlow<String?>(null)
    val reviewMessage: StateFlow<String?> = _reviewMessage.asStateFlow()
    private val _submittingReview = MutableStateFlow(false)
    val submittingReview: StateFlow<Boolean> = _submittingReview.asStateFlow()

    init {
        viewModelScope.launch {
            _place.value = dashboardRepository.getPlaceById(id)
            _reviews.value = reviewRepository.getReviews(id, PlaceReviewKinds.PLACES)
            computeDistance()
        }
    }

    private suspend fun computeDistance() {
        val p = _place.value ?: return
        _distanceKm.value = locationProvider.distanceFromUserToPlaceKm(
            p.address,
            p.latitude,
            p.longitude
        )
    }

    fun refreshDistance() {
        viewModelScope.launch { computeDistance() }
    }

    fun setReviewRating(v: Float) {
        _reviewRating.value = v.coerceIn(1f, 5f)
    }

    fun setReviewComment(c: String) {
        _reviewComment.value = c
    }

    fun submitReview() {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _reviewMessage.value = "Sign in to leave a review"
                return@launch
            }
            val comment = _reviewComment.value.trim()
            if (comment.isEmpty()) {
                _reviewMessage.value = "Please write a comment"
                return@launch
            }
            _submittingReview.value = true
            _reviewMessage.value = null
            val review = PlaceReview(
                placeId = id,
                placeKind = PlaceReviewKinds.PLACES,
                userId = user.uid,
                userName = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                rating = _reviewRating.value,
                comment = comment,
                createdAt = System.currentTimeMillis()
            )
            reviewRepository.addReview(review).fold(
                onSuccess = {
                    _reviewComment.value = ""
                    _reviews.value = reviewRepository.getReviews(id, PlaceReviewKinds.PLACES)
                },
                onFailure = { _reviewMessage.value = it.message ?: "Failed to post review" }
            )
            _submittingReview.value = false
        }
    }
}

@HiltViewModel
class EquipmentDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dashboardRepository: DashboardRepository,
    private val reviewRepository: ReviewRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val id: String = savedStateHandle.get<String>("id") ?: ""
    private val _equipment = MutableStateFlow<EquipmentShop?>(null)
    val equipment: StateFlow<EquipmentShop?> = _equipment.asStateFlow()
    private val _reviews = MutableStateFlow<List<PlaceReview>>(emptyList())
    val reviews: StateFlow<List<PlaceReview>> = _reviews.asStateFlow()
    val averageRating: StateFlow<Float> = _reviews.map { list ->
        if (list.isEmpty()) 0f else list.map { it.rating }.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)
    private val _distanceKm = MutableStateFlow<Double?>(null)
    val distanceKm: StateFlow<Double?> = _distanceKm.asStateFlow()
    private val _reviewRating = MutableStateFlow(5f)
    val reviewRating: StateFlow<Float> = _reviewRating.asStateFlow()
    private val _reviewComment = MutableStateFlow("")
    val reviewComment: StateFlow<String> = _reviewComment.asStateFlow()
    private val _reviewMessage = MutableStateFlow<String?>(null)
    val reviewMessage: StateFlow<String?> = _reviewMessage.asStateFlow()
    private val _submittingReview = MutableStateFlow(false)
    val submittingReview: StateFlow<Boolean> = _submittingReview.asStateFlow()

    init {
        viewModelScope.launch {
            _equipment.value = dashboardRepository.getEquipmentShopById(id)
            _reviews.value = reviewRepository.getReviews(id, PlaceReviewKinds.EQUIPMENT)
            computeDistance()
        }
    }

    private suspend fun computeDistance() {
        val e = _equipment.value ?: return
        _distanceKm.value = locationProvider.distanceFromUserToPlaceKm(
            e.address,
            e.latitude,
            e.longitude
        )
    }

    fun refreshDistance() {
        viewModelScope.launch { computeDistance() }
    }

    fun setReviewRating(v: Float) {
        _reviewRating.value = v.coerceIn(1f, 5f)
    }

    fun setReviewComment(c: String) {
        _reviewComment.value = c
    }

    fun submitReview() {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser ?: run {
                _reviewMessage.value = "Sign in to leave a review"
                return@launch
            }
            val comment = _reviewComment.value.trim()
            if (comment.isEmpty()) {
                _reviewMessage.value = "Please write a comment"
                return@launch
            }
            _submittingReview.value = true
            _reviewMessage.value = null
            val review = PlaceReview(
                placeId = id,
                placeKind = PlaceReviewKinds.EQUIPMENT,
                userId = user.uid,
                userName = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                rating = _reviewRating.value,
                comment = comment,
                createdAt = System.currentTimeMillis()
            )
            reviewRepository.addReview(review).fold(
                onSuccess = {
                    _reviewComment.value = ""
                    _reviews.value = reviewRepository.getReviews(id, PlaceReviewKinds.EQUIPMENT)
                },
                onFailure = { _reviewMessage.value = it.message ?: "Failed to post review" }
            )
            _submittingReview.value = false
        }
    }
}
