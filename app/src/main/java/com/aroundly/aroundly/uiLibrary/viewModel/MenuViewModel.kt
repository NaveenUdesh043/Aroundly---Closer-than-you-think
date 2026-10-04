package com.aroundly.aroundly.uiLibrary.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroundly.aroundly.domain.model.MenuItem
import com.aroundly.aroundly.domain.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Menu state for a single shop. Keyed per shop because the manager dashboard
 * renders one card per place, and a single shared list let whichever query
 * finished last overwrite every other card.
 */
data class ShopMenuState(
    val images: List<MenuItem> = emptyList(),
    val isLoading: Boolean = false,
    val isMutating: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    @Suppress("unused") @ApplicationContext private val context: Context,
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _shopMenus = MutableStateFlow<Map<String, ShopMenuState>>(emptyMap())
    val shopMenus: StateFlow<Map<String, ShopMenuState>> = _shopMenus.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    /** In-flight loads, so scrolling a card back into view cannot double-fetch. */
    private val inFlight = mutableMapOf<String, Job>()

    fun stateFor(shopId: String): ShopMenuState =
        _shopMenus.value[shopId] ?: ShopMenuState()

    fun loadMenuImages(shopId: String, force: Boolean = false) {
        if (inFlight[shopId]?.isActive == true) return
        if (!force && _shopMenus.value[shopId]?.images?.isNotEmpty() == true) return

        inFlight[shopId] = viewModelScope.launch {
            update(shopId) { it.copy(isLoading = true, error = null) }
            menuRepository.getMenuImages(shopId).fold(
                onSuccess = { images ->
                    update(shopId) {
                        it.copy(images = images, isLoading = false, error = null)
                    }
                },
                onFailure = { error ->
                    // Previously swallowed, so a failed read rendered as "no
                    // images" and looked like the upload had not been saved.
                    update(shopId) {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Could not load menu images"
                        )
                    }
                }
            )
        }
    }

    fun uploadMenuImage(shopId: String, imageBytes: ByteArray) {
        viewModelScope.launch {
            update(shopId) { it.copy(isMutating = true, error = null) }
            menuRepository.uploadMenuImage(shopId, imageBytes).fold(
                onSuccess = {
                    _message.value = "Image uploaded"
                    // Read the server result rather than patching the local
                    // list, so a rejected write cannot show a phantom image.
                    loadMenuImages(shopId, force = true)
                },
                onFailure = {
                    _message.value = "Failed to upload image: ${it.message}"
                }
            )
            update(shopId) { it.copy(isMutating = false) }
        }
    }

    fun deleteMenuImage(imageId: String, shopId: String) {
        viewModelScope.launch {
            update(shopId) { it.copy(isMutating = true, error = null) }
            menuRepository.deleteMenuImage(imageId, shopId).fold(
                onSuccess = {
                    _message.value = "Image deleted"
                    loadMenuImages(shopId, force = true)
                },
                onFailure = {
                    _message.value = "Failed to delete image: ${it.message}"
                }
            )
            update(shopId) { it.copy(isMutating = false) }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    private inline fun update(shopId: String, transform: (ShopMenuState) -> ShopMenuState) {
        _shopMenus.value = _shopMenus.value.toMutableMap().apply {
            this[shopId] = transform(this[shopId] ?: ShopMenuState())
        }
    }
}
