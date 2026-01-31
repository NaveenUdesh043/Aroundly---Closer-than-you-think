package com.aroundly.aroundly.uiLibrary.common

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.aroundly.aroundly.ui.theme.PrimaryTeal
import com.aroundly.aroundly.ui.theme.PrimaryTealDark

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
        Text(restaurant.type, style = MaterialTheme.typography.labelLarge, color = PrimaryTeal)
        Spacer(Modifier.height(8.dp))
        restaurant.description?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
        Spacer(Modifier.height(16.dp))
        restaurant.address?.let { AddressRow(address = it) }
        restaurant.phoneNumber?.let { PhoneRow(phone = it) }
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
        Text(hotel.type, style = MaterialTheme.typography.labelLarge, color = PrimaryTeal)
        Spacer(Modifier.height(8.dp))
        hotel.description?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
        Spacer(Modifier.height(16.dp))
        hotel.address?.let { AddressRow(address = it) }
        hotel.phoneNumber?.let { PhoneRow(phone = it) }
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
        Spacer(Modifier.height(8.dp))
        place.description?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
        Spacer(Modifier.height(16.dp))
        place.address?.let { AddressRow(address = it) }
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
        Text(shop.type, style = MaterialTheme.typography.labelLarge, color = Color(0xFF1C1C84))
        Spacer(Modifier.height(8.dp))
        shop.description?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }
        Spacer(Modifier.height(16.dp))
        shop.address?.let { AddressRow(address = it) }
        shop.phoneNumber?.let { PhoneRow(phone = it) }
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C84),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1C1C84),
                                Color(0xFF1C1C84).copy(alpha = 0.5f)
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
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            if (imageUrls.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    imageUrls.forEach { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun AddressRow(address: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
