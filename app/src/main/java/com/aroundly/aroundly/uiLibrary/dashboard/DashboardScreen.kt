package com.aroundly.aroundly.uiLibrary.dashboard

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.ui.theme.AccentAmber
import com.aroundly.aroundly.ui.theme.PrimaryTeal
import com.aroundly.aroundly.ui.theme.PrimaryTealDark
import com.aroundly.aroundly.uiLibrary.common.GenericDropdown
import com.aroundly.aroundly.uiLibrary.viewModel.DashboardViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onLogout: () -> Unit = {},
    onRestaurantClick: (Restaurant) -> Unit,
    onHotelClick: (Hotel) -> Unit,
    onPlaceClick: (PlaceToVisit) -> Unit,
    onEquipmentClick: (EquipmentShop) -> Unit
) {
    val restaurants by viewModel.filteredRestaurants.collectAsState(initial = emptyList())
    val hotels by viewModel.filteredHotels.collectAsState(initial = emptyList())
    val places by viewModel.filteredPlaces.collectAsState(initial = emptyList())
    val equipment by viewModel.filteredEquipmentShops.collectAsState(initial = emptyList())
    val cities by viewModel.availableCities.collectAsState(initial = emptyList())
    val restTypes by viewModel.availableRestaurantTypes.collectAsState(initial = emptyList())
    val hotelTypes by viewModel.availableHotelTypes.collectAsState(initial = emptyList())
    val eqTypes by viewModel.availableEquipmentTypes.collectAsState(initial = emptyList())
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedRestType by viewModel.selectedRestaurantType.collectAsState()
    val selectedHotelType by viewModel.selectedHotelType.collectAsState()
    val selectedEqType by viewModel.selectedEquipmentType.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingLocation by viewModel.isLoadingLocation.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.detectLocation()
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Restaurants", "Hotels", "Places", "Rentals")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Aroundly",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryTeal,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                    IconButton(onClick = {
                        if (viewModel.hasLocationPermission()) {
                            viewModel.detectLocation()
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }) {
                        if (isLoadingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Use my location",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search cafes, restaurants, hotels...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryTeal)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryTeal,
                    focusedLeadingIconColor = PrimaryTeal,
                    cursorColor = PrimaryTeal,
                    focusedLabelColor = PrimaryTeal
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Filter dropdowns
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GenericDropdown(
                    label = "Select City",
                    options = cities.ifEmpty { listOf("All") },
                    selectedOption = selectedCity.ifEmpty { "All" },
                    onOptionSelected = { viewModel.setSelectedCity(it) }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GenericDropdown(
                            label = "Restaurant",
                            options = restTypes.ifEmpty { listOf("All") },
                            selectedOption = selectedRestType.ifEmpty { "All" },
                            onOptionSelected = { viewModel.setSelectedRestaurantType(it) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        GenericDropdown(
                            label = "Hotel",
                            options = hotelTypes.ifEmpty { listOf("All") },
                            selectedOption = selectedHotelType.ifEmpty { "All" },
                            onOptionSelected = { viewModel.setSelectedHotelType(it) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        GenericDropdown(
                            label = "Rental",
                            options = eqTypes.ifEmpty { listOf("All") },
                            selectedOption = selectedEqType.ifEmpty { "All" },
                            onOptionSelected = { viewModel.setSelectedEquipmentType(it) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Tab chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs.size) { index ->
                    FilterChip(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(tabs[index]) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryTeal,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                }
            }

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val showRestaurants = selectedTab == 0 || selectedTab == 1
                    val showHotels = selectedTab == 0 || selectedTab == 2
                    val showPlaces = selectedTab == 0 || selectedTab == 3
                    val showEquipment = selectedTab == 0 || selectedTab == 4

                    if (showRestaurants && restaurants.isNotEmpty()) {
                        item {
                            SectionHeader("Restaurants & Cafes", Icons.Default.Restaurant)
                        }
                        items(restaurants) { r ->
                            PlaceCard(
                                name = r.name,
                                type = r.type,
                                imageUrl = r.images?.firstOrNull(),
                                onClick = { onRestaurantClick(r) }
                            )
                        }
                    }

                    if (showHotels && hotels.isNotEmpty()) {
                        item {
                            SectionHeader("Places to Stay", Icons.Default.Place)
                        }
                        items(hotels) { h ->
                            PlaceCard(
                                name = h.name,
                                type = h.type,
                                imageUrl = h.images?.firstOrNull(),
                                onClick = { onHotelClick(h) }
                            )
                        }
                    }

                    if (showPlaces && places.isNotEmpty()) {
                        item {
                            SectionHeader("Places to Visit", Icons.Default.Place)
                        }
                        items(places) { p ->
                            PlaceCard(
                                name = p.name,
                                type = "Attraction",
                                imageUrl = p.images?.firstOrNull(),
                                onClick = { onPlaceClick(p) }
                            )
                        }
                    }

                    if (showEquipment && equipment.isNotEmpty()) {
                        item {
                            SectionHeader("Bikes & Cars", Icons.Default.Place)
                        }
                        items(equipment) { e ->
                            PlaceCard(
                                name = e.name,
                                type = e.type,
                                imageUrl = e.images?.firstOrNull(),
                                onClick = { onEquipmentClick(e) }
                            )
                        }
                    }

                    if (restaurants.isEmpty() && hotels.isEmpty() && places.isEmpty() && equipment.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No places found. Try adjusting your filters or add a new city.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PrimaryTeal,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryTealDark
        )
    }
}

@Composable
private fun PlaceCard(
    name: String,
    type: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .height(140.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryTeal.copy(alpha = 0.3f), PrimaryTealDark.copy(alpha = 0.5f))
                    )
                )
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
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
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    type,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}
