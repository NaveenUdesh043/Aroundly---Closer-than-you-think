package com.aroundly.aroundly.uiLibrary.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aroundly.aroundly.uiLibrary.viewModel.AuthViewModel

@Composable
fun SignupScreen(
    viewModel: AuthViewModel = viewModel(),
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFFF4F0E9)
    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF131013),
            Color(0xFF2B2320)
        )
    )
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        // Header card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .background(headerGradient)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Back",
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Explorer account chip
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0x33FFC857),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "EXPLORER ACCOUNT",
                            color = Color(0xFFFFC857),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Create account",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Join Aroundly today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2DED6)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // White content sheet
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Personal Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF25211C)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tell us about yourself",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8C8478)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Full name (first + last)
                Text(
                    text = "FIRST NAME",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9183)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = "$firstName".trim(),
                    onValueChange = { value ->
                        val parts = value.split(" ", limit = 1)
                        firstName = parts.getOrNull(0) ?: ""
                    },
                    placeholder = { Text("John", color = Color(0xFFB8B0A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF7F1E7),
                        unfocusedContainerColor = Color(0xFFF7F1E7),
                        cursorColor = Color(0xFF1C1C1C)
                    ),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "LAST NAME",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9183)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = "$lastName".trim(),
                    onValueChange = { value ->
                        val parts = value.split(" ", limit = 1)
                        lastName = parts.getOrNull(0) ?: ""
                    },
                    placeholder = { Text("Doe", color = Color(0xFFB8B0A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF7F1E7),
                        unfocusedContainerColor = Color(0xFFF7F1E7),
                        cursorColor = Color(0xFF1C1C1C)
                    ),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CONTACT NUMBER",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9183)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    placeholder = { Text("Phone number", color = Color(0xFFB8B0A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF7F1E7),
                        unfocusedContainerColor = Color(0xFFF7F1E7),
                        cursorColor = Color(0xFF1C1C1C)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "EMAIL",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9183)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("you@example.com", color = Color(0xFFB8B0A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF7F1E7),
                        unfocusedContainerColor = Color(0xFFF7F1E7),
                        cursorColor = Color(0xFF1C1C1C)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PASSWORD",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9183)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Min. 8 characters", color = Color(0xFFB8B0A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF7F1E7),
                        unfocusedContainerColor = Color(0xFFF7F1E7),
                        cursorColor = Color(0xFF1C1C1C)
                    ),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color(0XFFAE62ED)
                            )
                        }
                    },
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CONFIRM PASSWORD",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9183)
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Repeat your password", color = Color(0xFFB8B0A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF7F1E7),
                        unfocusedContainerColor = Color(0xFFF7F1E7),
                        cursorColor = Color(0xFF1C1C1C)
                    ),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0XFFAE62ED)
                            )

                        }
                    },
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        errorMessage = ""

                        when {
                            firstName.isBlank() -> errorMessage = "First name is required"
                            lastName.isBlank() -> errorMessage = "Last name is required"
                            contactNumber.isBlank() -> errorMessage = "Contact number is required"
                            email.isBlank() -> errorMessage = "Email is required"
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                errorMessage = "Invalid email format"
                            password.length < 6 ->
                                errorMessage = "Password must be at least 6 characters"
                            password != confirmPassword ->
                                errorMessage = "Passwords do not match"
                            else -> {
                                isLoading = true
                                viewModel.signup(
                                    firstName,
                                    lastName,
                                    contactNumber,
                                    email,
                                    password,
                                    confirmPassword
                                ) { success, message ->
                                    isLoading = false
                                    if (success) {
                                        onSignupSuccess()
                                    } else {
                                        errorMessage = message
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(color = Color.Black, shape = RoundedCornerShape(16.dp)),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    elevation = null
                ) {
                    Text(
                        text = if (isLoading) "Creating account..." else "Continue  →",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                color = Color(0xFF6A6054)
            )
            Text(
                text = "Sign in",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onNavigateToLogin)
            )
        }
    }
}



