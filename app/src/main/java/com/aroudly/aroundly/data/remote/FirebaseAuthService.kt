package com.aroudly.aroundly.data.remote

import com.aroudly.aroundly.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signup(name: String, email: String, password: String, phoneNumber: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()

        val user = User(
            uid = result.user!!.uid,
            name = name,
            email = email,
            phoneNumber = phoneNumber
        )

        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }
}
