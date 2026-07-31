package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.StealthConfigEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FleetViewModel

@Composable
fun StealthConfigScreen(
    viewModel: FleetViewModel
) {
    val stealthConfig by viewModel.stealthConfigState.collectAsState()
    val config = stealthConfig ?: StealthConfigEntity()

    var globalStealth by remember(config) { mutableStateOf(config.globalStealthEnabled) }
    var privacyHours by remember(config) { mutableStateOf(config.privacyHoursEnabled) }
    var silentAlerts by remember(config) { mutableStateOf(config.silentAlertsActive) }
    var maskPublicFeed by remember(config) { mutableStateOf(config.maskLocationInPublicFeed) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            EditorialTopHeader(
                title = "Stealth Protocol Matrix",
                issueTag = "ISSUE NO. 42 • SPECTRE ENCRYPTION",
                subtitle = "Configure cryptographic location obfuscation and silent panic feeds."
            )
        }

        // Hero Stealth Status Banner
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (globalStealth) EditorialSecondaryContainer else EditorialCardBg)
                        .border(
                            1.dp,
                            if (globalStealth) EditorialSecondary else EditorialOutlineVariant,
                            RoundedCornerShape(32.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditorialIssueBadge(text = "CRYPTOGRAPHIC PROTOCOL")

                            Switch(
                                checked = globalStealth,
                                onCheckedChange = {
                                    globalStealth = it
                                    viewModel.updateStealthConfig(config.copy(globalStealthEnabled = it))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = EditorialSecondary,
                                    uncheckedThumbColor = EditorialOutline,
                                    uncheckedTrackColor = EditorialCardBg
                                ),
                                modifier = Modifier.testTag("global_stealth_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (globalStealth) "Ghost Protocol Engaged" else "Public Telematics Active",
                            style = MaterialTheme.typography.displaySmall,
                            color = EditorialOnSurface
                        )

                        Text(
                            text = if (globalStealth)
                                "GPS location vectors obfuscated with +2.4km noise jitter on external API feeds."
                            else
                                "Broadcasting standard unmasked coordinates on public logistics API.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EditorialOnSurfaceVariant
                        )
                    }
                }
            }
        }

        // Encryption Key Manager & Security Hardware Status
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
                        Column {
                            EditorialIssueBadge(
                                text = "HARDWARE CRYPTO MODULE",
                                textColor = EditorialOnPrimaryContainer
                            )
                            Text(
                                text = "RSA-4096 Key Pair",
                                style = MaterialTheme.typography.titleLarge,
                                color = EditorialOnPrimaryContainer
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = EditorialOnPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Status: ${config.cryptoKeyStatus}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EditorialOnPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Keys stored inside secure enclave telematics hardware unit.",
                        style = MaterialTheme.typography.labelSmall,
                        color = EditorialOnPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Stealth Options Settings List
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                EditorialIssueBadge(text = "AUTOMATED PRIVACY SCHEDULER")
                Spacer(modifier = Modifier.height(8.dp))

                StealthOptionToggleRow(
                    title = "Night Transit Privacy Hours",
                    description = "Auto-enable Ghost Stealth between 22:00 PM and 06:00 AM.",
                    checked = privacyHours,
                    onCheckedChange = {
                        privacyHours = it
                        viewModel.updateStealthConfig(config.copy(privacyHoursEnabled = it))
                    },
                    testTag = "privacy_hours_toggle"
                )

                Spacer(modifier = Modifier.height(8.dp))

                StealthOptionToggleRow(
                    title = "Silent Alert Dispatch",
                    description = "Send high-priority fuel theft alerts directly to central dispatch without triggering truck cabin horn.",
                    checked = silentAlerts,
                    onCheckedChange = {
                        silentAlerts = it
                        viewModel.updateStealthConfig(config.copy(silentAlertsActive = it))
                    },
                    testTag = "silent_alerts_toggle"
                )

                Spacer(modifier = Modifier.height(8.dp))

                StealthOptionToggleRow(
                    title = "Mask Public Vahan Feed",
                    description = "Hide vehicle registration RJ-14-GB-9921 from public tracking portals.",
                    checked = maskPublicFeed,
                    onCheckedChange = {
                        maskPublicFeed = it
                        viewModel.updateStealthConfig(config.copy(maskLocationInPublicFeed = it))
                    },
                    testTag = "mask_public_toggle"
                )
            }
        }
    }
}

@Composable
private fun StealthOptionToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(EditorialCardBg)
            .border(1.dp, EditorialOutlineVariant, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = EditorialOnSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EditorialOnSurfaceVariant
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EditorialPrimary,
                    uncheckedThumbColor = EditorialOutline,
                    uncheckedTrackColor = EditorialCardBg
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
