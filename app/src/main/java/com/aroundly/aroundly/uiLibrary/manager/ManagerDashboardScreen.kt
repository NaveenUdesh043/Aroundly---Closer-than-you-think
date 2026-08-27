package com.aroundly.aroundly.uiLibrary.manager

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aroundly.aroundly.domain.model.EquipmentShop
import com.aroundly.aroundly.domain.model.Hotel
import com.aroundly.aroundly.domain.model.PlaceSubmission
import com.aroundly.aroundly.domain.model.PlaceToVisit
import com.aroundly.aroundly.domain.model.PlaceType
import com.aroundly.aroundly.domain.model.Restaurant
import com.aroundly.aroundly.domain.model.SubmissionStatus
import com.aroundly.aroundly.ui.theme.PrimaryTeal
import com.aroundly.aroundly.ui.theme.PrimaryTealDark
import com.aroundly.aroundly.uiLibrary.common.GenericDropdown
import com.aroundly.aroundly.uiLibrary.common.MenuTabContent
import com.aroundly.aroundly.uiLibrary.viewModel.ManagerViewModel
import com.aroundly.aroundly.uiLibrary.viewModel.MenuViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerDashboardScreen(
    viewModel: ManagerViewModel = hiltViewModel(),
    menuViewModel: MenuViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val mySubmissions by viewModel.mySubmissions.collectAsState(initial = emptyList())
    val myRestaurants by viewModel.myRestaurants.collectAsState(initial = emptyList())
    val myHotels by viewModel.myHotels.collectAsState(initial = emptyList())
    val myPlaces by viewModel.myPlaces.collectAsState(initial = emptyList())
    val myEquipmentShops by viewModel.myEquipmentShops.collectAsState(initial = emptyList())
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val submitMessage by viewModel.submitMessage.collectAsState()

    var placeType by remember { mutableStateOf(PlaceType.RESTAURANT) }
    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    var receiptUri by remember { mutableStateOf<Uri?>(null) }
    var receiptBytes by remember { mutableStateOf<ByteArray?>(null) }
    var placeImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var placeImageBytesList by remember { mutableStateOf<List<ByteArray>>(emptyList()) }

    val menuMessage by menuViewModel.message.collectAsState()

    val context = LocalContext.current
    val receiptPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            receiptUri = it
            receiptBytes = context.contentResolver.openInputStream(it)?.use { stream ->
                stream.readBytes()
            }
        }
    }
    val placeImagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        val newUris = (placeImageUris + uris).take(5)
        placeImageUris = newUris
        placeImageBytesList = newUris.mapNotNull { uri ->
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadMySubmissions()
        viewModel.loadMyApprovedPlaces()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Business", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryTeal,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            submitMessage?.let { msg ->
                Text(msg, color = if (msg.contains("fail")) Color.Red else PrimaryTeal)
                Spacer(Modifier.height(8.dp))
            }

            Text("Add New Place", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            GenericDropdown(
                label = "Place Type",
                options = PlaceType.entries.map { it.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() } },
                selectedOption = placeType.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() },
                onOptionSelected = { opt ->
                    placeType = PlaceType.entries.find {
                        it.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() } == opt
                    } ?: PlaceType.RESTAURANT
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Place Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text(when (placeType) {
                    PlaceType.RESTAURANT -> "Type (e.g. BYOB, Fine Dining)"
                    PlaceType.HOTEL -> "Type (e.g. Budget, Luxury)"
                    PlaceType.PLACE_TO_VISIT -> "Type (e.g. Museum, Park)"
                    PlaceType.EQUIPMENT_SHOP -> "Type (e.g. Bikes, Cars)"
                }) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            PaymentInfoSection()
            Spacer(Modifier.height(16.dp))
            Text("Payment Receipt (required)", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .clickable {
                        receiptPicker.launch(
                            PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                .build()
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (receiptUri != null) {
                    AsyncImage(
                        model = receiptUri,
                        contentDescription = "Receipt",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(40.dp))
                        Text("Tap to upload receipt")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Restaurant/Place Images", style = MaterialTheme.typography.labelMedium)
            Text("Add up to 5 images (optional)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                placeImageUris.forEachIndexed { index, uri ->
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Place image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                if (placeImageUris.size < 5) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .clickable {
                                placeImagesPicker.launch(
                                    PickVisualMediaRequest.Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        .build()
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add image", modifier = Modifier.size(32.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            androidx.compose.material3.Button(
                onClick = {
                    viewModel.clearMessage()
                    if (name.isNotBlank() && city.isNotBlank() && receiptBytes != null) {
                        viewModel.submitPlace(
                            placeType = placeType,
                            name = name,
                            city = city,
                            type = type,
                            description = description,
                            address = address,
                            phoneNumber = phoneNumber,
                            websiteUrl = websiteUrl,
                            receiptImageBytes = receiptBytes,
                            placeImageBytesList = placeImageBytesList
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting && name.isNotBlank() && city.isNotBlank() && receiptBytes != null
            ) {
                Text(if (isSubmitting) "Submitting..." else "Submit for Approval")
            }

            Spacer(Modifier.height(32.dp))
            Text("My Approved Places", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            menuMessage?.let { msg ->
                Text(msg, color = if (msg.contains("fail")) Color.Red else PrimaryTeal)
                Spacer(Modifier.height(8.dp))
            }

            var showEditSheet by remember { mutableStateOf<Any?>(null) }

            var activeMenuShopId by remember { mutableStateOf<String?>(null) }
            val menuImagePicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri == null) return@rememberLauncherForActivityResult
                val shopId = activeMenuShopId
                if (shopId == null) return@rememberLauncherForActivityResult
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { stream ->
                        stream.readBytes()
                    }
                    if (bytes != null && bytes.isNotEmpty()) {
                        menuViewModel.uploadMenuImage(shopId, bytes)
                    }
                } catch (_: Exception) {}
            }

            myRestaurants.forEach { r ->
                ApprovedPlaceCardWithMenu(
                    name = r.name,
                    type = "Restaurant",
                    shopId = r.id,
                    onEdit = { showEditSheet = r },
                    onUploadImage = {
                        activeMenuShopId = r.id
                        menuImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    menuViewModel = menuViewModel
                )
                Spacer(Modifier.height(8.dp))
            }
            myHotels.forEach { h ->
                ApprovedPlaceCardWithMenu(
                    name = h.name,
                    type = "Hotel",
                    shopId = h.id,
                    onEdit = { showEditSheet = h },
                    onUploadImage = {
                        activeMenuShopId = h.id
                        menuImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    menuViewModel = menuViewModel
                )
                Spacer(Modifier.height(8.dp))
            }
            myPlaces.forEach { p ->
                ApprovedPlaceCardWithMenu(
                    name = p.name,
                    type = "Place",
                    shopId = p.id,
                    onEdit = { showEditSheet = p },
                    onUploadImage = {
                        activeMenuShopId = p.id
                        menuImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    menuViewModel = menuViewModel
                )
                Spacer(Modifier.height(8.dp))
            }
            myEquipmentShops.forEach { e ->
                ApprovedPlaceCardWithMenu(
                    name = e.name,
                    type = "Equipment",
                    shopId = e.id,
                    onEdit = { showEditSheet = e },
                    onUploadImage = {
                        activeMenuShopId = e.id
                        menuImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    menuViewModel = menuViewModel
                )
                Spacer(Modifier.height(8.dp))
            }
            if (myRestaurants.isEmpty() && myHotels.isEmpty() && myPlaces.isEmpty() && myEquipmentShops.isEmpty()) {
                Text("No approved places yet. Submit a new place and wait for admin approval.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
            }

            if (showEditSheet != null) {
                EditPlaceBottomSheet(
                    place = showEditSheet!!,
                    viewModel = viewModel,
                    onDismiss = { showEditSheet = null }
                )
            }

            Spacer(Modifier.height(24.dp))
            Text("My Submissions (Pending/Approved/Rejected)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            mySubmissions.forEach { sub ->
                SubmissionCard(submission = sub)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PaymentInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Payment",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "One-time listing fee",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(12.dp))

        // Listing fee card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF151515)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LISTING FEE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFFC107)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "LKR 5,000",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFC107)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "One-time payment for permanent listing",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE0E0E0)
                )
                Spacer(Modifier.height(12.dp))

                PaymentBullet(text = "Listed on Aroundly")
                PaymentBullet(text = "Priority placement")
                PaymentBullet(text = "Analytics dashboard")
                PaymentBullet(text = "Unlimited updates")
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Bank Transfer Details",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF6E5)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                PaymentInfoRow(label = "Bank", value = "Commercial Bank")
                PaymentInfoRow(label = "Account Name", value = "R N U SILVA")
                PaymentInfoRow(label = "Account No.", value = "8000088332")
                PaymentInfoRow(label = "Branch", value = "Negombo")
            }
        }
    }
}

@Composable
private fun PaymentBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            color = Color(0xFFFFC107),
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

@Composable
private fun PaymentInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF8D8D8D)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryTealDark
        )
    }
}

@Composable
private fun ApprovedPlaceCard(
    name: String,
    type: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, fontWeight = FontWeight.Bold)
                Text(type, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryTeal)
        }
    }
}

@Composable
private fun ApprovedPlaceCardWithMenu(
    name: String,
    type: String,
    shopId: String,
    onEdit: () -> Unit,
    onUploadImage: () -> Unit,
    menuViewModel: MenuViewModel
) {
    val menuImages by menuViewModel.menuImages.collectAsState()

    LaunchedEffect(shopId) {
        menuViewModel.loadMenuImages(shopId)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold)
                    Text(type, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryTeal)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onUploadImage,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Upload Menu Image")
            }

            if (menuImages.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Menu Images (${menuImages.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryTealDark
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    menuImages.take(3).forEach { image ->
                        AsyncImage(
                            model = image.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    }
                }
                if (menuImages.size > 3) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "+${menuImages.size - 3} more images",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPlaceBottomSheet(
    place: Any,
    viewModel: ManagerViewModel,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        when (place) {
            is Restaurant -> EditRestaurantForm(restaurant = place, viewModel = viewModel, onDismiss = onDismiss)
            is Hotel -> EditHotelForm(hotel = place, viewModel = viewModel, onDismiss = onDismiss)
            is PlaceToVisit -> EditPlaceForm(place = place, viewModel = viewModel, onDismiss = onDismiss)
            is EquipmentShop -> EditEquipmentForm(shop = place, viewModel = viewModel, onDismiss = onDismiss)
            else -> {}
        }
    }
}

@Composable
private fun EditRestaurantForm(restaurant: Restaurant, viewModel: ManagerViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(restaurant.name) }
    var city by remember { mutableStateOf(restaurant.city ?: "") }
    var type by remember { mutableStateOf(restaurant.type) }
    var description by remember { mutableStateOf(restaurant.description ?: "") }
    var address by remember { mutableStateOf(restaurant.address ?: "") }
    var phone by remember { mutableStateOf(restaurant.phoneNumber ?: "") }
    var newImageBytesList by remember { mutableStateOf<List<ByteArray>>(emptyList()) }
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        val bytesList = mutableListOf<ByteArray>()
        for (uri in uris) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                bytesList.add(stream.readBytes())
            }
        }
        newImageUris = uris
        newImageBytesList = bytesList
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Edit Restaurant", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))

        if (restaurant.images?.isNotEmpty() == true && newImageUris.isEmpty()) {
            Text("Current Images", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = PrimaryTealDark)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                restaurant.images.take(5).forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        OutlinedButton(onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Change Images (up to 5)")
        }
        if (newImageUris.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("${newImageUris.size} new image(s) selected", style = MaterialTheme.typography.bodySmall, color = PrimaryTeal)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                newImageUris.take(5).forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updateRestaurant(
                restaurant.copy(name = name, city = city, type = type, description = description, address = address, phoneNumber = phone),
                newImageBytesList = newImageBytesList
            )
            onDismiss()
        }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun EditHotelForm(hotel: Hotel, viewModel: ManagerViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(hotel.name) }
    var city by remember { mutableStateOf(hotel.city ?: "") }
    var type by remember { mutableStateOf(hotel.type) }
    var description by remember { mutableStateOf(hotel.description ?: "") }
    var address by remember { mutableStateOf(hotel.address ?: "") }
    var phone by remember { mutableStateOf(hotel.phoneNumber ?: "") }
    var newImageBytesList by remember { mutableStateOf<List<ByteArray>>(emptyList()) }
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        val bytesList = mutableListOf<ByteArray>()
        for (uri in uris) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                bytesList.add(stream.readBytes())
            }
        }
        newImageUris = uris
        newImageBytesList = bytesList
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Edit Hotel", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))

        if (hotel.images?.isNotEmpty() == true && newImageUris.isEmpty()) {
            Text("Current Images", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = PrimaryTealDark)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                hotel.images.take(5).forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        OutlinedButton(onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Change Images (up to 5)")
        }
        if (newImageUris.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("${newImageUris.size} new image(s) selected", style = MaterialTheme.typography.bodySmall, color = PrimaryTeal)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                newImageUris.take(5).forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updateHotel(
                hotel.copy(name = name, city = city, type = type, description = description, address = address, phoneNumber = phone),
                newImageBytesList = newImageBytesList
            )
            onDismiss()
        }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun EditPlaceForm(place: PlaceToVisit, viewModel: ManagerViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(place.name) }
    var city by remember { mutableStateOf(place.city ?: "") }
    var description by remember { mutableStateOf(place.description ?: "") }
    var address by remember { mutableStateOf(place.address ?: "") }
    var newImageBytesList by remember { mutableStateOf<List<ByteArray>>(emptyList()) }
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        val bytesList = mutableListOf<ByteArray>()
        for (uri in uris) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                bytesList.add(stream.readBytes())
            }
        }
        newImageUris = uris
        newImageBytesList = bytesList
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Edit Place", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))

        if (place.images?.isNotEmpty() == true && newImageUris.isEmpty()) {
            Text("Current Images", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = PrimaryTealDark)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                place.images.take(5).forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        OutlinedButton(onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Change Images (up to 5)")
        }
        if (newImageUris.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("${newImageUris.size} new image(s) selected", style = MaterialTheme.typography.bodySmall, color = PrimaryTeal)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                newImageUris.take(5).forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updatePlace(
                place.copy(name = name, city = city, description = description, address = address),
                newImageBytesList = newImageBytesList
            )
            onDismiss()
        }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun EditEquipmentForm(shop: EquipmentShop, viewModel: ManagerViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(shop.name) }
    var city by remember { mutableStateOf(shop.city ?: "") }
    var type by remember { mutableStateOf(shop.type) }
    var description by remember { mutableStateOf(shop.description ?: "") }
    var address by remember { mutableStateOf(shop.address ?: "") }
    var phone by remember { mutableStateOf(shop.phoneNumber ?: "") }
    var newImageBytesList by remember { mutableStateOf<List<ByteArray>>(emptyList()) }
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        val bytesList = mutableListOf<ByteArray>()
        for (uri in uris) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                bytesList.add(stream.readBytes())
            }
        }
        newImageUris = uris
        newImageBytesList = bytesList
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Edit Equipment Shop", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))

        if (shop.images?.isNotEmpty() == true && newImageUris.isEmpty()) {
            Text("Current Images", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = PrimaryTealDark)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                shop.images.take(5).forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        OutlinedButton(onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Change Images (up to 5)")
        }
        if (newImageUris.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("${newImageUris.size} new image(s) selected", style = MaterialTheme.typography.bodySmall, color = PrimaryTeal)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                newImageUris.take(5).forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updateEquipmentShop(
                shop.copy(name = name, city = city, type = type, description = description, address = address, phoneNumber = phone),
                newImageBytesList = newImageBytesList
            )
            onDismiss()
        }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SubmissionCard(submission: PlaceSubmission) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(submission.name, fontWeight = FontWeight.Bold)
                Text(
                    submission.status.name,
                    color = when (submission.status) {
                        SubmissionStatus.PENDING -> Color(0xFFFFA000)
                        SubmissionStatus.APPROVED -> Color(0xFF4CAF50)
                        SubmissionStatus.REJECTED -> Color(0xFFE53935)
                    }
                )
            }
            Text("${submission.placeType.name} • ${submission.city}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BusinessTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF3DD) else Color.White
        ),
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE0D8CC))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color(0xFF5A3E10) else Color(0xFF4A4137),
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
