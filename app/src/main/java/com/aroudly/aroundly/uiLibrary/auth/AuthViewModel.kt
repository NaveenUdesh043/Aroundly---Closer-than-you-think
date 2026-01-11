package com.aroudly.aroundly.uiLibrary.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aroudly.aroundly.data.remote.FirebaseAuthService
import com.aroudly.aroundly.data.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login successful")
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    fun signup(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save additional user info in Firestore
                    val user = hashMapOf("email" to email)
                    firestore.collection("users")
                        .document(auth.currentUser!!.uid)
                        .set(user)
                        .addOnSuccessListener { callback(true, "Signup successful") }
                        .addOnFailureListener { e -> callback(false, e.localizedMessage ?: "Firestore error") }
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Signup failed")
                }
            }
    }
}
