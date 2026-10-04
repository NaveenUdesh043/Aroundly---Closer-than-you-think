package com.aroundly.aroundly.data.repository

import com.aroundly.aroundly.domain.model.User
import com.aroundly.aroundly.domain.model.UserRole
import com.aroundly.aroundly.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Short-lived cache of user roles.
 *
 * Sign-in, the profile screens and the role router each needed the role, and
 * every one of those was a separate Firestore round trip even though the
 * document cannot change mid-session except on an explicit role update. The
 * cache removes those repeated reads; [fetchLock] collapses concurrent misses
 * so two screens appearing at once still cost a single request.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val roleCache = ConcurrentHashMap<String, CachedRole>()
    private val fetchLock = Mutex()

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val doc = firestore.collection("users").document(firebaseUser.uid).get().await()
        return doc.toUser(firebaseUser.uid)
    }

    override suspend fun getUserRole(uid: String): UserRole? {
        roleCache[uid]?.takeIf { !it.isExpired() }?.let { return it.value }

        return fetchLock.withLock {
            // Another caller may have filled the cache while we waited.
            roleCache[uid]?.takeIf { !it.isExpired() }?.value
                ?: fetchAndCache(uid)
        }
    }

    override fun cacheUserRole(uid: String, role: UserRole) {
        roleCache[uid] = CachedRole(role)
    }

    override fun invalidateUserCache(uid: String?) {
        if (uid == null) roleCache.clear() else roleCache.remove(uid)
    }

    private suspend fun fetchAndCache(uid: String): UserRole {
        val doc = firestore.collection("users").document(uid).get().await()
        val role = doc.toRole()
        roleCache[uid] = CachedRole(role)
        return role
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toRole(): UserRole {
        val raw = getString("role") ?: return UserRole.USER
        return try {
            UserRole.valueOf(raw)
        } catch (e: Exception) {
            UserRole.USER
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toUser(uid: String): User? {
        return User(
            uid = uid,
            firstName = getString("firstName") ?: "",
            lastName = getString("lastName") ?: "",
            email = getString("email") ?: "",
            phoneNumber = getString("contactNumber") ?: getString("phoneNumber") ?: "",
            role = toRole()
        )
    }

    private data class CachedRole(
        val value: UserRole,
        val storedAt: Long = System.currentTimeMillis()
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - storedAt > TTL_MILLIS
    }

    private companion object {
        /** Covers a navigation session, while still picking up a role change
         *  without needing a reinstall. */
        const val TTL_MILLIS = 5 * 60 * 1000L
    }
}
