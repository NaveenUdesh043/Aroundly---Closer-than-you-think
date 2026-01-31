package com.aroundly.aroundly.uiLibrary.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.ui.theme.PrimaryTeal
import com.aroundly.aroundly.uiLibrary.viewModel.AdminViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val pendingSubmissions by viewModel.pendingSubmissions.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPendingSubmissions() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin - Pending Submissions", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryTeal,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            message?.let { msg ->
                Text(msg, color = PrimaryTeal)
                Spacer(Modifier.height(8.dp))
            }

            if (isLoading && pendingSubmissions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else {
                Text(
                    "${pendingSubmissions.size} pending submission(s)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(pendingSubmissions) { submission ->
                        PendingSubmissionCard(
                            submission = submission,
                            onApprove = { notes -> viewModel.approveSubmission(submission, notes) },
                            onReject = { notes -> viewModel.rejectSubmission(submission, notes) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingSubmissionCard(
    submission: PlaceSubmission,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    var adminNotes by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                submission.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("${submission.placeType.name} • ${submission.type} • ${submission.city}")
            submission.description.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
            }
            submission.address.takeIf { it.isNotBlank() }?.let {
                Text("Address: $it", style = MaterialTheme.typography.bodySmall)
            }
            submission.phoneNumber.takeIf { it.isNotBlank() }?.let {
                Text("Phone: $it", style = MaterialTheme.typography.bodySmall)
            }
            Text("Owner: ${submission.ownerEmail}", style = MaterialTheme.typography.bodySmall)

            if (submission.placeImages.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("Place Images:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    submission.placeImages.forEach { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Place image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
            if (submission.receiptImageUrl.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Payment Receipt:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                AsyncImage(
                    model = submission.receiptImageUrl,
                    contentDescription = "Receipt",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = adminNotes,
                onValueChange = { adminNotes = it },
                label = { Text("Admin notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 1
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onApprove(adminNotes) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Approve")
                }
                OutlinedButton(
                    onClick = { onReject(adminNotes) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject", color = Color.Red)
                }
            }
        }
    }
}
