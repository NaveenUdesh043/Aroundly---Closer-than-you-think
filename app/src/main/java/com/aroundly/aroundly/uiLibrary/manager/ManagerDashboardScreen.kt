package com.aroundly.aroundly.uiLibrary.manager

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import com.aroundly.aroundly.uiLibrary.viewModel.ManagerViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerDashboardScreen(
    viewModel: ManagerViewModel = hiltViewModel(),
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
    var receiptUri by remember { mutableStateOf<Uri?>(null) }
    var receiptBytes by remember { mutableStateOf<ByteArray?>(null) }
    var placeImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var placeImageBytesList by remember { mutableStateOf<List<ByteArray>>(emptyList()) }

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

            Spacer(Modifier.height(16.dp))

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
            Text("My Approved Places (Edit without approval)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            var showEditSheet by remember { mutableStateOf<Any?>(null) }
            myRestaurants.forEach { r ->
                ApprovedPlaceCard(name = r.name, type = "Restaurant", onClick = { showEditSheet = r })
                Spacer(Modifier.height(8.dp))
            }
            myHotels.forEach { h ->
                ApprovedPlaceCard(name = h.name, type = "Hotel", onClick = { showEditSheet = h })
                Spacer(Modifier.height(8.dp))
            }
            myPlaces.forEach { p ->
                ApprovedPlaceCard(name = p.name, type = "Place", onClick = { showEditSheet = p })
                Spacer(Modifier.height(8.dp))
            }
            myEquipmentShops.forEach { e ->
                ApprovedPlaceCard(name = e.name, type = "Equipment", onClick = { showEditSheet = e })
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
    Column(Modifier.padding(16.dp)) {
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
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updateRestaurant(restaurant.copy(name = name, city = city, type = type, description = description, address = address, phoneNumber = phone))
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
    Column(Modifier.padding(16.dp)) {
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
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updateHotel(hotel.copy(name = name, city = city, type = type, description = description, address = address, phoneNumber = phone))
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
    Column(Modifier.padding(16.dp)) {
        Text("Edit Place", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updatePlace(place.copy(name = name, city = city, description = description, address = address))
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
    Column(Modifier.padding(16.dp)) {
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
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.updateEquipmentShop(shop.copy(name = name, city = city, type = type, description = description, address = address, phoneNumber = phone))
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
