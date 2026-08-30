package com.aroundly.aroundly.uiLibrary.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.BgDark
import com.aroundly.aroundly.ui.theme.BgMid
import com.aroundly.aroundly.ui.theme.BgWarm
import com.aroundly.aroundly.ui.theme.CategoryHotel
import com.aroundly.aroundly.ui.theme.CategoryPlace
import com.aroundly.aroundly.ui.theme.CategoryRental
import com.aroundly.aroundly.ui.theme.CategoryRestaurant
import com.aroundly.aroundly.ui.theme.White
import com.aroundly.aroundly.ui.theme.White50
import com.aroundly.aroundly.ui.theme.White70
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private val CategoryMarkerColors = mapOf(
    PlaceCategory.RESTAURANT to BitmapDescriptorFactory.HUE_ORANGE,
    PlaceCategory.HOTEL to BitmapDescriptorFactory.HUE_GREEN,
    PlaceCategory.PLACE to BitmapDescriptorFactory.HUE_VIOLET,
    PlaceCategory.RENTAL to BitmapDescriptorFactory.HUE_AZURE
)

private val CategoryColors = mapOf(
    PlaceCategory.RESTAURANT to CategoryRestaurant,
    PlaceCategory.HOTEL to CategoryHotel,
    PlaceCategory.PLACE to CategoryPlace,
    PlaceCategory.RENTAL to CategoryRental
)

@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()
    val mapPlaces by viewModel.mapPlaces.collectAsState()
    val isLoadingPlaces by viewModel.isLoadingPlaces.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val defaultLocation = remember { LatLng(28.6139, 77.2090) }
    val cameraPositionState = rememberCameraPositionState()
    var hasZoomedToUser by remember { mutableStateOf(false) }

    val userLatLng = currentLocation?.let {
        LatLng(it.latitude, it.longitude)
    } ?: defaultLocation

    val filteredPlaces = if (selectedCategory != null) {
        mapPlaces.filter { it.category == selectedCategory }
    } else {
        mapPlaces
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        MapHeader(onBack = onBack)

        CategoryFilterBar(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.toggleCategory(it) }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = viewModel.hasLocationPermission(),
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    mapToolbarEnabled = false
                )
            ) {
                if (currentLocation != null) {
                    val userMarkerState = remember(userLatLng) { MarkerState(position = userLatLng) }
                    Marker(
                        state = userMarkerState,
                        title = "You are here",
                        snippet = currentLocation?.cityHint ?: "Current location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )

                    Circle(
                        center = userLatLng,
                        radius = 5000.0,
                        fillColor = Color(0x22FFB020),
                        strokeColor = Color(0x66FFB020),
                        strokeWidth = 2f
                    )
                }

                filteredPlaces.forEach { place ->
                    place.latLng?.let { pos ->
                        val hue = CategoryMarkerColors[place.category] ?: BitmapDescriptorFactory.HUE_RED
                        Marker(
                            state = MarkerState(position = pos),
                            title = place.name,
                            snippet = "${place.category.label}\n${place.address ?: ""}",
                            icon = BitmapDescriptorFactory.defaultMarker(hue)
                        )
                    }
                }
            }

            if (!hasZoomedToUser && currentLocation != null) {
                LaunchedEffect(currentLocation) {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(userLatLng, 13f)
                    )
                    hasZoomedToUser = true
                }
            }

            if (isLoadingLocation || isLoadingPlaces) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(BgMid)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Amber,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = {
                        if (currentLocation != null) {
                            hasZoomedToUser = false
                            viewModel.fetchCurrentLocation()
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Amber)
                        .shadow(8.dp, CircleShape)
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "My Location",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            PlaceCountBadge(
                count = filteredPlaces.size,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )

            if (currentLocation != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xE61A1B22))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${currentLocation?.cityHint ?: "Current Location"}  •  ${filteredPlaces.size} places nearby",
                        color = White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (currentLocation == null && !isLoadingLocation) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xE61A1B22))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Enable location to see your position on the map",
                        color = White70,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterBar(
    selectedCategory: PlaceCategory?,
    onCategorySelected: (PlaceCategory) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(PlaceCategory.entries) { category ->
            val isSelected = selectedCategory == category
            val chipColor = CategoryColors[category] ?: Amber

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        category.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else chipColor)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = chipColor,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White,
                    containerColor = Color(0xFF2A2A2E),
                    labelColor = White70
                ),
                border = null
            )
        }
    }
}

@Composable
private fun PlaceCountBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xE61A1B22))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Amber)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$count places",
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun Circle(
    center: LatLng,
    radius: Double,
    fillColor: Color,
    strokeColor: Color,
    strokeWidth: Float
) {
    com.google.maps.android.compose.Circle(
        center = center,
        radius = radius,
        fillColor = fillColor,
        strokeColor = strokeColor,
        strokeWidth = strokeWidth
    )
}

@Composable
private fun MapHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .shadow(8.dp,
                RoundedCornerShape(
                    bottomStart = 28.dp,
                    bottomEnd = 28.dp))
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to BgDark,
                        0.45f to BgMid,
                        1.0f to BgWarm
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(500f, 500f)
                )
            )
            .padding(horizontal = 20.dp, vertical = 50.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x1AFFFFFF))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = White70,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Explore Map",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
