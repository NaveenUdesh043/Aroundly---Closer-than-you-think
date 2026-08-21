package com.aroundly.aroundly.uiLibrary.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aroundly.aroundly.domain.model.Review
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.SurfaceDivider
import com.aroundly.aroundly.ui.theme.TextMuted
import com.aroundly.aroundly.ui.theme.TextPrimary
import com.aroundly.aroundly.ui.theme.TextSecondary
import com.aroundly.aroundly.uiLibrary.viewModel.ReviewViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Star rating display ──
@Composable
fun StarRatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Int = 14,
    showValue: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating) Amber else SurfaceDivider,
                modifier = Modifier.size(starSize.dp)
            )
        }
        if (showValue && rating > 0) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = String.format(Locale.US, "%.1f", rating),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

// ── Star rating input ──
@Composable
fun StarRatingInput(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Int = 28
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Rate $i star${if (i > 1) "s" else ""}",
                tint = if (i <= rating) Amber else SurfaceDivider,
                modifier = Modifier
                    .size(starSize.dp)
                    .clickable { onRatingChange(i.toFloat()) }
                    .padding(1.dp)
            )
        }
    }
}

// ── Single review card ──
@Composable
fun ReviewCard(
    review: Review,
    currentUserId: String?,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(review.timestamp) {
        SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(review.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.userName,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StarRatingBar(
                        rating = review.rating,
                        starSize = 12,
                        showValue = true
                    )
                    if (currentUserId == review.userId) {
                        Spacer(Modifier.width(4.dp))
                        IconButton(
                            onClick = { onDelete(review.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete review",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            if (review.text.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = review.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ── Review input card ──
@Composable
fun ReviewInputCard(
    onSubmit: (Float, String) -> Unit,
    isSubmitting: Boolean,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableFloatStateOf(0f) }
    var text by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Write a Review",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))

            StarRatingInput(
                rating = rating,
                onRatingChange = {
                    rating = it
                    showError = false
                }
            )

            if (showError) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Please select a star rating",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE53935)
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Share your experience...", color = TextMuted)
                },
                minLines = 2,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Amber,
                    unfocusedBorderColor = SurfaceDivider,
                    cursorColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (rating < 1f) {
                        showError = true
                    } else {
                        onSubmit(rating, text)
                        rating = 0f
                        text = ""
                    }
                })
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (rating < 1f) {
                        showError = true
                    } else {
                        onSubmit(rating, text)
                        rating = 0f
                        text = ""
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Amber,
                    contentColor = Color(0xFF1C1C1C)
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF1C1C1C)
                    )
                } else {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Review", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Full reviews section ──
@Composable
fun ReviewsSection(
    viewModel: ReviewViewModel,
    modifier: Modifier = Modifier
) {
    val reviews by viewModel.reviews.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()
    val reviewCount by viewModel.reviewCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val submitResult by viewModel.submitResult.collectAsState()
    val userReview by viewModel.userReview.collectAsState()

    val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

    Column(modifier = modifier) {
        // Header with average rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (reviewCount > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StarRatingBar(rating = averageRating, starSize = 16, showValue = true)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "($reviewCount)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Submit result feedback
        submitResult?.let { success ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (success) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = if (success) "Review submitted!" else "Failed to submit review",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (success) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
            viewModel.clearSubmitResult()
        }

        // Review input (only if user hasn't reviewed yet)
        if (userReview == null) {
            ReviewInputCard(
                onSubmit = { rating, text -> viewModel.submitReview(rating, text) },
                isSubmitting = isLoading
            )
            Spacer(Modifier.height(16.dp))
        }

        // Reviews list
        if (isLoading && reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Amber, modifier = Modifier.size(24.dp))
            }
        } else if (reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No reviews yet. Be the first to review!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            reviews.forEach { review ->
                ReviewCard(
                    review = review,
                    currentUserId = currentUserId,
                    onDelete = { viewModel.deleteReview(it) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
