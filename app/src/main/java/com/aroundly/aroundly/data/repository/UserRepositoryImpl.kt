package com.aroundly.aroundly.data.repository

import com.aroundly.aroundly.domain.model.User
import com.aroundly.aroundly.domain.model.UserRole
import com.aroundly.aroundly.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val doc = firestore.collection("users").document(firebaseUser.uid).get().await()
        return doc.toUser(firebaseUser.uid)
    }

    override suspend fun getUserRole(uid: String): UserRole? {
        val doc = firestore.collection("users").document(uid).get().await()
        val roleStr = doc.getString("role") ?: return UserRole.USER
        return try {
            UserRole.valueOf(roleStr)
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
            role = try {
                UserRole.valueOf(getString("role") ?: "USER")
            } catch (e: Exception) {
                UserRole.USER
            }
        )
    }
}
