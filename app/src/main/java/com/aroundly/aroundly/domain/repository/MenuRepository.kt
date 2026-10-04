package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.MenuItem

interface MenuRepository {
    suspend fun getMenuImages(shopId: String): Result<List<MenuItem>>
    suspend fun uploadMenuImage(shopId: String, imageBytes: ByteArray): Result<String>
    suspend fun deleteMenuImage(imageId: String, shopId: String): Result<Unit>
}
