package com.aroundly.aroundly.uiLibrary.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * One row in a [AppSidemenu].
 *
 * The item closes the drawer before running [onClick], so a navigation does not
 * race the dismiss animation.
 */
data class SidemenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val selected: Boolean = false,
    val isDestructive: Boolean = false
)

/**
 * Shared sidemenu content for both dashboards.
 *
 * The traveler and manager screens run different palettes (dark amber versus
 * teal on white), so the colours are passed in from the caller instead of being
 * read from [MaterialTheme.colorScheme] - that scheme is dynamic on Android 12+
 * and would repaint the drawer in wallpaper colours on every device.
 */
@Composable
fun AppSidemenu(
    drawerState: DrawerState,
    scope: CoroutineScope,
    header: @Composable () -> Unit,
    items: List<SidemenuItem>,
    containerColor: Color,
    selectedContainerColor: Color,
    selectedContentColor: Color,
    unselectedContentColor: Color,
    destructiveContentColor: Color,
    modifier: Modifier = Modifier
) {
    val closeThen: () -> Unit = {
        scope.launch { drawerState.close() }
        Unit
    }

    ModalDrawerSheet(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp),
        drawerContainerColor = containerColor,
        drawerContentColor = unselectedContentColor
    ) {
        header()

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = unselectedContentColor.copy(alpha = 0.12f))
        Spacer(Modifier.height(8.dp))

        items.forEach { item ->
            val contentColor = when {
                item.isDestructive -> destructiveContentColor
                item.selected -> selectedContentColor
                else -> unselectedContentColor
            }

            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (item.selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                selected = item.selected,
                onClick = {
                    closeThen()
                    item.onClick()
                },
                icon = { Icon(item.icon, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = selectedContainerColor,
                    selectedIconColor = contentColor,
                    selectedTextColor = contentColor,
                    unselectedIconColor = unselectedContentColor,
                    unselectedTextColor = unselectedContentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

/** Drawer header row: icon on the left, title and subtitle stacked beside it. */
@Composable
fun SidemenuHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBackground: Color,
    iconTint: Color,
    titleColor: Color,
    subtitleColor: Color,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconBackground, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor
            )
        }
    }
}