package com.aroundly.aroundly.uiLibrary.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.OpeningHoursDay
import com.aroundly.aroundly.domain.model.PlaceReview
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.ui.theme.PrimaryTealDark
import com.aroundly.aroundly.uiLibrary.viewModel.EquipmentDetailsViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.HotelDetailsViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.PlaceDetailsViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.RestaurantDetailsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailsScreen(
    viewModel: RestaurantDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val restaurant by viewModel.restaurant.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()
    val distanceKm by viewModel.distanceKm.collectAsState()
    val reviewRating by viewModel.reviewRating.collectAsState()
    val reviewComment by viewModel.reviewComment.collectAsState()
    val reviewMessage by viewModel.reviewMessage.collectAsState()
    val submittingReview by viewModel.submittingReview.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDistance()
    }

    if (restaurant == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val r = restaurant!!

    PlaceDetailsScaffold(
        title = r.name,
        subtitle = r.address,
        imageUrls = r.images ?: emptyList(),
        onBack = onBack
    ) {
        StatRow(averageRating = averageRating, distanceKm = distanceKm)
        Spacer(Modifier.height(16.dp))

        val tags = if (r.tags.isNotEmpty()) r.tags else listOfNotNull(r.type.takeIf { it.isNotBlank() })
        TagChipsRow(tags = if (tags.isEmpty()) listOf("Place") else tags)

        Spacer(Modifier.height(20.dp))

        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            text = r.description.takeUnless { it.isNullOrBlank() }
                ?: "Discover this place on Aroundly.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))
        OpeningHoursCard(
            schedule = r.openingHoursSchedule,
            monFri = r.openingHoursMonFri,
            saturday = r.openingHoursSaturday,
            sunday = r.openingHoursSunday
        )

        Spacer(Modifier.height(20.dp))
        AddReviewSection(
            rating = reviewRating,
            onRatingChange = viewModel::setReviewRating,
            comment = reviewComment,
            onCommentChange = viewModel::setReviewComment,
            onSubmit = viewModel::submitReview,
            submitting = submittingReview,
            message = reviewMessage
        )

        Spacer(Modifier.height(12.dp))
        ReviewsList(reviews = reviews)

        Spacer(Modifier.height(24.dp))

        BottomActionsRow(
            phone = r.phoneNumber,
            latitude = r.latitude,
            longitude = r.longitude,
            address = r.address
        )

        r.websiteUrl?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            WebsiteRow(websiteUrl = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    viewModel: HotelDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val hotel by viewModel.hotel.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()
    val distanceKm by viewModel.distanceKm.collectAsState()
    val reviewRating by viewModel.reviewRating.collectAsState()
    val reviewComment by viewModel.reviewComment.collectAsState()
    val reviewMessage by viewModel.reviewMessage.collectAsState()
    val submittingReview by viewModel.submittingReview.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDistance()
    }

    if (hotel == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val h = hotel!!

    PlaceDetailsScaffold(
        title = h.name,
        subtitle = h.address,
        imageUrls = h.images ?: emptyList(),
        onBack = onBack
    ) {
        StatRow(averageRating = averageRating, distanceKm = distanceKm)
        Spacer(Modifier.height(16.dp))
        val tags = if (h.tags.isNotEmpty()) h.tags else listOfNotNull("Hotel", h.type.takeIf { it.isNotBlank() })
        TagChipsRow(tags = tags)

        Spacer(Modifier.height(20.dp))
        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            text = h.description.takeUnless { it.isNullOrBlank() }
                ?: "Stay comfortably with great service.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))
        OpeningHoursCard(
            title = "Opening hours",
            schedule = h.openingHoursSchedule,
            monFri = h.openingHoursMonFri,
            saturday = h.openingHoursSaturday,
            sunday = h.openingHoursSunday
        )

        Spacer(Modifier.height(20.dp))
        AddReviewSection(
            rating = reviewRating,
            onRatingChange = viewModel::setReviewRating,
            comment = reviewComment,
            onCommentChange = viewModel::setReviewComment,
            onSubmit = viewModel::submitReview,
            submitting = submittingReview,
            message = reviewMessage
        )
        Spacer(Modifier.height(12.dp))
        ReviewsList(reviews = reviews)

        Spacer(Modifier.height(24.dp))
        BottomActionsRow(
            phone = h.phoneNumber,
            latitude = h.latitude,
            longitude = h.longitude,
            address = h.address
        )
        h.websiteUrl?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            WebsiteRow(websiteUrl = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceToVisitDetailsScreen(
    viewModel: PlaceDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val place by viewModel.place.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()
    val distanceKm by viewModel.distanceKm.collectAsState()
    val reviewRating by viewModel.reviewRating.collectAsState()
    val reviewComment by viewModel.reviewComment.collectAsState()
    val reviewMessage by viewModel.reviewMessage.collectAsState()
    val submittingReview by viewModel.submittingReview.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDistance()
    }

    if (place == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val p = place!!

    PlaceDetailsScaffold(
        title = p.name,
        subtitle = p.address,
        imageUrls = p.images ?: emptyList(),
        onBack = onBack
    ) {
        StatRow(averageRating = averageRating, distanceKm = distanceKm)
        Spacer(Modifier.height(16.dp))
        val tags = if (p.tags.isNotEmpty()) p.tags else listOf("Attraction")
        TagChipsRow(tags = tags)

        Spacer(Modifier.height(20.dp))
        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            text = p.description.takeUnless { it.isNullOrBlank() }
                ?: "A place to visit near you.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))
        OpeningHoursCard(
            schedule = p.openingHoursSchedule,
            monFri = p.openingHoursMonFri,
            saturday = p.openingHoursSaturday,
            sunday = p.openingHoursSunday
        )

        Spacer(Modifier.height(20.dp))
        AddReviewSection(
            rating = reviewRating,
            onRatingChange = viewModel::setReviewRating,
            comment = reviewComment,
            onCommentChange = viewModel::setReviewComment,
            onSubmit = viewModel::submitReview,
            submitting = submittingReview,
            message = reviewMessage
        )
        Spacer(Modifier.height(12.dp))
        ReviewsList(reviews = reviews)

        Spacer(Modifier.height(24.dp))
        BottomActionsRow(
            phone = null,
            latitude = p.latitude,
            longitude = p.longitude,
            address = p.address
        )
        p.websiteUrl?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            WebsiteRow(websiteUrl = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentShopDetailsScreen(
    viewModel: EquipmentDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val shop by viewModel.equipment.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val averageRating by viewModel.averageRating.collectAsState()
    val distanceKm by viewModel.distanceKm.collectAsState()
    val reviewRating by viewModel.reviewRating.collectAsState()
    val reviewComment by viewModel.reviewComment.collectAsState()
    val reviewMessage by viewModel.reviewMessage.collectAsState()
    val submittingReview by viewModel.submittingReview.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDistance()
    }

    if (shop == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val e = shop!!

    PlaceDetailsScaffold(
        title = e.name,
        subtitle = e.address,
        imageUrls = e.images ?: emptyList(),
        onBack = onBack
    ) {
        StatRow(averageRating = averageRating, distanceKm = distanceKm)
        Spacer(Modifier.height(16.dp))
        val tags = if (e.tags.isNotEmpty()) e.tags else listOfNotNull("Rental", e.type.takeIf { it.isNotBlank() })
        TagChipsRow(tags = if (tags.isEmpty()) listOf("Shop") else tags)

        Spacer(Modifier.height(20.dp))
        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            text = e.description.takeUnless { it.isNullOrBlank() }
                ?: "Equipment and rentals for your trip.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))
        OpeningHoursCard(
            schedule = e.openingHoursSchedule,
            monFri = e.openingHoursMonFri,
            saturday = e.openingHoursSaturday,
            sunday = e.openingHoursSunday
        )

        Spacer(Modifier.height(20.dp))
        AddReviewSection(
            rating = reviewRating,
            onRatingChange = viewModel::setReviewRating,
            comment = reviewComment,
            onCommentChange = viewModel::setReviewComment,
            onSubmit = viewModel::submitReview,
            submitting = submittingReview,
            message = reviewMessage
        )
        Spacer(Modifier.height(12.dp))
        ReviewsList(reviews = reviews)

        Spacer(Modifier.height(24.dp))
        BottomActionsRow(
            phone = e.phoneNumber,
            latitude = e.latitude,
            longitude = e.longitude,
            address = e.address
        )
        e.websiteUrl?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            WebsiteRow(websiteUrl = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceDetailsScaffold(
    title: String,
    subtitle: String?,
    imageUrls: List<String>,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFF3E0),
                                Color(0xFFFFE0B2)
                            )
                        )
                    )
            ) {
                if (imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrls.first(),
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x99000000)
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF5D4037)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    subtitle?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "📍 $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                content()
            }
        }
    }
}

private fun formatRating(average: Float): String =
    if (average > 0f) String.format("%.1f", average) else "—"

private fun formatDistanceKm(km: Double?): String =
    when {
        km == null -> "—"
        km < 1 -> "${(km * 1000).roundToInt()} m"
        else -> String.format("%.1f km", km)
    }

private fun formatWalkMinutes(km: Double?): String {
    if (km == null) return "—"
    val minutes = ((km / 5.0) * 60).roundToInt().coerceAtLeast(1)
    return "~${minutes} min"
}

@Composable
private fun StatRow(averageRating: Float, distanceKm: Double?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCardRating(
            modifier = Modifier.weight(1f),
            averageRating = averageRating
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Distance",
            value = formatDistanceKm(distanceKm)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Walk",
            value = formatWalkMinutes(distanceKm)
        )
    }
}

@Composable
private fun StatCardRating(modifier: Modifier = Modifier, averageRating: Float) {
    Card(
        modifier = modifier.heightIn(min = 80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Rating",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF8D6E63)
            )
            RatingStarsRow(
                rating = averageRating.coerceIn(0f, 5f),
                starSize = 13.sp
            )
            Text(
                text = formatRating(averageRating),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )
        }
    }
}

@Composable
private fun RatingStarsRow(
    rating: Float,
    starSize: TextUnit,
    onPickStar: ((Int) -> Unit)? = null
) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        for (star in 1..5) {
            val filled = rating >= star - 0.25f
            Text(
                text = if (filled) "★" else "☆",
                color = Color(0xFFFFA000),
                fontSize = starSize,
                modifier = if (onPickStar != null) {
                    Modifier.clickable { onPickStar(star) }
                } else {
                    Modifier
                }
            )
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, title: String, value: String) {
    Card(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF8D6E63)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )
        }
    }
}

