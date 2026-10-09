package com.aroundly.aroundly.uiLibrary.viewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.aroundly.aroundly.domain.model.UserRole
import com.aroundly.aroundly.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signInWithGoogle(idToken: String, callback: (Boolean, String) -> Unit) {
        if (idToken.isBlank()) {
            callback(false, "Google sign-in failed")
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    callback(false, task.exception?.localizedMessage ?: "Google sign-in failed")
                    return@addOnCompleteListener
                }

                val firebaseUser = auth.currentUser
                if (firebaseUser == null) {
                    callback(false, "Google sign-in failed")
                    return@addOnCompleteListener
                }

                val uid = firebaseUser.uid
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            userRepository.cacheUserRole(uid, roleOf(document.getString("role")))
                            callback(true, "Google sign-in successful")
                            return@addOnSuccessListener
                        }

                        val nameParts = (firebaseUser.displayName ?: "").split(" ")
                        val firstName = nameParts.getOrNull(0) ?: ""
                        val lastName = nameParts.getOrNull(1) ?: ""

                        val user = hashMapOf(
                            "uid" to uid,
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "contactNumber" to (firebaseUser.phoneNumber ?: ""),
                            "phoneNumber" to (firebaseUser.phoneNumber ?: ""),
                            "email" to (firebaseUser.email ?: ""),
                            "role" to UserRole.USER.name,
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                        firestore.collection("users").document(uid).set(user)
                            .addOnSuccessListener {
                                userRepository.cacheUserRole(uid, UserRole.USER)
                                callback(true, "Google sign-in successful")
                            }
                            .addOnFailureListener { e ->
                                callback(false, e.localizedMessage ?: "Failed to save user data")
                            }
                    }
                    .addOnFailureListener { e ->
                        callback(false, e.localizedMessage ?: "Google sign-in failed")
                    }
            }
    }

    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            callback(false, "Email and password are required")
            return
        }

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.uid?.let { userRepository.invalidateUserCache(it) }
                    callback(true, "Login successful")
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    private fun roleOf(raw: String?): UserRole = try {
        UserRole.valueOf(raw ?: UserRole.USER.name)
    } catch (e: Exception) {
        UserRole.USER
    }

    fun signup(
        firstName: String,
        lastName: String,
        contactNumber: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: UserRole = UserRole.USER,
        callback: (Boolean, String) -> Unit
    ) {

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

        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val user = hashMapOf(
                        "uid" to uid,
                        "firstName" to firstName.trim(),
                        "lastName" to lastName.trim(),
                        "contactNumber" to contactNumber.trim(),
                        "phoneNumber" to contactNumber.trim(),
                        "email" to email.trim(),
                        "role" to role.name,
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            userRepository.cacheUserRole(uid, role)
                            callback(true, "Signup successful")
                        }
                        .addOnFailureListener { e ->
                            auth.currentUser?.delete()
                            callback(false, e.localizedMessage ?: "Failed to save user data")
                        }

                } else {
                    callback(false, task.exception?.localizedMessage ?: "Signup failed")
                }
            }
    }
}


