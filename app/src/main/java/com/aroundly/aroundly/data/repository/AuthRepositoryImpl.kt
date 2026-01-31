package com.aroundly.aroundly.data.repository

import com.aroundly.aroundly.data.remote.FirebaseAuthService
import com.aroundly.aroundly.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: FirebaseAuthService
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String) =
        runCatching {
            service.login(email, password) }

    override suspend fun signup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String,
        role: com.aroundly.aroundly.domain.model.UserRole
    ) = runCatching {
            service.signup(
                firstName,
                lastName,
                email,
                password,
                phoneNumber,
                role
            ) }
}
