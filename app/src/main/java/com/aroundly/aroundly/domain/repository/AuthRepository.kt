package com.aroundly.aroundly.domain.repository

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>
    suspend fun signup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String,
        role: com.aroundly.aroundly.domain.model.UserRole = com.aroundly.aroundly.domain.model.UserRole.USER
    ): Result<Unit>
}