@Composable
private fun TagChipsRow(tags: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFF3E5F5),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6A1B9A)
                )
            }
        }
    }
}

@Composable
private fun OpeningHoursCard(
    title: String = "Opening hours",
    schedule: List<OpeningHoursDay> = emptyList(),
    monFri: String?,
    saturday: String?,
    sunday: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            if (schedule.isNotEmpty()) {
                schedule.forEach { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            day.dayLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = when {
                                day.isClosed -> "Closed"
                                day.closeTime.isNotBlank() ->
                                    "${day.openTime} – ${day.closeTime}".trim()
                                day.openTime.isNotBlank() -> day.openTime
                                else -> "—"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Mon – Fri", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("Saturday", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("Sunday", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            monFri?.takeIf { it.isNotBlank() } ?: "—",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            saturday?.takeIf { it.isNotBlank() } ?: "—",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            sunday?.takeIf { it.isNotBlank() } ?: "—",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddReviewSection(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    submitting: Boolean,
    message: String?
) {
    Column {
        Text("Your review", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Rating", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        RatingStarsRow(
            rating = rating,
            starSize = 28.sp,
            onPickStar = { onRatingChange(it.toFloat()) }
        )
        Spacer(Modifier.height(4.dp))
        Text("${"%.1f".format(rating)} / 5", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            label = { Text("Comment") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        message?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, color = if (it.contains("fail", ignoreCase = true)) Color.Red else Color(0xFF5D4037), style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onSubmit,
            enabled = !submitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (submitting) "Posting…" else "Post review")
        }
    }
}

@Composable
private fun ReviewsList(reviews: List<PlaceReview>) {
    Column {
        Text("Reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (reviews.isEmpty()) {
            Text("No reviews yet. Be the first!", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        } else {
            reviews.forEach { rev ->
                ReviewCard(review = rev)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ReviewCard(review: PlaceReview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(review.userName, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    RatingStarsRow(
                        rating = review.rating.coerceIn(0f, 5f),
                        starSize = 12.sp
                    )
                    Text(
                        "${"%.1f".format(review.rating)}",
                        color = Color(0xFFFFA000),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(review.comment, style = MaterialTheme.typography.bodySmall, color = Color(0xFF5D4037))
        }
    }
}

private fun openInGoogleMaps(context: Context, latitude: Double?, longitude: Double?, address: String?) {
    when {
        latitude != null && longitude != null -> {
            val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            try {
                context.startActivity(mapIntent)
            } catch (_: Exception) {
                val web = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
                context.startActivity(Intent(Intent.ACTION_VIEW, web))
            }
        }
        !address.isNullOrBlank() -> {
            val web = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}")
            context.startActivity(Intent(Intent.ACTION_VIEW, web))
        }
    }
}

@Composable
private fun BottomActionsRow(
    phone: String?,
    latitude: Double?,
    longitude: Double?,
    address: String?
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                phone?.let {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$it"))
                    context.startActivity(intent)
                }
            },
            enabled = phone != null,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF5D4037)
            )
        ) {
            Text("Call")
        }

        Button(
            onClick = {
                openInGoogleMaps(context, latitude, longitude, address)
            },
            enabled = latitude != null && longitude != null || !address.isNullOrBlank(),
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC107),
                contentColor = Color(0xFF3E2723)
            )
        ) {
            Text("Directions")
        }
    }
}

@Composable
private fun WebsiteRow(websiteUrl: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                val safeUrl = if (websiteUrl.startsWith("http://") || websiteUrl.startsWith("https://")) {
                    websiteUrl
                } else {
                    "https://$websiteUrl"
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(safeUrl))
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Public,
            contentDescription = null,
            tint = Color(0xFF1C1C84),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(websiteUrl, style = MaterialTheme.typography.bodyMedium, color = PrimaryTealDark)
    }
}
