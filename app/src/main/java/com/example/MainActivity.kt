package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.Screen
import com.example.ui.navigation.bottomNavItems
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FleetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GhostFleetTheme {
                GhostFleetApp()
            }
        }
    }
}

@Composable
fun GhostFleetApp() {
    val navController = rememberNavController()
    val viewModel: FleetViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.CentralControl.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = EditorialBackground,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation"),
                containerColor = Color(0xFFF7F2FA),
                tonalElevation = 6.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EditorialOnSecondaryContainer,
                            selectedTextColor = EditorialOnSecondaryContainer,
                            indicatorColor = EditorialSecondaryContainer,
                            unselectedIconColor = EditorialOnSurfaceVariant,
                            unselectedTextColor = EditorialOnSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.CentralControl.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.CentralControl.route) {
                CentralControlScreen(
                    viewModel = viewModel,
                    onNavigateToUnit = {
                        navController.navigate(Screen.UnitTelemetry.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.UnitTelemetry.route) {
                UnitTelemetryScreen(viewModel = viewModel)
            }

            composable(Screen.FuelManagement.route) {
                FuelManagementScreen(viewModel = viewModel)
            }

            composable(Screen.StealthConfig.route) {
                StealthConfigScreen(viewModel = viewModel)
            }

            composable(Screen.DriverCompanion.route) {
                DriverCompanionScreen(viewModel = viewModel)
            }

            composable(Screen.MaintenanceHealth.route) {
                MaintenanceHealthScreen(viewModel = viewModel)
            }
        }
    }
}
