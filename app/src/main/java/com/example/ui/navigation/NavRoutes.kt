package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object CentralControl : Screen(
        route = "central_control",
        title = "Central",
        selectedIcon = Icons.Filled.Radar,
        unselectedIcon = Icons.Outlined.Radar
    )

    object UnitTelemetry : Screen(
        route = "unit_telemetry",
        title = "Telemetry",
        selectedIcon = Icons.Filled.LocalShipping,
        unselectedIcon = Icons.Outlined.LocalShipping
    )

    object StealthConfig : Screen(
        route = "stealth_config",
        title = "Stealth",
        selectedIcon = Icons.Filled.Security,
        unselectedIcon = Icons.Outlined.Security
    )

    object DriverCompanion : Screen(
        route = "driver_companion",
        title = "Companion",
        selectedIcon = Icons.Filled.Navigation,
        unselectedIcon = Icons.Outlined.Navigation
    )

    object MaintenanceHealth : Screen(
        route = "maintenance_health",
        title = "Health",
        selectedIcon = Icons.Filled.Build,
        unselectedIcon = Icons.Outlined.Build
    )

    object FuelManagement : Screen(
        route = "fuel_management",
        title = "Fuel",
        selectedIcon = Icons.Filled.LocalGasStation,
        unselectedIcon = Icons.Outlined.LocalGasStation
    )
}

val bottomNavItems = listOf(
    Screen.CentralControl,
    Screen.UnitTelemetry,
    Screen.FuelManagement,
    Screen.StealthConfig,
    Screen.DriverCompanion,
    Screen.MaintenanceHealth
)
