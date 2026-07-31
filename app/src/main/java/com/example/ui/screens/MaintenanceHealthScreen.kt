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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MaintenanceTaskEntity
import com.example.data.local.MaintenanceThresholdEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FleetViewModel

@Composable
fun MaintenanceHealthScreen(
    viewModel: FleetViewModel
) {
    val tasks by viewModel.maintenanceTasksState.collectAsState()
    val units by viewModel.unitsState.collectAsState()
    val thresholds by viewModel.maintenanceThresholdsState.collectAsState()

    val overdueTasksCount = tasks.count { it.status == "OVERDUE" }
    val triggeredAlertsCount = thresholds.count { it.isAlertTriggered || (it.targetMileageKm - it.currentMileageKm <= 0 && !it.isServiced) }

    // State for creating new threshold rule
    var selectedUnitId by remember { mutableStateOf("GF-309") }
    var componentInput by remember { mutableStateOf("") }
    var targetMileageInput by remember { mutableStateOf("") }
    var currentMileageInput by remember { mutableStateOf("") }
    var warningLeadInput by remember { mutableStateOf("500") }

    var formError by remember { mutableStateOf<String?>(null) }
    var showSuccessBanner by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Editorial Top Header
        item {
            EditorialTopHeader(
                title = "Maintenance & Health",
                issueTag = "ISSUE NO. 42 • MECHANICAL DIAGNOSTICS",
                subtitle = "Preventative component wear, mileage threshold alerts, & service scheduling."
            )
        }

        // Hero Fleet Health Stat
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditorialStatCard(
                    title = "System Health",
                    value = "92.8%",
                    subtitle = "${units.size - overdueTasksCount}/${units.size} Units Operational",
                    icon = Icons.Default.HealthAndSafety,
                    containerColor = EditorialPrimaryContainer,
                    contentColor = EditorialOnPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                EditorialStatCard(
                    title = "Mileage Alerts",
                    value = "$triggeredAlertsCount Triggered",
                    subtitle = "${thresholds.size} Rules Tracked",
                    icon = Icons.Default.NotificationsActive,
                    containerColor = if (triggeredAlertsCount > 0) EditorialAlertContainer else EditorialSecondaryContainer,
                    contentColor = if (triggeredAlertsCount > 0) EditorialAlertRed else EditorialOnSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // New Mileage Threshold Rule Configuration Form
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(EditorialCardBg)
                        .border(1.dp, EditorialOutlineVariant, RoundedCornerShape(28.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditorialIssueBadge(text = "NEW MILEAGE THRESHOLD RULE")
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = EditorialPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Select Heavy Unit",
                            style = MaterialTheme.typography.labelLarge,
                            color = EditorialOnSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Unit Selector Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val availableUnits = if (units.isNotEmpty()) units.map { it.id } else listOf("GF-309", "GF-512", "GF-104", "GF-808")
                            items(availableUnits) { uId ->
                                val isSelected = uId == selectedUnitId
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) EditorialPrimary else EditorialSurface)
                                        .border(
                                            1.dp,
                                            if (isSelected) EditorialPrimary else EditorialOutlineVariant,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { selectedUnitId = uId }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("threshold_unit_chip_$uId")
                                ) {
                                    Text(
                                        text = uId,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else EditorialOnSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Component Name Field
                        OutlinedTextField(
                            value = componentInput,
                            onValueChange = { componentInput = it },
                            label = { Text("Component / Maintenance Task Name") },
                            placeholder = { Text("e.g. Engine Oil & Filter Service") },
                            leadingIcon = { Icon(Icons.Default.Build, contentDescription = null, tint = EditorialPrimary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("threshold_component_input"),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Target Mileage & Current Mileage
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = targetMileageInput,
                                onValueChange = { targetMileageInput = it },
                                label = { Text("Target Threshold (Km)") },
                                placeholder = { Text("e.g. 85000") },
                                leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = EditorialPrimary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("threshold_target_input"),
                                shape = RoundedCornerShape(18.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currentMileageInput,
                                onValueChange = { currentMileageInput = it },
                                label = { Text("Current Odometer (Km)") },
                                placeholder = { Text("e.g. 84250") },
                                leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, tint = EditorialPrimary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("threshold_current_input"),
                                shape = RoundedCornerShape(18.dp),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Warning Lead Km
                        OutlinedTextField(
                            value = warningLeadInput,
                            onValueChange = { warningLeadInput = it },
                            label = { Text("Alert Warning Trigger Distance (Km before target)") },
                            placeholder = { Text("e.g. 500") },
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = EditorialPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("threshold_warning_lead_input"),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        if (formError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = formError ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = EditorialAlertRed
                            )
                        }

                        AnimatedVisibility(visible = showSuccessBanner) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = EditorialSuccessContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EditorialSuccessGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Maintenance threshold rule saved in Room DB!",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = EditorialSuccessGreen
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val targetVal = targetMileageInput.toDoubleOrNull()
                                val currentVal = currentMileageInput.toDoubleOrNull()
                                val warningVal = warningLeadInput.toDoubleOrNull() ?: 500.0

                                if (componentInput.isBlank()) {
                                    formError = "Please enter a component or task name."
                                    return@Button
                                }
                                if (targetVal == null || targetVal <= 0) {
                                    formError = "Please enter a valid target mileage threshold."
                                    return@Button
                                }
                                if (currentVal == null || currentVal < 0) {
                                    formError = "Please enter a valid current odometer reading."
                                    return@Button
                                }

                                formError = null
                                viewModel.addMaintenanceThreshold(
                                    unitId = selectedUnitId,
                                    componentName = componentInput,
                                    targetMileageKm = targetVal,
                                    currentMileageKm = currentVal,
                                    warningLeadKm = warningVal
                                )

                                componentInput = ""
                                targetMileageInput = ""
                                currentMileageInput = ""
                                showSuccessBanner = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EditorialPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("add_threshold_button")
                        ) {
                            Icon(Icons.Default.AddAlert, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SAVE MILEAGE THRESHOLD RULE",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Mileage Threshold Rules Section Header
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("mileage_threshold_section")
            ) {
                EditorialIssueBadge(text = "ROOM LOCAL MILEAGE THRESHOLD RULES & ALERTS")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Monitors local vehicle odometer progression against scheduled maintenance targets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EditorialOnSurfaceVariant
                )
            }
        }

        // List of Mileage Threshold Rules from Room
        if (thresholds.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No mileage threshold rules found in database.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditorialOnSurfaceVariant
                    )
                }
            }
        } else {
            items(thresholds, key = { it.id }) { threshold ->
                MileageThresholdCard(
                    threshold = threshold,
                    onUpdateMileage = { deltaKm ->
                        viewModel.updateThresholdMileage(
                            threshold = threshold,
                            newMileageKm = threshold.currentMileageKm + deltaKm
                        )
                    },
                    onMarkServiced = {
                        viewModel.markThresholdServiced(threshold)
                    },
                    onDelete = {
                        viewModel.deleteMaintenanceThreshold(threshold.id)
                    }
                )
            }
        }

        // Component Health Overview
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                EditorialIssueBadge(text = "COMPONENT SCHEMATIC HEALTH")
                Spacer(modifier = Modifier.height(8.dp))

                EditorialCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = EditorialCardBg
                ) {
                    ComponentHealthRow(name = "Heavy Hydro-Retarder Brakes", health = 78, isWarning = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    ComponentHealthRow(name = "AdBlue Exhaust SCR Catalyst", health = 88, isWarning = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    ComponentHealthRow(name = "Common Rail Fuel Injectors", health = 95, isWarning = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    ComponentHealthRow(name = "Heavy Duty Air Suspension", health = 91, isWarning = false)
                }
            }
        }

        // Maintenance Timeline List Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                EditorialIssueBadge(text = "SERVICE SCHEDULE TIMELINE")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(tasks, key = { it.id }) { task ->
            MaintenanceTaskCard(task = task)
        }
    }
}

@Composable
private fun MileageThresholdCard(
    threshold: MaintenanceThresholdEntity,
    onUpdateMileage: (Double) -> Unit,
    onMarkServiced: () -> Unit,
    onDelete: () -> Unit
) {
    val remainingKm = threshold.targetMileageKm - threshold.currentMileageKm
    val isOverdue = remainingKm < 0
    val isTriggered = threshold.isAlertTriggered || (remainingKm <= threshold.warningLeadKm && !threshold.isServiced)

    val progress = if (threshold.targetMileageKm > 0) {
        (threshold.currentMileageKm / threshold.targetMileageKm).toFloat().coerceIn(0f, 1f)
    } else 0f

    val badgeColor = when {
        threshold.isServiced -> EditorialSuccessGreen
        isOverdue || isTriggered -> EditorialAlertRed
        remainingKm <= threshold.warningLeadKm * 2 -> Color(0xFFD97706) // Warning Orange
        else -> EditorialPrimary
    }

    val badgeText = when {
        threshold.isServiced -> "SERVICED"
        isOverdue -> "OVERDUE ALERT!"
        isTriggered -> "THRESHOLD TRIGGERED!"
        remainingKm <= threshold.warningLeadKm * 2 -> "DUE SOON"
        else -> "HEALTHY"
    }

    val containerColor = when {
        isTriggered && !threshold.isServiced -> EditorialAlertContainer
        threshold.isServiced -> EditorialSuccessContainer.copy(alpha = 0.5f)
        else -> EditorialCardBg
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(containerColor)
            .border(
                1.dp,
                if (isTriggered && !threshold.isServiced) EditorialAlertRed.copy(alpha = 0.5f) else EditorialOutlineVariant,
                RoundedCornerShape(26.dp)
            )
            .padding(18.dp)
            .testTag("threshold_card_${threshold.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = EditorialPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "UNIT ${threshold.unitId}",
                            style = MaterialTheme.typography.titleMedium,
                            color = EditorialOnPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        color = badgeColor,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_threshold_button_${threshold.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Rule",
                        tint = EditorialAlertRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = threshold.componentName,
                style = MaterialTheme.typography.titleLarge,
                color = EditorialOnSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mileage progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = badgeColor,
                trackColor = EditorialOutlineVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current: ${threshold.currentMileageKm.toInt()} Km",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditorialOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Target: ${threshold.targetMileageKm.toInt()} Km",
                        style = MaterialTheme.typography.labelSmall,
                        color = EditorialOnSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    val remainingText = if (isOverdue) {
                        "${(-remainingKm).toInt()} Km Overdue"
                    } else {
                        "${remainingKm.toInt()} Km Remaining"
                    }
                    Text(
                        text = remainingText,
                        style = MaterialTheme.typography.titleMedium,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lead Alert: ${threshold.warningLeadKm.toInt()} Km",
                        style = MaterialTheme.typography.labelSmall,
                        color = EditorialOnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mileage Stepper Controls & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Steppers to test threshold triggers live
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Simulate:",
                        style = MaterialTheme.typography.labelSmall,
                        color = EditorialOnSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { onUpdateMileage(200.0) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("add_200km_button_${threshold.id}")
                    ) {
                        Text("+200 Km", style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = { onUpdateMileage(500.0) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("add_500km_button_${threshold.id}")
                    ) {
                        Text("+500 Km", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (!threshold.isServiced) {
                    Button(
                        onClick = onMarkServiced,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EditorialSuccessGreen,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("mark_serviced_button_${threshold.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SERVICED",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentHealthRow(
    name: String,
    health: Int,
    isWarning: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = EditorialOnSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$health%",
                style = MaterialTheme.typography.labelLarge,
                color = if (isWarning) EditorialAlertRed else EditorialPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { health / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = if (isWarning) EditorialAlertRed else EditorialPrimary,
            trackColor = EditorialOutlineVariant
        )
    }
}

@Composable
private fun MaintenanceTaskCard(
    task: MaintenanceTaskEntity
) {
    val isOverdue = task.status == "OVERDUE"
    val containerColor = if (isOverdue) EditorialAlertContainer else EditorialCardBg
    val textColor = if (isOverdue) EditorialAlertRed else EditorialOnSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .border(
                1.dp,
                if (isOverdue) EditorialAlertRed.copy(alpha = 0.4f) else EditorialOutlineVariant,
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UNIT ${task.unitId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = if (isOverdue) EditorialAlertRed else EditorialPrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = task.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = task.componentName,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due in ${task.dueKm} km",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialOnSurfaceVariant
                )
                Text(
                    text = "Est. Cost: ₹${task.estimatedCostRs}",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
