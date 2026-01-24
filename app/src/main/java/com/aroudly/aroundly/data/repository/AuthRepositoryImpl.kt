package com.aroudly.aroundly.data.repository

import com.aroudly.aroundly.data.remote.FirebaseAuthService
import com.aroudly.aroundly.domain.repository.AuthRepository

class AuthRepositoryImpl(
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
        phoneNumber: String
    ) = runCatching {
            service.signup(
                firstName,
                lastName,
                email,
                password,
                phoneNumber
            ) }
}
