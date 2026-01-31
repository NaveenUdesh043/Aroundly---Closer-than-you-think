package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun getUserRole(uid: String): com.aroundly.aroundly.domain.model.UserRole?
}
