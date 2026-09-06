package com.example.focusfrog.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.focusfrog.ui.components.BottomNavBar
import com.example.focusfrog.ui.components.BottomNavItem
import com.example.focusfrog.ui.shop.ShopScreen
import com.example.focusfrog.ui.shop.ShopViewModel
import com.example.focusfrog.ui.stats.StatsScreen
import com.example.focusfrog.ui.stats.StatsViewModel
import com.example.focusfrog.ui.timer.TimerScreen
import com.example.focusfrog.ui.timer.TimerViewModel

@Composable
fun MainScreen(
    timerViewModel: TimerViewModel,
    shopViewModel: ShopViewModel,
    statsViewModel: StatsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Timer.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(BottomNavItem.Timer.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Timer.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Timer.route) {
                TimerScreen(viewModel = timerViewModel)
            }
            composable(BottomNavItem.Shop.route) {
                ShopScreen(viewModel = shopViewModel)
            }
            composable(BottomNavItem.Stats.route) {
                StatsScreen(viewModel = statsViewModel)
            }
        }
    }
}
