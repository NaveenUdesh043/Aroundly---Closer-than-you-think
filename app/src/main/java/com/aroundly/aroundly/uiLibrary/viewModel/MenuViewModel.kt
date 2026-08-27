package com.aroundly.aroundly.uiLibrary.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.domain.model.MenuItem
import com.aroundly.aroundly.domain.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _menuImages = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuImages: StateFlow<List<MenuItem>> = _menuImages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun loadMenuImages(shopId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _menuImages.value = menuRepository.getMenuImages(shopId)
            _isLoading.value = false
        }
    }

    fun uploadMenuImage(shopId: String, imageBytes: ByteArray) {
        viewModelScope.launch {
            _isLoading.value = true
            menuRepository.uploadMenuImage(shopId, imageBytes).onSuccess {
                _message.value = "Image uploaded"
                loadMenuImages(shopId)
            }.onFailure {
                _message.value = "Failed to upload image"
            }
            _isLoading.value = false
        }
    }

    fun deleteMenuImage(imageId: String, shopId: String) {
        viewModelScope.launch {
            menuRepository.deleteMenuImage(imageId, shopId).onSuccess {
                _message.value = "Image deleted"
                loadMenuImages(shopId)
            }.onFailure {
                _message.value = "Failed to delete image"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
