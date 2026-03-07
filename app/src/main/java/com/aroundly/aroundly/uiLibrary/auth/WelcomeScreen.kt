package com.aroundly.aroundly.uiLibrary.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onExplorerClick: () -> Unit,
    onBusinessOwnerClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF19141F),
            Color(0xFF050608)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top content: brand, description, chips
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = Color(0x33FFC857),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = Color(0xFFFFC857),
                                shape = RoundedCornerShape(18.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text(
                        text = "Local",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Spot",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC857),
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Discover cafés, restaurants, stays & rentals near you — instantly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB0B3C0)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip("Cafés")
                    CategoryChip("Food")
                    CategoryChip("Visit")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip("Stay")
                    CategoryChip("Rent")
                }
            }

            // Middle content: role cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "JOIN AS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7C7B86)
                )

                RoleCard(
                    title = "Explorer",
                    subtitle = "Discover & explore places near you",
                    gradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF443024),
                            Color(0xFF6B4725)
                        )
                    ),
                    iconBackgroundColor = Color(0xFFFFC857),
                    onClick = onExplorerClick
                )

                RoleCard(
                    title = "Business Owner",
                    subtitle = "List your restaurant, hotel or rental",
                    gradient = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF12333B),
                            Color(0xFF214A5A)
                        )
                    ),
                    iconBackgroundColor = Color(0xFF5DD0C1),
                    onClick = onBusinessOwnerClick
                )
            }

            // Bottom actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = Color(0xFF8A8690)
                    )
                    Text(
                        text = "Sign in",
                        color = Color(0xFFFFC857),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onSignInClick)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String
) {
    Box(
        modifier = Modifier
            .background(
                color = Color(0x33FFFFFF),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFFE3E0E8),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    gradient: Brush,
    iconBackgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = iconBackgroundColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = Color(0xFFE4E0D9),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFE4E0D9)
                )
            }
        }
    }
}

