package com.aroundly.aroundly.uiLibrary.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aroundly.aroundly.R
import com.aroundly.aroundly.uiLibrary.common.DottedProgressBar
import com.aroundly.aroundly.uiLibrary.viewModel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.aroundly.aroundly.ui.theme.AccentPurple
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.BgDark
import com.aroundly.aroundly.ui.theme.SurfaceDivider
import com.aroundly.aroundly.ui.theme.SurfaceInput
import com.aroundly.aroundly.ui.theme.SurfaceWarm
import com.aroundly.aroundly.ui.theme.TextLink
import com.aroundly.aroundly.ui.theme.TextMuted
import com.aroundly.aroundly.ui.theme.TextOnDarkMuted
import com.aroundly.aroundly.ui.theme.TextPrimary
import com.aroundly.aroundly.ui.theme.TextSecondary


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToManagerSignup: () -> Unit = {},
    onAdminLoginSuccess: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val viewModel = remember { AuthViewModel() }
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val googleSignInClient = remember { GoogleSignInHelper.getClient(context) }
    val googleAuthLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val googleAccount = account.getResult(ApiException::class.java)
            val idToken = googleAccount.idToken
            if (idToken != null) {
                isLoading = true
                error = ""
                viewModel.signInWithGoogle(idToken) { success, message ->
                    isLoading = false
                    if (success) onLoginSuccess() else error = message
                }
            } else {
                error = "Google sign-in failed"
            }
        } catch (e: ApiException) {
            if (e.statusCode != GoogleSignInStatusCodes.SIGN_IN_CANCELLED &&
                e.statusCode != CommonStatusCodes.CANCELED
            ) {
                error = "Google sign-in failed"
            }
        }
    }

    val backgroundColor = SurfaceWarm
    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF131013),
            Color(0xFF3D3025)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(headerGradient),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Welcome to Aroundly",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Closer than you think",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = TextOnDarkMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
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
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("you@example.com", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceInput,
                        unfocusedContainerColor = SurfaceInput,
                        cursorColor = TextPrimary
                    ),
                    singleLine = true,
                    textStyle = TextStyle(color = TextPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••••", color = TextMuted) },
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
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forgot password?",
                        color =  Color(0xFF3D3025),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Login Button with Loading Spinner
                Button(
                    onClick = {
                        if (email.trim().equals(
                                "admin@gmail.com",
                                ignoreCase = true
                            ) && password == "Admin@043"
                        ) {
                            onAdminLoginSuccess()
                            return@Button
                        }
                        isLoading = true
                        error = ""
                        viewModel.login(email, password) { success, errorMessage ->
                            isLoading = false
                            if (success) onLoginSuccess()
                            else error = errorMessage
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
                    if (isLoading) {
                        DottedProgressBar()
                    } else {
                        Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = Color.Red)
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = SurfaceDivider,
                        thickness = 1.dp
                    )
                    Text(
                        text = "  or continue with  ",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = SurfaceDivider,
                        thickness = 1.dp
                    )
                }

                Spacer(Modifier.height(16.dp))

                SocialButton(
                    label = "Google",
                    leadingText = "G",
                    onClick = {
                        error = ""
                        googleAuthLauncher.launch(googleSignInClient.signInIntent)
                    }
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = TextSecondary
                    )
                    Text(
                        text = "Create one",
                        color = Amber,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onNavigateToSignup)
                    )
                }
            }
        }
    }
}

