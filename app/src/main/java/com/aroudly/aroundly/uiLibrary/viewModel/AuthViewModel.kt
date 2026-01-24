package com.aroudly.aroundly.uiLibrary.viewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            callback(false, "Email and password are required")
            return
        }

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login successful")
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    fun signup(
        firstName: String,
        lastName: String,
        contactNumber: String,
        email: String,
        password: String,
        confirmPassword: String,
        callback: (Boolean, String) -> Unit
    ) {

        // -------- Validation --------
        when {
            firstName.isBlank() -> {
                callback(false, "First name is required")
                return
            }
            lastName.isBlank() -> {
                callback(false, "Last name is required")
                return
            }
            contactNumber.isBlank() -> {
                callback(false, "Contact number is required")
                return
            }
            contactNumber.length < 8 -> {
                callback(false, "Enter a valid contact number")
                return
            }
            email.isBlank() -> {
                callback(false, "Email is required")
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                callback(false, "Enter a valid email address")
                return
            }
            password.isBlank() -> {
                callback(false, "Password is required")
                return
            }
            password.length < 6 -> {
                callback(false, "Password must be at least 6 characters")
                return
            }
            confirmPassword.isBlank() -> {
                callback(false, "Confirm password is required")
                return
            }
            password != confirmPassword -> {
                callback(false, "Passwords do not match")
                return
            }
        }

        // -------- Firebase Auth --------
        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val user = hashMapOf(
                        "uid" to uid,
                        "firstName" to firstName.trim(),
                        "lastName" to lastName.trim(),
                        "contactNumber" to contactNumber.trim(),
                        "email" to email.trim(),
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            callback(true, "Signup successful")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.localizedMessage ?: "Failed to save user data")
                        }

                } else {
                    callback(false, task.exception?.localizedMessage ?: "Signup failed")
                }
            }
    }
}


