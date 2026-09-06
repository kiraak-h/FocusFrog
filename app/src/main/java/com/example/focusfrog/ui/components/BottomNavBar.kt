package com.example.focusfrog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Timer : BottomNavItem("timer", "Timer", Icons.Default.Home)
    object Shop : BottomNavItem("shop", "Shop", Icons.Default.ShoppingCart)
    object Stats : BottomNavItem("stats", "Stats", Icons.Default.Star)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Timer,
        BottomNavItem.Shop,
        BottomNavItem.Stats
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) }
            )
        }
    }
}
