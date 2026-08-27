package com.aroundly.aroundly.uiLibrary.admin

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.ui.theme.PrimaryTeal
import com.aroundly.aroundly.uiLibrary.viewModel.AdminViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.AmberBg
import com.aroundly.aroundly.ui.theme.Charcoal
import com.aroundly.aroundly.ui.theme.DarkNavy
import com.aroundly.aroundly.ui.theme.DeepDark
import com.aroundly.aroundly.ui.theme.RoseBg
import com.aroundly.aroundly.ui.theme.SageBg
import com.aroundly.aroundly.ui.theme.TextMuted
import com.aroundly.aroundly.ui.theme.WarmWhite
import com.aroundly.aroundly.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {

    val pendingSubmissions by viewModel.pendingSubmissions.collectAsState(initial = emptyList())
    val approvedSubmissions by viewModel.approvedSubmissions.collectAsState(initial = emptyList())
    val rejectedSubmissions by viewModel.rejectedSubmissions.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPendingSubmissions()
        viewModel.loadApprovedSubmissions()
        viewModel.loadRejectedSubmissions()
    }

    var activeTab by remember { mutableStateOf(SubmissionTab.PENDING) }

    var itemToDelete by remember { mutableStateOf<PlaceSubmission?>(null) }

    fun approve(item: PlaceSubmission, notes: String) {
        viewModel.approveSubmission(item, notes)
    }

    fun reject(item: PlaceSubmission, notes: String) {
        viewModel.rejectSubmission(item, notes)
    }

    val currentList = when (activeTab) {
        SubmissionTab.PENDING -> pendingSubmissions
        SubmissionTab.APPROVED -> approvedSubmissions
        SubmissionTab.REJECTED -> rejectedSubmissions
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite)
    ) {

        AdminHeader(
            pendingCount = pendingSubmissions.size,
            approvedCount = approvedSubmissions.size,
            rejectedCount = rejectedSubmissions.size,
            onLogout = onLogout
        )

        AdminTabRow(
            activeTab = activeTab,
            pending = pendingSubmissions.size,
            approved = approvedSubmissions.size,
            rejected = rejectedSubmissions.size,
            onTabSelected = { activeTab = it }
        )

        if (isLoading && pendingSubmissions.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryTeal)
            }
        }

        message?.let {
            Text(
                text = it,
                modifier = Modifier.padding(16.dp),
                color = PrimaryTeal
            )
        }

        AnimatedContent(
            targetState = activeTab,
            transitionSpec = {
                fadeIn(tween(220)) togetherWith fadeOut(tween(180))
            },
            label = ""
        ) { tab ->

            if (currentList.isEmpty()) {
                EmptyTabContent(tab)
            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(currentList) { submission ->

                        SubmissionCard(
                            item = submission,
                            tab = tab,
                            onApprove = { notes ->
                                approve(submission, notes)
                            },
                            onReject = { notes ->
                                reject(submission, notes)
                            },
                            onDelete = { itemToDelete = submission }
                        )
                    }
                }
            }
        }

        itemToDelete?.let { item ->
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("Delete ${item.name}?") },
                text = { Text("This will permanently remove the ${item.placeType.name.replace('_', ' ')} and all its images and reviews.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deletePlace(item)
                            itemToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { itemToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AdminHeader(
    pendingCount: Int,
    approvedCount: Int,
    rejectedCount: Int,
    onLogout: () -> Unit
) {
    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF131013),
            Color(0xFF3D3025)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerGradient)
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        "ADMIN CONTROL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )

                    Text(
                        "Aroundly Admin",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = White
                    )
                }

                IconButton(onClick = onLogout) {

                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = White
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                StatCard("⏳", pendingCount.toString(), "Pending", AmberBg, Modifier.weight(1f))
                StatCard("✅", approvedCount.toString(), "Approved", SageBg, Modifier.weight(1f))
                StatCard("❌", rejectedCount.toString(), "Rejected", RoseBg, Modifier.weight(1f))
            }
        }
    }
}

enum class SubmissionTab(val label: String, val icon: String) {
    PENDING("Pending", "⏳"),
    APPROVED("Approved", "✅"),
    REJECTED("Rejected", "❌")
}
@Composable
fun StatCard(
    icon: String,
    value: String,
    label: String,
    bg: Color,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .background(bg, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(icon, fontSize = 20.sp)

        Text(
            value,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Text(
            label,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

@Composable
fun AdminTabRow(
    activeTab: SubmissionTab,
    pending: Int,
    approved: Int,
    rejected: Int,
    onTabSelected: (SubmissionTab) -> Unit
) {

    val counts = mapOf(
        SubmissionTab.PENDING to pending,
        SubmissionTab.APPROVED to approved,
        SubmissionTab.REJECTED to rejected
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        SubmissionTab.entries.forEach { tab ->

            val active = tab == activeTab

            Surface(
                onClick = { onTabSelected(tab) },
                shape = RoundedCornerShape(10.dp),
                color = if (active) Amber else Color.Transparent,
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "${tab.icon} ${tab.label} (${counts[tab]})",
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SubmissionCard(
    item: PlaceSubmission,
    tab: SubmissionTab,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onDelete: () -> Unit
) {

    var notes by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {

        Column(Modifier.padding(16.dp)) {

            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Text("${item.placeType.name} • ${item.city}")

            Text("Owner: ${item.ownerEmail}")

            Spacer(Modifier.height(8.dp))

            if (item.placeImages.isNotEmpty()) {

                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    item.placeImages.forEach {

                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Admin notes") },
                modifier = Modifier.fillMaxWidth()
            )

            if (tab == SubmissionTab.PENDING) {

                Spacer(Modifier.height(10.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = { onReject(notes) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject", color = Color.Red)
                    }

                    Button(
                        onClick = { onApprove(notes) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Approve")
                    }
                }
            }

            if (tab == SubmissionTab.APPROVED) {

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Delete Place")
                }
            }
        }
    }
}

@Composable
fun EmptyTabContent(tab: SubmissionTab) {

    val message = when (tab) {
        SubmissionTab.PENDING -> "No pending submissions"
        SubmissionTab.APPROVED -> "No approved businesses"
        SubmissionTab.REJECTED -> "No rejected businesses"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("📭", fontSize = 50.sp)

        Spacer(Modifier.height(10.dp))

        Text(
            message,
            fontSize = 16.sp,
            color = Charcoal.copy(alpha = 0.4f)
        )
    }
}