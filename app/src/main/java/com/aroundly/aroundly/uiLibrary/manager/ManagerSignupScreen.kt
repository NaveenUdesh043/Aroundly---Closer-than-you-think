package com.aroundly.aroundly.uiLibrary.manager

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aroundly.aroundly.R
import com.aroundly.aroundly.domain.model.UserRole
import com.aroundly.aroundly.ui.theme.PrimaryTeal
import com.aroundly.aroundly.uiLibrary.viewModel.AuthViewModel

@Composable
fun ManagerSignupScreen(
    viewModel: AuthViewModel = viewModel(),
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val logoGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6236FF), // Purple
            Color(0xFFC042FF), // Magenta
            Color(0xFF42E8FF)  // Cyan
        )
    )
    val logoGradientButton = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF6236FF), // Deep Purple
            Color(0xFFC042FF), // Magenta
            Color(0xFF42E8FF)  // Cyan/Light Blue
        )
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logogogog),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Register as Business Owner",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C84)
        )
        Text(
            text = "Welcome to Aroundly",
          //  style = MaterialTheme.typography.bodyMedium,
            style = TextStyle(
                brush = logoGradient,
                fontWeight = FontWeight.Bold,
                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily
            ),
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name",
                color = Color(0xFF1C1C84)
            ) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1C1C84),
                unfocusedBorderColor = Color(0xFF1C1C84),
                focusedLabelColor = Color(0xFF1C1C84),
                unfocusedLabelColor = Color(0xFF1C1C84)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name",
                color = Color(0xFF1C1C84)
            ) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1C1C84),
                unfocusedBorderColor = Color(0xFF1C1C84),
                focusedLabelColor = Color(0xFF1C1C84),
                unfocusedLabelColor = Color(0xFF1C1C84)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = businessName,
            onValueChange = { businessName = it },
            label = { Text("Business Name",
                color = Color(0xFF1C1C84)
            ) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1C1C84),
                unfocusedBorderColor = Color(0xFF1C1C84),
                focusedLabelColor = Color(0xFF1C1C84),
                unfocusedLabelColor = Color(0xFF1C1C84)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = { Text("Contact Number",
                color = Color(0xFF1C1C84)
            ) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1C1C84),
                unfocusedBorderColor = Color(0xFF1C1C84),
                focusedLabelColor = Color(0xFF1C1C84),
                unfocusedLabelColor = Color(0xFF1C1C84)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(
                "Email",
                color = Color(0xFF1C1C84)
            )
                    },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password field with visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password",
                color = Color(0xFF1C1C84)
            ) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1C1C84),
                unfocusedBorderColor = Color(0xFF1C1C84),
                focusedLabelColor = Color(0xFF1C1C84),
                unfocusedLabelColor = Color(0xFF1C1C84)
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color(0XFFAE62ED)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password field with visibility toggle
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password",
                color = Color(0xFF1C1C84)

                ) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1C1C84),
                unfocusedBorderColor = Color(0xFF1C1C84),
                focusedLabelColor = Color(0xFF1C1C84),
                unfocusedLabelColor = Color(0xFF1C1C84)
            ),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                        tint = Color(0XFFAE62ED)
                    )
                }
            },
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
                    password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                    password != confirmPassword -> errorMessage = "Passwords do not match"
                    else -> {
                        isLoading = true
                        viewModel.signup(
                            firstName,
                            lastName,
                            contactNumber,
                            email,
                            password,
                            confirmPassword,
                            callback = { success, message ->
                                isLoading = false
                                if (success) onSignupSuccess()
                                else errorMessage = message
                            },
                            role = UserRole.MANAGER
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = logoGradientButton, shape = ButtonDefaults.shape),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
            ),
            elevation = null
        ) {
            Text(if (isLoading) "Creating account..." else "Register as Business Owner")
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login",
                style = TextStyle(
                    brush = logoGradient,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

