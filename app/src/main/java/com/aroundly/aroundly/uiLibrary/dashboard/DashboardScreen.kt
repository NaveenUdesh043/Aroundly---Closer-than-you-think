package com.aroundly.aroundly.uiLibrary.dashboard

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aroundly.aroundly.R
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.AmberDark
import com.aroundly.aroundly.ui.theme.AmberGlow
import com.aroundly.aroundly.ui.theme.BgDark
import com.aroundly.aroundly.ui.theme.BgMid
import com.aroundly.aroundly.ui.theme.BgWarm
import com.aroundly.aroundly.ui.theme.SageGreen
import com.aroundly.aroundly.ui.theme.White
import com.aroundly.aroundly.ui.theme.White50
import com.aroundly.aroundly.ui.theme.White70
import com.aroundly.aroundly.ui.theme.White25
import com.aroundly.aroundly.ui.theme.TextDisabled
import com.aroundly.aroundly.ui.theme.TextMuted
import com.aroundly.aroundly.ui.theme.TextOnDarkMuted
import com.aroundly.aroundly.ui.theme.TextPrimary
import com.aroundly.aroundly.ui.theme.TextSecondary
import com.aroundly.aroundly.ui.theme.StatusPending
import com.aroundly.aroundly.ui.theme.StatusApproved
import com.aroundly.aroundly.ui.theme.StatusRejected
import com.aroundly.aroundly.uiLibrary.common.GenericDropdown
import com.aroundly.aroundly.uiLibrary.common.LogoutConfirmBottomSheet
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
    onEquipmentClick: (EquipmentShop) -> Unit,
    onMapClick: () -> Unit = {}
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
    val currentLocation by viewModel.currentLocation.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.detectLocation()
        }
    }

    var showLogoutSheet by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Restaurants", "Hotels", "Places", "Rentals")

    val sectionCategories = listOf(
        Triple("Restaurants & Cafes", Icons.Default.Restaurant, Color(0xFFFF8F00)),
        Triple("Places to Stay", Icons.Default.Hotel, Color(0xFF00897B)),
        Triple("Places to Visit", Icons.Default.Place, Color(0xFF7B1FA2)),
        Triple("Travel Rentals", Icons.Default.ShoppingCart, Color(0xFF1565C0))
    )

    Scaffold(
        containerColor = BgDark
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // ── HEADER ──
                item {
                    DashboardHeader(
                        city = selectedCity,
                        isLoadingLocation = isLoadingLocation,
                        onLogout = { showLogoutSheet = true },
                        onLocationClick = {
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
                        },
                        onMapClick = onMapClick
                    )
                }

                // ── SEARCH ──
                item {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) }
                    )
                }

                // ── FILTER CHIPS ──
                item {
                    FilterChipRow(
                        tabs = tabs,
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                // ── DROPDOWN FILTERS ──
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GenericDropdown(
                            label = "Select City",
                            options = cities.ifEmpty { listOf("All") },
                            selectedOption = selectedCity.ifEmpty { "Select City" },
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
                                    selectedOption = selectedRestType.ifEmpty { "Cafes" },
                                    onOptionSelected = { viewModel.setSelectedRestaurantType(it) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                GenericDropdown(
                                    label = "Hotel",
                                    options = hotelTypes.ifEmpty { listOf("All") },
                                    selectedOption = selectedHotelType.ifEmpty { "Hotels" },
                                    onOptionSelected = { viewModel.setSelectedHotelType(it) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                GenericDropdown(
                                    label = "Rental",
                                    options = eqTypes.ifEmpty { listOf("All") },
                                    selectedOption = selectedEqType.ifEmpty { "Rental" },
                                    onOptionSelected = { viewModel.setSelectedEquipmentType(it) }
                                )
                            }
                        }
                    }
                }

                // ── LOCATION BANNER ──
                item {
                    AnimatedVisibility(
                        visible = currentLocation?.cityHint != null && selectedCity.isNotEmpty(),
                        enter = fadeIn()
                    ) {
                        val cityFromLocation = currentLocation?.cityHint ?: ""
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background( BgMid)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FF8F00)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFFF57C00),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Showing places near $cityFromLocation",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = White
                                    )
                                }
                            }
                        }
                    }
                }

                // ── CONTENT ──
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Amber)
                        }
                    }
                } else {
                    val showRestaurants = selectedTab == 0 || selectedTab == 1
                    val showHotels = selectedTab == 0 || selectedTab == 2
                    val showPlaces = selectedTab == 0 || selectedTab == 3
                    val showEquipment = selectedTab == 0 || selectedTab == 4

                    var itemCount = 0

                    if (showRestaurants && restaurants.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = sectionCategories[0].first,
                                icon = sectionCategories[0].second,
                                accentColor = sectionCategories[0].third
                            )
                        }
                        if (restaurants.isNotEmpty()) {
                            // Hero card for first restaurant
                            item {
                                RestaurantHeroCard(
                                    restaurant = restaurants.first(),
                                    onClick = { onRestaurantClick(restaurants.first()) }
                                )
                            }
                        }
                        if (restaurants.size > 1) {
                            items(restaurants.drop(1)) { r ->
                                itemCount++
                                AnimatedCardAppear(index = itemCount) {
                                    PlaceCard(
                                        name = r.name,
                                        type = r.type,
                                        imageUrl = r.images?.firstOrNull(),
                                        badgeColor = sectionCategories[0].third,
                                        onClick = { onRestaurantClick(r) }
                                    )
                                }
                            }
                        }
                    }

                    if (showHotels && hotels.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = sectionCategories[1].first,
                                icon = sectionCategories[1].second,
                                accentColor = sectionCategories[1].third
                            )
                        }
                        itemsIndexed(hotels) { index, h ->
                            itemCount++
                            AnimatedCardAppear(index = itemCount) {
                                PlaceCard(
                                    name = h.name,
                                    type = h.type,
                                    imageUrl = h.images?.firstOrNull(),
                                    badgeColor = sectionCategories[1].third,
                                    onClick = { onHotelClick(h) }
                                )
                            }
                        }
                    }

                    if (showPlaces && places.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = sectionCategories[2].first,
                                icon = sectionCategories[2].second,
                                accentColor = sectionCategories[2].third
                            )
                        }
                        itemsIndexed(places) { index, p ->
                            itemCount++
                            AnimatedCardAppear(index = itemCount) {
                                PlaceCard(
                                    name = p.name,
                                    type = "Attraction",
                                    imageUrl = p.images?.firstOrNull(),
                                    badgeColor = sectionCategories[2].third,
                                    onClick = { onPlaceClick(p) }
                                )
                            }
                        }
                    }

                    if (showEquipment && equipment.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = sectionCategories[3].first,
                                icon = sectionCategories[3].second,
                                accentColor = sectionCategories[3].third
                            )
                        }
                        itemsIndexed(equipment) { index, e ->
                            itemCount++
                            AnimatedCardAppear(index = itemCount) {
                                PlaceCard(
                                    name = e.name,
                                    type = e.type,
                                    imageUrl = e.images?.firstOrNull(),
                                    badgeColor = sectionCategories[3].third,
                                    onClick = { onEquipmentClick(e) }
                                )
                            }
                        }
                    }

                    if (restaurants.isEmpty() && hotels.isEmpty() && places.isEmpty() && equipment.isEmpty()) {
                        item {
                            EmptyState()
                        }
                    }
                }
            }
        }
    }

    if (showLogoutSheet) {
        LogoutConfirmBottomSheet(
            onConfirm = {
                showLogoutSheet = false
                onLogout()
            },
            onDismiss = { showLogoutSheet = false }
        )
    }
}

