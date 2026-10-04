package com.aroundly.aroundly.domain.repository

import com.aroundly.aroundly.domain.model.User
import com.aroundly.aroundly.domain.model.UserRole

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun getUserRole(uid: String): UserRole?

    /** Primes the role cache after a sign-in or sign-up already knows the role,
     *  so the next role lookup does not repeat the Firestore read. */
    fun cacheUserRole(uid: String, role: UserRole)

    /** Drops cached roles for [uid], or for every user when null. */
    fun invalidateUserCache(uid: String? = null)
}
