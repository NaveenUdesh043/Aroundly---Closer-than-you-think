package com.aroundly.aroundly.uiLibrary.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.aroundly.aroundly.domain.model.UserRole
import com.aroundly.aroundly.ui.theme.PrimaryTeal
import com.aroundly.aroundly.uiLibrary.viewModel.RoleRouterViewModel

@Composable
fun RoleRouterScreen(
    viewModel: RoleRouterViewModel = hiltViewModel(),
    onNavigateToUserDashboard: () -> Unit,
    onNavigateToManagerDashboard: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val role by viewModel.userRole.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState(initial = true)

    LaunchedEffect(role, isLoading) {
        if (!isLoading) {
            when (role) {
                UserRole.USER -> onNavigateToUserDashboard()
                UserRole.MANAGER -> onNavigateToManagerDashboard()
                UserRole.ADMIN -> onNavigateToAdminDashboard()
                null -> onNavigateToLogin()
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryTeal)
        }
    }
}
