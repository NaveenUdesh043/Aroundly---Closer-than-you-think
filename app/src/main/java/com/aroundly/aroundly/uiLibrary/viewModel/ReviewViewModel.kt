package com.aroundly.aroundly.uiLibrary.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.domain.model.Review
import com.aroundly.aroundly.domain.repository.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val placeId: String = savedStateHandle.get<String>("id") ?: ""
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _averageRating = MutableStateFlow(0f)
    val averageRating: StateFlow<Float> = _averageRating.asStateFlow()

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> = _reviewCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _submitResult = MutableStateFlow<Boolean?>(null)
    val submitResult: StateFlow<Boolean?> = _submitResult.asStateFlow()

    private val _userReview = MutableStateFlow<Review?>(null)
    val userReview: StateFlow<Review?> = _userReview.asStateFlow()

    init {
        loadReviews()
    }

    fun loadReviews() {
        if (placeId.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _reviews.value = reviewRepository.getReviews(placeId)
            _averageRating.value = reviewRepository.getAverageRating(placeId)
            _reviewCount.value = reviewRepository.getReviewCount(placeId)

            val userId = auth.currentUser?.uid
            if (userId != null) {
                _userReview.value = reviewRepository.getUserReviewForPlace(userId, placeId)
            }
            _isLoading.value = false
        }
    }

    fun submitReview(rating: Float, text: String) {
        val user = auth.currentUser ?: return
        if (rating < 1f) return

        viewModelScope.launch {
            _isLoading.value = true

            val userDoc = try {
                firestore.collection("users").document(user.uid).get().await()
            } catch (e: Exception) {
                null
            }
            val firstName = userDoc?.getString("firstName") ?: ""
            val lastName = userDoc?.getString("lastName") ?: ""
            val userName = "$firstName $lastName".trim().ifEmpty { "Anonymous" }

            val review = Review(
                placeId = placeId,
                userId = user.uid,
                userName = userName,
                rating = rating,
                text = text
            )

            val result = reviewRepository.addReview(review)
            _submitResult.value = result.isSuccess
            loadReviews()
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.deleteReview(reviewId)
            loadReviews()
        }
    }

    fun clearSubmitResult() {
        _submitResult.value = null
    }
}
