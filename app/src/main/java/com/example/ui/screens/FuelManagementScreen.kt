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
import com.example.data.local.FuelLogEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FleetViewModel

@Composable
fun FuelManagementScreen(
    viewModel: FleetViewModel
) {
    val fuelLogs by viewModel.fuelLogsState.collectAsState()
    val units by viewModel.unitsState.collectAsState()

    var selectedUnitId by remember { mutableStateOf("GF-309") }
    var litersInput by remember { mutableStateOf("") }
    var costInput by remember { mutableStateOf("") }
    var odometerInput by remember { mutableStateOf("") }
    var stationInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    var searchQuery by remember { mutableStateOf("") }
    var showFormSuccessMsg by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Summary calculations
    val totalSpend = fuelLogs.sumOf { it.totalCostRs }
    val totalLiters = fuelLogs.sumOf { it.liters }
    val avgCostPerLiter = if (totalLiters > 0) totalSpend / totalLiters else 0.0

    val filteredLogs = fuelLogs.filter { log ->
        searchQuery.isBlank() ||
                log.unitId.contains(searchQuery, ignoreCase = true) ||
                log.stationName.contains(searchQuery, ignoreCase = true) ||
                log.notes.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Editorial Top Header
        item {
            EditorialTopHeader(
                title = "Fuel Management",
                issueTag = "ISSUE NO. 42 • DIESEL TELEMATICS",
                subtitle = "Log refill receipts, monitor cost rates, and review consumption history."
            )
        }

        // Summary Analytics Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditorialStatCard(
                    title = "Total Spend",
                    value = "₹${java.lang.String.format("%.0f", totalSpend)}",
                    subtitle = "${fuelLogs.size} Refill Logs",
                    icon = Icons.Default.AttachMoney,
                    containerColor = EditorialPrimaryContainer,
                    contentColor = EditorialOnPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )

                EditorialStatCard(
                    title = "Total Volume",
                    value = "${java.lang.String.format("%.0f", totalLiters)} L",
                    subtitle = "Avg ₹${java.lang.String.format("%.1f", avgCostPerLiter)}/L",
                    icon = Icons.Default.LocalGasStation,
                    containerColor = EditorialSecondaryContainer,
                    contentColor = EditorialOnSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Input Form Section
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
                            EditorialIssueBadge(text = "NEW FUEL REFILL LOG")
                            Icon(
                                imageVector = Icons.Default.LocalGasStation,
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

                        // Unit selector chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val availableUnits = if (units.isNotEmpty()) units.map { it.id } else listOf("GF-309", "GF-512", "GF-104", "GF-808")
                            items(availableUnits) { unitId ->
                                val isSelected = unitId == selectedUnitId
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) EditorialPrimary else EditorialSurface)
                                        .border(
                                            1.dp,
                                            if (isSelected) EditorialPrimary else EditorialOutlineVariant,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { selectedUnitId = unitId }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("select_form_unit_$unitId")
                                ) {
                                    Text(
                                        text = unitId,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else EditorialOnSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Input fields: Liters & Total Cost
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = litersInput,
                                onValueChange = { litersInput = it },
                                label = { Text("Volume (Liters)") },
                                placeholder = { Text("e.g. 180") },
                                leadingIcon = { Icon(Icons.Default.WaterDrop, contentDescription = null, tint = EditorialPrimary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("fuel_liters_input"),
                                shape = RoundedCornerShape(18.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = costInput,
                                onValueChange = { costInput = it },
                                label = { Text("Cost (₹)") },
                                placeholder = { Text("e.g. 17100") },
                                leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = EditorialPrimary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("fuel_cost_input"),
                                shape = RoundedCornerShape(18.dp),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Input field: Odometer
                        OutlinedTextField(
                            value = odometerInput,
                            onValueChange = { odometerInput = it },
                            label = { Text("Odometer Reading (Km)") },
                            placeholder = { Text("e.g. 84500") },
                            leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, tint = EditorialPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fuel_odometer_input"),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Input field: Fuel Station Location
                        OutlinedTextField(
                            value = stationInput,
                            onValueChange = { stationInput = it },
                            label = { Text("Fuel Station / Depot Location") },
                            placeholder = { Text("e.g. Indian Oil Station, NH-48") },
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = EditorialPrimary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fuel_station_input"),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Input field: Notes / Receipt Number
                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Notes / Invoice Ref (Optional)") },
                            placeholder = { Text("e.g. Full tank refill before journey") },
                            leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = EditorialPrimary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fuel_notes_input"),
                            shape = RoundedCornerShape(18.dp),
                            singleLine = true
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = EditorialAlertRed
                            )
                        }

                        AnimatedVisibility(visible = showFormSuccessMsg) {
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
                                            text = "Fuel entry successfully recorded!",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = EditorialSuccessGreen
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val litersVal = litersInput.toDoubleOrNull()
                                val costVal = costInput.toDoubleOrNull()
                                val odoVal = odometerInput.toDoubleOrNull() ?: 0.0

                                if (litersVal == null || litersVal <= 0) {
                                    errorMessage = "Please enter a valid fuel volume in liters."
                                    return@Button
                                }
                                if (costVal == null || costVal <= 0) {
                                    errorMessage = "Please enter a valid cost amount."
                                    return@Button
                                }

                                errorMessage = null
                                viewModel.addFuelLog(
                                    unitId = selectedUnitId,
                                    liters = litersVal,
                                    totalCostRs = costVal,
                                    odometerKm = odoVal,
                                    stationName = stationInput,
                                    notes = notesInput
                                )

                                // Clear fields
                                litersInput = ""
                                costInput = ""
                                odometerInput = ""
                                stationInput = ""
                                notesInput = ""
                                showFormSuccessMsg = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EditorialPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("add_fuel_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LOG REFILL ENTRY",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Fuel History Title & Search Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                EditorialIssueBadge(text = "FUEL REFILL LOG HISTORY")
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search logs by Unit or Station...", style = MaterialTheme.typography.bodyMedium) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EditorialPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("filter_fuel_input"),
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
        }

        // History Log List
        if (filteredLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No fuel logs found matching query.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditorialOnSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredLogs, key = { it.id }) { log ->
                FuelLogCardItem(
                    log = log,
                    onDelete = { viewModel.deleteFuelLog(log.id) }
                )
            }
        }
    }
}

@Composable
private fun FuelLogCardItem(
    log: FuelLogEntity,
    onDelete: () -> Unit
) {
    val ratePerLiter = if (log.liters > 0) log.totalCostRs / log.liters else 0.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(EditorialCardBg)
            .border(1.dp, EditorialOutlineVariant, RoundedCornerShape(26.dp))
            .padding(18.dp)
            .testTag("fuel_log_card_${log.id}")
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
                            text = log.unitId,
                            style = MaterialTheme.typography.titleMedium,
                            color = EditorialOnPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = log.dateString,
                        style = MaterialTheme.typography.labelSmall,
                        color = EditorialOnSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_fuel_log_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Log",
                        tint = EditorialAlertRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${log.liters} Liters",
                        style = MaterialTheme.typography.titleLarge,
                        color = EditorialOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rate: ₹${java.lang.String.format("%.1f", ratePerLiter)} / Liter",
                        style = MaterialTheme.typography.labelSmall,
                        color = EditorialPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${java.lang.String.format("%.0f", log.totalCostRs)}",
                        style = MaterialTheme.typography.displaySmall,
                        color = EditorialPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    if (log.odometerKm > 0) {
                        Text(
                            text = "Odo: ${log.odometerKm.toInt()} Km",
                            style = MaterialTheme.typography.labelSmall,
                            color = EditorialOnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = EditorialOnSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = log.stationName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialOnSurface
                )
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ref: ${log.notes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = EditorialOnSurfaceVariant
                )
            }
        }
    }
}