@Composable
private fun AnimatedCardAppear(
    index: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            delayMillis = (index * 60).coerceAtMost(300),
            easing = LinearEasing
        ),
        label = "cardAlpha"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
        }
    ) {
        content()
    }
}

@Composable
private fun DashboardHeader(
    city: String,
    isLoadingLocation: Boolean,
    onLogout: () -> Unit,
    onLocationClick: () -> Unit,
    onMapClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
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
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(Amber, AmberDark))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.aroundly_app_icon),
                                contentDescription = "App Icon",
                                modifier = Modifier
                                    .size(34.dp)
                                .clip(CircleShape),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(androidx.compose.ui.text.SpanStyle(color = White, fontWeight = FontWeight.Bold)) {
                                    append("Around")
                                }
                                withStyle(androidx.compose.ui.text.SpanStyle(color = Amber, fontWeight = FontWeight.Bold)) {
                                    append("ly")
                                }
                            },
                            fontSize = 22.sp
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Discover amazing places",
                        style = MaterialTheme.typography.bodyLarge,
                        color = White70,
                        fontStyle = FontStyle.Italic
                    )
                }
                IconButton(
                    onClick = onMapClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x1AFFFFFF))
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Map",
                        tint = White70,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x1AFFFFFF))
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = White70,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Location pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1AFFFFFF))
                    .clickable { onLocationClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FF8F00)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoadingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Amber,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFFFF8F00),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (city.isNotEmpty()) city else "Use my location",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = White
                        )
                        Text(
                            text = "Tap to change location",
                            style = MaterialTheme.typography.bodySmall,
                            color = White50
                        )
                    }
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = White25,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(BgMid)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Amber,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            androidx.compose.material3.TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search what you want..",
                        color = White50,
                        fontSize = 14.sp
                    )
                },
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Amber
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun FilterChipRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs.size) { index ->
            FilterChip(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                label = {
                    Text(
                        tabs[index],
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = if (selectedTab == index) {
                    {
                        Icon(
                            imageVector = when (index) {
                                1 -> Icons.Default.Restaurant
                                2 -> Icons.Default.Hotel
                                3 -> Icons.Default.Place
                                4 -> Icons.Default.ShoppingCart
                                else -> Icons.Default.Search
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1C1C1C),
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
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )
    }
}

@Composable
private fun RestaurantHeroCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BgMid)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val imageUrl = restaurant.images?.firstOrNull()
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = restaurant.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFF8F00),
                                    Color(0xFFEF6C00)
                                )
                            )
                        )
                )
            }

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 60f
                        )
                    )
            )

            // Featured badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCCFF8F00))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "Featured",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    restaurant.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                Text(
                    restaurant.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = White
                )
                    }
                    if (restaurant.city != null) {
                        Spacer(Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Amber,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                restaurant.city,
                                style = MaterialTheme.typography.bodySmall,
                                color = White70
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceCard(
    name: String,
    type: String,
    imageUrl: String?,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BgMid)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(110.dp)
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    bottomStart = 18.dp
                                )
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    bottomStart = 18.dp
                                )
                            )
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        badgeColor.copy(alpha = 0.7f),
                                        badgeColor.copy(alpha = 0.4f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (badgeColor) {
                                Color(0xFFFF8F00) -> Icons.Default.Restaurant
                                Color(0xFF00897B) -> Icons.Default.Hotel
                                Color(0xFF7B1FA2) -> Icons.Default.Place
                                else -> Icons.Default.ShoppingCart
                            },
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Type badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    type,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeColor,
                    fontSize = 10.sp
                )
            }
                Spacer(Modifier.height(6.dp))
                Text(
                    name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = White50,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        type,
                        style = MaterialTheme.typography.bodySmall,
                        color = White50,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0x22FFB020),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = White50,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "No places found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Try adjusting your filters or search\nfor something different",
                style = MaterialTheme.typography.bodyMedium,
                color = White70,
                lineHeight = 20.sp
            )
        }
    }
}
