package com.aroundly.aroundly.uiLibrary.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.hilt.navigation.compose.hiltViewModel
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aroundly.aroundly.domain.model.UserRole
import com.aroundly.aroundly.ui.theme.AccentPurple
import com.aroundly.aroundly.ui.theme.BgDark
import com.aroundly.aroundly.ui.theme.SurfaceInput
import com.aroundly.aroundly.ui.theme.SurfaceWarm
import com.aroundly.aroundly.ui.theme.TextDisabled
import com.aroundly.aroundly.ui.theme.TextMuted
import com.aroundly.aroundly.ui.theme.TextOnDarkMuted
import com.aroundly.aroundly.ui.theme.TextPrimary
import com.aroundly.aroundly.ui.theme.TextSecondary
import com.aroundly.aroundly.uiLibrary.viewModel.AuthViewModel

@Composable
fun ManagerSignupScreen(
    viewModel: AuthViewModel = hiltViewModel(),
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
    val backgroundColor = SurfaceWarm

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                bottomStart = 32.dp,
                bottomEnd = 32.dp
            ),
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
                        color = TextOnDarkMuted
                    )

                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
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
                    text = "Manager Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Create manager's profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "FIRST NAME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = "$firstName".trim(),
                    onValueChange = { value ->
                        val parts = value.split(" ", limit = 1)
                        firstName = parts.getOrNull(0) ?: ""
                    },
                    placeholder = { Text("John", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput,
                        cursorColor = TextPrimary
                    ),
                    singleLine = true,
                    textStyle = TextStyle(color = TextPrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "LAST NAME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = "$lastName".trim(),
                    onValueChange = { value ->
                        val parts = value.split(" ", limit = 1)
                        lastName = parts.getOrNull(0) ?: ""
                    },
                    placeholder = { Text("Smith", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput,
                        cursorColor = TextPrimary
                    ),
                    singleLine = true,
                    textStyle = TextStyle(color = TextPrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "BUSINESS NAME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    placeholder = { Text("Business name", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput,
                        cursorColor = TextPrimary
                    ),
                    singleLine = true,
                    textStyle = TextStyle(color = TextPrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "EMAIL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("business@email.com", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput,
                        cursorColor = TextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    textStyle = TextStyle(color = TextPrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PHONE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    placeholder = { Text("+94 7X XXX XXXX", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput,
                        cursorColor = TextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    textStyle = TextStyle(color = TextPrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PASSWORD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Min. 8 characters", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput,
                        cursorColor = TextPrimary
                    ),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF3D3025)
                            )
                        }
                    },
                    textStyle = TextStyle(color = TextPrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CONFIRM PASSWORD",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Repeat your password", color = TextDisabled) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput,
                        cursorColor = TextPrimary
                    ),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF3D3025)
                            )
                        }
                    },
                    textStyle = TextStyle(color = TextPrimary),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)

                ) {
                    Text(
                        text = "I have read and accept the Terms and Conditions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurfaceWarm
                    )

                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = { showTermsDialog = true },
            ) {
                Text(
                    text = "View Terms and Conditions",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = SurfaceWarm
                )
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
                .background(
                    color = Color(0xFFFFB02C),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ),
            enabled = !isLoading && termsAccepted,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Gray.copy(alpha = 0.1f)
            ),
            elevation = null
        ) {
            Text(
                text = if (isLoading) "Creating account..." else "Continue  →",
                color = SurfaceWarm,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? ",
                color = SurfaceWarm
            )
            Text(
                text = "Sign in",
                color = Color(0xFFFFB02C),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onNavigateToLogin)
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
                            "Effective Date: 01/03/2030 \n\n\n" +
                                    "Welcome to Aroundly,\n\n A mobile application that helps users discover nearby cafés, restaurants, and dining experiences. \n\n" +
                                    "By downloading, accessing, or using the Aroundly application, you agree to comply with and be bound by the following Terms and Conditions. " +
                                    "If you do not agree with these terms, please do not use the application.\n\n" +
                                    "1. About Aroundly \n" +
                                    "Aroundly is a restaurant and café discovery platform that allows: \n\n" +
                                    "• Users to explore nearby restaurants and cafés \n\n" +
                                    "• View restaurant information and images \n\n" +
                                    "• Rate and review places \n\n" +
                                    "• Business owners to promote their restaurants through listings \n\n" +
                                    "The application is intended to connect customers with local dining businesses. \n" +
                                    "2. Free Access for Users \n\n" +
                                    "The Aroundly mobile application is free for general users. \n" +
                                    "Users may: \n\n" +
                                    "• Search for restaurants \n" +
                                    "• Discover nearby cafés \n" +
                                    "• View restaurant details \n" +
                                    "• Read and submit reviews \n" +
                                    "Aroundly does not charge normal users to use the basic features of the application. \n" +
                                    "3. Business Owner / Restaurant Listings \n\n" +
                                    "Restaurant owners, managers, or businesses may register their restaurants on Aroundly to \n" +
                                    "promote their services.\n\n" +
                                    "Business owners may be required to pay a listing or promotional fee in order to: \n\n" +
                                    "• Publish their restaurant on the platform \n" +
                                    "• Display restaurant details and images \n" +
                                    "• Increase visibility to users \n" +
                                    "Business owners are responsible for providing accurate and truthful information about their \n" +
                                    "restaurant. \n\n" +
                                    "4. Payment Policy for Business Owners \n\n" +
                                    "Payments made by restaurant owners or managers are for listing, promotional, or advertising \n" +
                                    "services within the Aroundly platform. \n\n" +
                                    "Before making any payment, the business owner must carefully review:\n" +
                                    "• Listing details \n" +
                                    "• Pricing \n" +
                                    "• Service duration \n" +
                                    "By completing the payment, the business owner confirms acceptance of these terms. \n\n" +
                                    "5. No Refund Policy \n\n" +
                                    "All payments made for restaurant listings, promotions, or advertising services on Aroundly are \n" +
                                    "f\n" +
                                    "inal and non-refundable. \n" +
                                    "Once a payment has been successfully processed: \n\n" +
                                    "• Refund requests will not be accepted \n" +
                                    "• Payment cancellations will not be processed \n" +
                                    "• The service will continue for the agreed duration \n" +
                                    "Business owners must ensure they fully understand the service before making payment. \n\n" +
                                    "6. User Reviews and Ratings \n\n" +
                                    "Users may leave ratings and reviews for restaurants. \n" +
                                    "However, users must not: \n" +
                                    "• Post false information \n" +
                                    "• Post offensive or abusive content \n" +
                                    "• Damage the reputation of businesses intentionally \n" +
                                    "Aroundly reserves the right to remove inappropriate reviews. \n\n" +
                                    "7. Content Accuracy \n\n" +
                                    "While Aroundly tries to maintain accurate information, we do not guarantee that: \n" +
                                    "• Restaurant details \n" +
                                    "• Opening hours \n" +
                                    "• Menu information \n" +
                                    "• Prices \n" +
                                    "are always fully accurate. \n" +
                                    "Users are encouraged to verify details directly with the restaurant. \n\n" +
                                    "8. Account Suspension \n\n" +
                                    "Aroundly reserves the right to suspend or terminate accounts if users or business owners: \n" +
                                    "• Provide false information \n" +
                                    "• Misuse the platform \n" +
                                    "• Violate these Terms & Conditions \n" +
                                    "• Attempt fraud or abuse payment systems \n\n" +
                                    "9. Limitation of Liability \n\n" +
                                    "Aroundly is a platform that connects users with restaurants. \n" +
                                    "We are not responsible for: \n" +
                                    "• Food quality \n" +
                                    "• Restaurant services \n" +
                                    "• Pricing disputes \n" +
                                    "• Customer experience at restaurants \n" +
                                    "Any issues must be resolved directly with the restaurant. \n\n" +
                                    "10. Changes to Terms \n\n" +
                                    "Aroundly may update these Terms & Conditions at any time. \n" +
                                    "Updates may be communicated through: \n" +
                                    "• App notifications \n" +
                                    "• Website updates \n" +
                                    "• Application updates \n" +
                                    "Continued use of the app means acceptance of the updated terms. \n\n" +
                                    "11. Contact Information \n\n" +
                                    "If you have any questions regarding these Terms & Conditions, please contact: \n\n" +
                                    "Application Name: Aroundly \n\n" +
                                    "Email: naveenudesh043@gmail.com"
                        )
                    }
                }
            )
        }
    }
}
