package com.aroundly.aroundly.uiLibrary.common

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.ui.theme.PrimaryTealDark
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailsScreen(
    restaurant: Restaurant,
    onBack: () -> Unit
) {
    PlaceDetailsScaffold(
        title = restaurant.name,
        imageUrls = restaurant.images ?: emptyList(),
        onBack = onBack
    ) {
        StatRow()
        Spacer(Modifier.height(16.dp))

        TagChipsRow(
            tags = buildList {
                add(restaurant.type.ifBlank { "Restaurant" })
                add("Cozy")
                add("Free WiFi")
                add("Outdoor")
            }
        )

        Spacer(Modifier.height(20.dp))

        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            text = restaurant.description.takeUnless { it.isNullOrBlank() }
                ?: "Discover tasty meals and a warm atmosphere at this restaurant.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))
        OpeningHoursCard()

        Spacer(Modifier.height(20.dp))
        ReviewsSection()

        Spacer(Modifier.height(24.dp))

        BottomActionsRow(
            phone = restaurant.phoneNumber,
            address = restaurant.address
        )

        restaurant.websiteUrl?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            WebsiteRow(websiteUrl = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    hotel: Hotel,
    onBack: () -> Unit
) {
    PlaceDetailsScaffold(
        title = hotel.name,
        imageUrls = hotel.images ?: emptyList(),
        onBack = onBack
    ) {
        StatRow()
        Spacer(Modifier.height(16.dp))

        TagChipsRow(
            tags = listOf("Hotel", hotel.type.ifBlank { "Comfort" }, "Family friendly")
        )

        Spacer(Modifier.height(20.dp))

        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            text = hotel.description.takeUnless { it.isNullOrBlank() }
                ?: "Stay comfortably with modern rooms and great service.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))
        OpeningHoursCard(title = "Check-in / Check-out")

        Spacer(Modifier.height(20.dp))
        ReviewsSection()

        Spacer(Modifier.height(24.dp))

        BottomActionsRow(
            phone = hotel.phoneNumber,
            address = hotel.address
        )

        hotel.websiteUrl?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            WebsiteRow(websiteUrl = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceToVisitDetailsScreen(
    place: PlaceToVisit,
    onBack: () -> Unit
) {
    PlaceDetailsScaffold(
        title = place.name,
        imageUrls = place.images ?: emptyList(),
        onBack = onBack
    ) {
        StatRow()
        Spacer(Modifier.height(16.dp))

        TagChipsRow(
            tags = listOf("Attraction", "Outdoor")
        )

        Spacer(Modifier.height(20.dp))

        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            text = place.description.takeUnless { it.isNullOrBlank() }
                ?: "A great place to experience the local atmosphere.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))
        OpeningHoursCard()

        Spacer(Modifier.height(20.dp))
        ReviewsSection()

        Spacer(Modifier.height(24.dp))

        BottomActionsRow(
            phone = null,
            address = place.address
        )

        place.websiteUrl?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            WebsiteRow(websiteUrl = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentShopDetailsScreen(
    shop: EquipmentShop,
    onBack: () -> Unit
) {
    PlaceDetailsScaffold(
        title = shop.name,
        imageUrls = shop.images ?: emptyList(),
        onBack = onBack
    ) {
        StatRow()
        Spacer(Modifier.height(16.dp))

        TagChipsRow(
            tags = listOf("Rental", shop.type.ifBlank { "Equipment" })
        )

        Spacer(Modifier.height(20.dp))

        Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            text = shop.description.takeUnless { it.isNullOrBlank() }
                ?: "Find the equipment you need for your next adventure.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(20.dp))
        OpeningHoursCard()

        Spacer(Modifier.height(20.dp))
        ReviewsSection()

        Spacer(Modifier.height(24.dp))

        BottomActionsRow(
            phone = shop.phoneNumber,
            address = shop.address
        )

        shop.websiteUrl?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(12.dp))
            WebsiteRow(websiteUrl = it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceDetailsScaffold(
    title: String,
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
                        .matchParentSize()
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

@Composable
private fun AddressRow(address: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                val uri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color(0xFF1C1C84),
            modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(8.dp))
        Text(address, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StatRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(title = "Rating", value = "4.8")
        StatCard(title = "Distance", value = "0.2 km")
        StatCard(title = "Walk", value = "~12 min")
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .height(72.dp),
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
private fun OpeningHoursCard(title: String = "Opening Hours") {
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
                    Text("7:00 AM – 10:00 PM", style = MaterialTheme.typography.bodySmall)
                    Text("8:00 AM – 11:00 PM", style = MaterialTheme.typography.bodySmall)
                    Text("9:00 AM – 9:00 PM", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun ReviewsSection() {
    Column {
        Text("Reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        ReviewCard(
            name = "Sarah M.",
            text = "Best spot nearby. The atmosphere is perfect for relaxing.",
            rating = "5.0"
        )
        Spacer(Modifier.height(8.dp))
        ReviewCard(
            name = "Raj P.",
            text = "Great place. Friendly staff and good vibes.",
            rating = "4.8"
        )
    }
}

@Composable
private fun ReviewCard(name: String, text: String, rating: String) {
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
                Text(name, fontWeight = FontWeight.SemiBold)
                Text("$rating ★", color = Color(0xFFFFA000), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(6.dp))
            Text(text, style = MaterialTheme.typography.bodySmall, color = Color(0xFF5D4037))
        }
    }
}

@Composable
private fun BottomActionsRow(
    phone: String?,
    address: String?
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth(),
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
                address?.let {
                    val uri = Uri.parse("geo:0,0?q=${Uri.encode(it)}")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                }
            },
            enabled = address != null,
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

@Composable
private fun PhoneRow(phone: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF1C1C84), modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(8.dp))
        Text(phone, style = MaterialTheme.typography.bodyMedium)
    }
}
