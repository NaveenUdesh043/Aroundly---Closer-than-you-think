package com.aroundly.aroundly.data.remote

import com.aroundly.aroundly.domain.model.User
import com.aroundly.aroundly.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(
            email,
            password
        ).await()
    }

    suspend fun signup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String,
        role: UserRole = UserRole.USER
    ) {
        val result = auth.createUserWithEmailAndPassword(
            email,
            password
        ).await()

        val user = User(
            uid = result.user!!.uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber,
            role = role
        )

        firestore.collection("users")
            .document(user.uid)
            .set(
                mapOf(
                    "uid" to user.uid,
                    "firstName" to user.firstName,
                    "lastName" to user.lastName,
                    "email" to user.email,
                    "phoneNumber" to user.phoneNumber,
                    "contactNumber" to user.phoneNumber,
                    "role" to user.role.name
                )
            )
            .await()
    }
}
