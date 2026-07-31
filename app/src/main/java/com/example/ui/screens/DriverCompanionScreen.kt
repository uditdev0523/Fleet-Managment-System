package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FleetViewModel

@Composable
fun DriverCompanionScreen(
    viewModel: FleetViewModel
) {
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val unit = selectedUnit

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            EditorialTopHeader(
                title = "Driver Companion",
                issueTag = "ISSUE NO. 42 • CABIN TELEMATICS",
                subtitle = "Live highway guidance, axle load balance, & eco-driving metrics."
            )
        }

        if (unit != null) {
            // Live Trip Card
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    EditorialCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = EditorialPrimaryContainer
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditorialIssueBadge(
                                text = "TRIP PROGRESS • EN ROUTE",
                                textColor = EditorialOnPrimaryContainer
                            )

                            Surface(
                                color = EditorialOnPrimaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "ETA: 3h 40m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${unit.originLocation}\n➔ ${unit.destinationLocation}",
                            style = MaterialTheme.typography.titleLarge,
                            color = EditorialOnPrimaryContainer,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Highway: NH-48 Corridor",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EditorialOnPrimaryContainer
                            )
                            Text(
                                text = "Speed: ${unit.speedKmH} km/h",
                                style = MaterialTheme.typography.titleMedium,
                                color = EditorialOnPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Driver Eco Score & Cargo Balance Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EditorialStatCard(
                        title = "Driver Eco Score",
                        value = "${unit.ecoScore}/100",
                        subtitle = "Rajesh Sharma",
                        icon = Icons.Default.ThumbUp,
                        containerColor = EditorialSecondaryContainer,
                        contentColor = EditorialOnSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )

                    EditorialStatCard(
                        title = "Axle Weight",
                        value = "${unit.cargoWeightKg / 1000}T",
                        subtitle = "Balanced (98%)",
                        icon = Icons.Default.Balance,
                        containerColor = EditorialTertiaryContainer,
                        contentColor = EditorialOnSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Realtime Cabin Telemetry Logs
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    EditorialIssueBadge(text = "LIVE CABIN FEED")
                    Spacer(modifier = Modifier.height(8.dp))

                    CompanionLogItem(
                        time = "12:14 PM",
                        title = "Engine Throttle Smooth",
                        message = "Maintaining steady 1,400 RPM on NH-48 highway section."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CompanionLogItem(
                        time = "11:50 AM",
                        title = "AdBlue Exhaust Clean",
                        message = "SCR Catalyst temperature optimal at 280°C."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CompanionLogItem(
                        time = "11:20 AM",
                        title = "Fuel Reservoir Check",
                        message = "Flow rate steady at 14.2 L/100km. No siphon anomalies."
                    )
                }
            }
        }
    }
}

@Composable
private fun CompanionLogItem(
    time: String,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(EditorialCardBg)
            .border(1.dp, EditorialOutlineVariant, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = EditorialOnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = EditorialPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = EditorialOnSurfaceVariant
            )
        }
    }
}
