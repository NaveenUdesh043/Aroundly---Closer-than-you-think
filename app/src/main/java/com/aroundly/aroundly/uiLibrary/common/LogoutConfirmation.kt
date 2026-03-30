package com.aroundly.aroundly.uiLibrary.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun LogoutToolbarIcon(
    onLogoutConfirmed: () -> Unit,
    contentDescription: String = "Logout",
    icon: ImageVector = Icons.AutoMirrored.Filled.Logout,
    tint: Color = Color.Unspecified
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onLogoutConfirmed()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
    IconButton(onClick = { showDialog = true }) {
        Icon(icon, contentDescription = contentDescription, tint = tint)
    }
}

/** Admin / alternate logout using exit icon. */
@Composable
fun LogoutExitToolbarIcon(
    onLogoutConfirmed: () -> Unit,
    tint: Color = Color.Unspecified
) {
    LogoutToolbarIcon(
        onLogoutConfirmed = onLogoutConfirmed,
        contentDescription = "Logout",
        icon = Icons.AutoMirrored.Filled.ExitToApp,
        tint = tint
    )
}
