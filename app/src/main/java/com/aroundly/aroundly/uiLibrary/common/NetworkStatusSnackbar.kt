package com.aroundly.aroundly.uiLibrary.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aroundly.aroundly.R
import com.aroundly.aroundly.data.network.ConnectionStatus
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.Charcoal
import com.aroundly.aroundly.ui.theme.Rose
import com.aroundly.aroundly.ui.theme.TextOnDark
import com.aroundly.aroundly.uiLibrary.viewModel.NetworkStatusViewModel
import kotlinx.coroutines.flow.collectLatest

data class ConnectionSnackbarVisuals(
    val status: ConnectionStatus,
    override val message: String,
    override val duration: SnackbarDuration =
        if (status == ConnectionStatus.ONLINE) SnackbarDuration.Short else SnackbarDuration.Long,
    override val withDismissAction: Boolean = true,
    override val actionLabel: String? = null
) : SnackbarVisuals

/**
 * Hosts connection status snackbars at the top of the screen, above all navigation content.
 */
@Composable
fun NetworkStatusSnackbarHost(modifier: Modifier = Modifier) {
    val viewModel: NetworkStatusViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { status ->
            // Replace an in-flight snackbar instead of queueing several connection messages.
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                ConnectionSnackbarVisuals(
                    status = status,
                    message = when (status) {
                        ConnectionStatus.LOST ->
                            context.getString(R.string.connection_lost_message)
                        ConnectionStatus.POOR ->
                            context.getString(R.string.poor_connection_message)
                        ConnectionStatus.ONLINE ->
                            context.getString(R.string.back_online_message)
                    }
                )
            )
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) { data ->
        ConnectionSnackbar(data)
    }
}

@Composable
private fun ConnectionSnackbar(data: SnackbarData) {
    val status = (data.visuals as? ConnectionSnackbarVisuals)?.status ?: ConnectionStatus.ONLINE

    val containerColor: Color
    val contentColor: Color
    val icon: ImageVector
    when (status) {
        ConnectionStatus.LOST -> {
            containerColor = Rose
            contentColor = TextOnDark
            icon = Icons.Filled.WifiOff
        }
        ConnectionStatus.POOR -> {
            containerColor = Amber
            contentColor = Charcoal
            icon = Icons.Filled.Warning
        }
        ConnectionStatus.ONLINE -> {
            containerColor = Color(0xFF2E7D32)
            contentColor = TextOnDark
            icon = Icons.Filled.CheckCircle
        }
    }

    // Slide in from above the screen instead of using the default bottom snackbar motion.
    val density = LocalDensity.current
    val offsetY = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        offsetY.snapTo(-with(density) { 120.dp.toPx() })
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Snackbar(
        modifier = Modifier.graphicsLayer { translationY = offsetY.value },
        shape = RoundedCornerShape(16.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        dismissAction = {
            IconButton(onClick = { data.dismiss() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.dismiss)
                )
            }
        },
        dismissActionContentColor = contentColor,
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = data.visuals.message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}
