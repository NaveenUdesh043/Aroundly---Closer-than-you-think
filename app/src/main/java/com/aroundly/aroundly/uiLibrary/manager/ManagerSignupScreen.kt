package com.aroundly.aroundly.uiLibrary.manager

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import com.aroundly.aroundly.domain.model.UserRole
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

    var termsAccepted by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF131013),
            Color(0xFF2B2320)
        )
    )
    val backgroundColor = Color(0xFFF4F0E9)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Back", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Text(
                            text = "Business",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 26.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Partner",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC857),
                            fontSize = 26.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "List your business on Aroundly",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2DED6)
                    )

                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Account form sheet
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 24.dp)
                .fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Your Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF25211C)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Create your manager profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8C8478)
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                    placeholder = { Text("Smith", color = Color(0xFFB8B0A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF7F1E7),
                        unfocusedContainerColor = Color(0xFFF7F1E7),
                        cursorColor = Color(0xFF1C1C1C)
                    ),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "BUSINESS NAME",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9183)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    placeholder = { Text("Business name", color = Color(0xFFB8B0A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF7F1E7),
                        unfocusedContainerColor = Color(0xFFF7F1E7),
                        cursorColor = Color(0xFF1C1C1C)
                    ),
                    singleLine = true,
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
                    placeholder = { Text("business@email.com", color = Color(0xFFB8B0A4)) },
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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PHONE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9A9183)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    placeholder = { Text("+94 7X XXX XXXX", color = Color(0xFFB8B0A4)) },
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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color(0XFFAE62ED)
                            )
                        }
                    },
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
                        val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0XFFAE62ED)
                            )
                        }
                    },
                    textStyle = TextStyle(color = Color(0xFF1C1C1C)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it }
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text("I have read and accept the Terms and Conditions")
                TextButton(onClick = { showTermsDialog = true }) {
                    Text("View Terms and Conditions")
                }
            }
        }


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
//                    lastName.isBlank() -> errorMessage = "Last name is required"
                    contactNumber.isBlank() -> errorMessage = "Contact number is required"
                    email.isBlank() -> errorMessage = "Email is required"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                        errorMessage = "Invalid email format"
                    password.length < 6 -> errorMessage = "Password must be at least 6 characters"
                    password != confirmPassword -> errorMessage = "Passwords do not match"
                    !termsAccepted -> errorMessage = "You must accept the Terms and Conditions"
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
                .height(52.dp)
                .background(color = Color(0xFFFFB02C), shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
            enabled = !isLoading && termsAccepted ,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Gray.copy(alpha = 0.1f)
            ),
            elevation = null
        ) {
            Text(
                text = if (isLoading) "Creating account..." else "Continue  →",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login",
                style = TextStyle(
                    color = Color(0xFF1C1C1C),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                confirmButton = {
                    TextButton(onClick = { showTermsDialog = false }) {
                        Text("Close")
                    }
                },
                title = { Text("Terms and Conditions") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            "Dummy Terms and Conditions\n\n" +
                                    "1. As a business owner, you are responsible for ensuring that all information " +
                                    "you provide (shop name, address, website, and contact details) is accurate.\n\n" +
                                    "2. Aroundly may display your shop details, website link, and map location to users " +
                                    "for discovery and navigation purposes.\n\n" +
                                    "3. You agree not to upload illegal, offensive, or misleading content.\n\n" +
                                    "4. Aroundly is not responsible for any loss, disputes, or payments " +
                                    "between you and your customers.\n\n" +
                                    "5. These terms can be changed in the future; continued use of the manager " +
                                    "account implies acceptance of the latest terms."
                        )
                    }
                }
            )
        }
            }
    }
}

@Composable
private fun StepCircle(
    step: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    val background = when {
        isCompleted -> Color(0xFFFFC857)
        isActive -> Color(0xFFFFC857)
        else -> Color(0xFF3A3029)
    }
    val contentColor = if (isCompleted || isActive) Color.Black else Color.White

    Box(
        modifier = Modifier
            .height(24.dp)
            .width(24.dp)
            .background(color = background, shape = androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = step, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}


