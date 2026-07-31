package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FleetAlertEntity
import com.example.data.local.FleetUnitEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FleetViewModel

@Composable
fun CentralControlScreen(
    viewModel: FleetViewModel,
    onNavigateToUnit: (String) -> Unit
) {
    val units by viewModel.filteredUnits.collectAsState()
    val alerts by viewModel.alertsState.collectAsState()
    val activeFilter by viewModel.unitFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val activeUnitsCount = units.count { it.status == "ACTIVE" }
    val stealthCount = units.count { it.stealthActive || it.status == "STEALTH" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Editorial Header
        item {
            EditorialTopHeader(
                title = "Central Fleet Control",
                issueTag = "ISSUE NO. 42 • HEAVY FLEET INTELLIGENCE",
                subtitle = "Real-time telemetry, location privacy masking, and fuel alerts.",
                onSearchClick = { }
            )
        }

        // Active Alerts Banner if any high severity alert
        val unresolvedAlert = alerts.firstOrNull { !it.isResolved }
        if (unresolvedAlert != null) {
            item {
                AlertBannerCard(
                    alert = unresolvedAlert,
                    onDismiss = { viewModel.resolveAlert(unresolvedAlert.id) }
                )
            }
        }

        // Top Feature Cards Row (Hero Stat & Secondary Stat)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditorialStatCard(
                    title = "Fleet Health",
                    value = "94.2%",
                    subtitle = "4 Active Heavy Units",
                    icon = Icons.Default.Speed,
                    containerColor = EditorialPrimaryContainer,
                    contentColor = EditorialOnPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                EditorialStatCard(
                    title = "Ghost Stealth",
                    value = "$stealthCount Unit",
                    subtitle = "Cryptographic GPS",
                    icon = Icons.Default.Shield,
                    containerColor = EditorialSecondaryContainer,
                    contentColor = EditorialOnSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Interactive Radar Map
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                FleetRadarMapView(unitsCount = units.size, stealthCount = stealthCount)
            }
        }

        // Filter Chips Row
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                EditorialIssueBadge(text = "UNIT TELEMETRY DIRECTORY")
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("ALL", "ACTIVE", "STEALTH", "MAINTENANCE")
                    items(filters) { filter ->
                        FilterChipPill(
                            title = filter,
                            isSelected = activeFilter == filter,
                            onClick = { viewModel.setUnitFilter(filter) }
                        )
                    }
                }
            }
        }

        // Search Bar Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Filter unit, driver or registration...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EditorialPrimary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .testTag("search_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = EditorialCardBg,
                    focusedContainerColor = EditorialSurface,
                    unfocusedBorderColor = EditorialOutlineVariant,
                    focusedBorderColor = EditorialPrimary
                ),
                singleLine = true
            )
        }

        // Fleet Unit List
        items(units, key = { it.id }) { unit ->
            FleetUnitCard(
                unit = unit,
                onClick = {
                    viewModel.setSelectedUnitId(unit.id)
                    onNavigateToUnit(unit.id)
                },
                onToggleStealth = { viewModel.toggleStealthForUnit(unit) }
            )
        }
    }
}

@Composable
private fun FilterChipPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) EditorialPrimary else EditorialCardBg
    val contentColor = if (isSelected) Color.White else EditorialOnSurface

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(
                1.dp,
                if (isSelected) EditorialPrimary else EditorialOutlineVariant,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("filter_$title")
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

@Composable
private fun AlertBannerCard(
    alert: FleetAlertEntity,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(EditorialAlertContainer)
            .border(1.dp, EditorialAlertRed.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EditorialAlertRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = EditorialAlertRed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${alert.unitId}: ${alert.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditorialOnSurface
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_alert")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Resolve Alert",
                    tint = EditorialAlertRed
                )
            }
        }
    }
}

@Composable
private fun FleetUnitCard(
    unit: FleetUnitEntity,
    onClick: () -> Unit,
    onToggleStealth: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(EditorialCardBg)
            .border(1.dp, EditorialOutlineVariant, RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .padding(18.dp)
            .testTag("unit_card_${unit.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (unit.status) {
                                    "ACTIVE" -> EditorialSuccessGreen
                                    "STEALTH" -> EditorialSecondary
                                    else -> EditorialAlertRed
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = unit.id,
                        style = MaterialTheme.typography.titleLarge,
                        color = EditorialOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  ${unit.registrationNo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditorialOnSurfaceVariant
                    )
                }

                // Stealth Toggle Pill
                Surface(
                    color = if (unit.stealthActive) EditorialSecondaryContainer else EditorialSurfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.clickable { onToggleStealth() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (unit.stealthActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = if (unit.stealthActive) EditorialOnSecondaryContainer else EditorialOnSurface,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (unit.stealthActive) "STEALTH" else "PUBLIC",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (unit.stealthActive) EditorialOnSecondaryContainer else EditorialOnSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = unit.modelName,
                style = MaterialTheme.typography.titleMedium,
                color = EditorialPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Driver: ${unit.driverName}  •  Cargo: ${unit.materialLoad}",
                style = MaterialTheme.typography.bodyMedium,
                color = EditorialOnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Bar: Speed, Fuel %, Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricChip(icon = Icons.Default.Speed, label = "${unit.speedKmH} km/h")
                MetricChip(icon = Icons.Default.LocalGasStation, label = "Fuel ${unit.fuelPercent}%")
                MetricChip(icon = Icons.Default.LocationOn, label = unit.currentLocation)
            }
        }
    }
}

@Composable
private fun MetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EditorialPrimary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = EditorialOnSurfaceVariant,
            maxLines = 1
        )
    }
}
