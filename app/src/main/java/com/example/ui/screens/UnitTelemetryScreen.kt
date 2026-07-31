package com.example.ui.screens

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
import com.example.data.local.FleetUnitEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FleetViewModel

@Composable
fun UnitTelemetryScreen(
    viewModel: FleetViewModel
) {
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val allUnits by viewModel.unitsState.collectAsState()

    val unit = selectedUnit ?: allUnits.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            EditorialTopHeader(
                title = "Vehicle Unit Telemetry",
                issueTag = "ISSUE NO. 42 • VAHAN TELEMATICS",
                subtitle = "Live axle sensor diagnostics, fuel flow, & weight distribution."
            )
        }

        // Horizontal Unit Picker Row
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allUnits) { item ->
                    val isSelected = item.id == unit?.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) EditorialPrimary else EditorialCardBg)
                            .border(1.dp, if (isSelected) EditorialPrimary else EditorialOutlineVariant, RoundedCornerShape(20.dp))
                            .clickable { viewModel.setSelectedUnitId(item.id) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("select_unit_${item.id}")
                    ) {
                        Column {
                            Text(
                                text = item.id,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Color.White else EditorialOnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.modelName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else EditorialOnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (unit != null) {
            // Vahan Identity Hero Card
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(EditorialPrimaryContainer)
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EditorialIssueBadge(
                                    text = "VAHAN IDENTITY • ${unit.registrationNo}",
                                    textColor = EditorialOnPrimaryContainer
                                )

                                Surface(
                                    color = if (unit.stealthActive) EditorialSecondaryContainer else EditorialSuccessContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (unit.stealthActive) "GHOST MODE" else "LIVE PING",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (unit.stealthActive) EditorialOnSecondaryContainer else EditorialSuccessGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = unit.modelName,
                                style = MaterialTheme.typography.displayMedium,
                                color = EditorialOnPrimaryContainer
                            )

                            Text(
                                text = "Assigned Driver: ${unit.driverName} • Route: ${unit.originLocation} ➔ ${unit.destinationLocation}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EditorialOnPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Material Load & Fuel Gauges Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Material Weight Card
                    EditorialCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = EditorialCardBg
                    ) {
                        EditorialIssueBadge(text = "CARGO LOAD")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = unit.materialLoad,
                            style = MaterialTheme.typography.titleLarge,
                            color = EditorialOnSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (unit.cargoWeightKg.toFloat() / unit.maxCapacityKg.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = EditorialPrimary,
                            trackColor = EditorialOutlineVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${unit.cargoWeightKg / 1000}T / ${unit.maxCapacityKg / 1000}T Cap",
                            style = MaterialTheme.typography.labelSmall,
                            color = EditorialOnSurfaceVariant
                        )
                    }

                    // Fuel & AdBlue Card
                    EditorialCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = EditorialCardBg
                    ) {
                        EditorialIssueBadge(text = "RESERVOIR")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fuel ${unit.fuelPercent}%",
                            style = MaterialTheme.typography.titleLarge,
                            color = EditorialOnSurface
                        )
                        Text(
                            text = "AdBlue ${unit.adBluePercent}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EditorialSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Flow: ${unit.fuelFlowL100km} L/100km",
                            style = MaterialTheme.typography.labelSmall,
                            color = EditorialPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // TPMS Axle Diagram Component
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    val psiList = unit.tpmsPressuresPsi.split(",")
                        .mapNotNull { it.trim().toIntOrNull() }
                        .ifEmpty { listOf(118, 116, 120, 118, 114, 116) }

                    AxleTpmsDiagram(pressuresPsi = psiList)
                }
            }

            // G-Sensor & Realtime Engine Metrics
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    EditorialCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = EditorialSecondaryContainer,
                        borderColor = EditorialSecondary.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                EditorialIssueBadge(
                                    text = "TELEMETRY LOG",
                                    textColor = EditorialOnSecondaryContainer
                                )
                                Text(
                                    text = "G-Sensor & Engine Health",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EditorialOnSecondaryContainer
                                )
                            }

                            Button(
                                onClick = { viewModel.toggleStealthForUnit(unit) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EditorialPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("toggle_stealth_button")
                            ) {
                                Icon(
                                    imageVector = if (unit.stealthActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (unit.stealthActive) "Disable Stealth" else "Enable Stealth",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TelemetryDetailPill(
                                label = "Engine Temp",
                                value = "${unit.engineTempC}°C",
                                color = EditorialOnSecondaryContainer
                            )
                            TelemetryDetailPill(
                                label = "Idle Duration",
                                value = "${unit.idleMinutes} mins",
                                color = EditorialOnSecondaryContainer
                            )
                            TelemetryDetailPill(
                                label = "Eco Score",
                                value = "${unit.ecoScore}/100",
                                color = EditorialOnSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryDetailPill(
    label: String,
    value: String,
    color: Color
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
