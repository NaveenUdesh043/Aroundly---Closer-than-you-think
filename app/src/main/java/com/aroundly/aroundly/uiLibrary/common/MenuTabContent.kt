package com.aroundly.aroundly.uiLibrary.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aroundly.aroundly.ui.theme.Amber
import com.aroundly.aroundly.ui.theme.BgDark
import com.aroundly.aroundly.ui.theme.BgMid
import com.aroundly.aroundly.ui.theme.White
import com.aroundly.aroundly.ui.theme.White70
import com.aroundly.aroundly.uiLibrary.viewModel.MenuViewModel

@Composable
fun MenuTabContent(
    shopId: String,
    menuViewModel: MenuViewModel,
    showDelete: Boolean = false
) {
    val menuImages by menuViewModel.menuImages.collectAsState()
    val isLoading by menuViewModel.isLoading.collectAsState()

    LaunchedEffect(shopId) {
        menuViewModel.loadMenuImages(shopId)
    }

    if (isLoading && menuImages.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Amber)
        }
    } else if (menuImages.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "No menu images yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Upload photos of your menu",
                    style = MaterialTheme.typography.bodySmall,
                    color = White70
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(menuImages, key = { it.id }) { image ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = image.imageUrl,
                        contentDescription = "Menu image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (showDelete) {
                        IconButton(
                            onClick = {
                                menuViewModel.deleteMenuImage(image.id, shopId)
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier
                                    .background(
                                        Color.Black.copy(alpha = 0.5f),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
