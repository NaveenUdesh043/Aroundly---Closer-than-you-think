package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.User
import com.aroundly.aroundly.domain.model.UserRole

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun getUserRole(uid: String): UserRole?

    fun cacheUserRole(uid: String, role: UserRole)

    fun invalidateUserCache(uid: String? = null)
}
