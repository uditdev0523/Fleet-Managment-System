package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun EditorialIssueBadge(
    text: String = "ISSUE NO. 42 • GHOST-FLEET TELEMETRY",
    modifier: Modifier = Modifier,
    textColor: Color = EditorialPrimary
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        ),
        color = textColor,
        modifier = modifier
    )
}

@Composable
fun EditorialTopHeader(
    title: String,
    issueTag: String = "ISSUE NO. 42 • JUNE 2026",
    subtitle: String? = null,
    onSearchClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EditorialBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left menu pill
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(EditorialSecondaryContainer)
                    .clickable { onMenuClick?.invoke() }
                    .testTag("menu_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = EditorialOnSecondaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Right actions pill
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onSearchClick != null) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EditorialPrimaryContainer)
                            .clickable { onSearchClick() }
                            .testTag("search_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = EditorialOnPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Profile Avatar Pill
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .background(EditorialPrimary)
                        .shadow(2.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GF",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        EditorialIssueBadge(text = issueTag)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium,
            color = EditorialOnSurface,
            lineHeight = 36.sp
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = EditorialOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun EditorialCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = EditorialCardBg,
    borderColor: Color = EditorialOutlineVariant,
    cornerRadius: Dp = 28.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .padding(18.dp),
        content = content
    )
}

@Composable
fun EditorialStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color = EditorialPrimaryContainer,
    contentColor: Color = EditorialOnPrimaryContainer,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(containerColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(26.dp)
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.displaySmall,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun FleetRadarMapView(
    unitsCount: Int = 4,
    stealthCount: Int = 1,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF231E21))
            .border(1.dp, EditorialPrimary.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = minOf(size.width, size.height) * 0.42f

            // Grid lines
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawCircle(
                color = Color(0xFF8F4C38).copy(alpha = 0.25f),
                radius = maxRadius * 0.35f,
                center = center,
                style = Stroke(width = 1.5f, pathEffect = dashEffect)
            )
            drawCircle(
                color = Color(0xFF8F4C38).copy(alpha = 0.35f),
                radius = maxRadius * 0.7f,
                center = center,
                style = Stroke(width = 1.5f, pathEffect = dashEffect)
            )
            drawCircle(
                color = Color(0xFF8F4C38).copy(alpha = 0.5f),
                radius = maxRadius,
                center = center,
                style = Stroke(width = 2f)
            )

            // Crosshairs
            drawLine(
                color = Color(0xFF8F4C38).copy(alpha = 0.3f),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1.5f
            )
            drawLine(
                color = Color(0xFF8F4C38).copy(alpha = 0.3f),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1.5f
            )

            // Radar beam sweep arc
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to Color.Transparent,
                    0.8f to Color(0xFFFFDAD1).copy(alpha = 0.05f),
                    1.0f to Color(0xFFFFDAD1).copy(alpha = 0.35f),
                    center = center
                ),
                startAngle = sweepAngle - 45f,
                sweepAngle = 45f,
                useCenter = true,
                topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                size = Size(maxRadius * 2, maxRadius * 2)
            )

            // Vehicle Unit Blips
            val blip1 = Offset(center.x + maxRadius * 0.4f, center.y - maxRadius * 0.3f) // Active GF-309
            val blip2 = Offset(center.x - maxRadius * 0.5f, center.y + maxRadius * 0.2f) // Stealth GF-512
            val blip3 = Offset(center.x + maxRadius * 0.2f, center.y + maxRadius * 0.6f) // Active GF-104
            val blip4 = Offset(center.x - maxRadius * 0.2f, center.y - maxRadius * 0.5f) // Maintenance

            // Active Blip GF-309
            drawCircle(color = Color(0xFFFFDAD1), radius = 6f, center = blip1)
            drawCircle(color = Color(0xFF8F4C38).copy(alpha = 0.4f), radius = 14f, center = blip1, style = Stroke(width = 2f))

            // Stealth Blip GF-512 (Lavender ring)
            drawCircle(color = Color(0xFFE8DEF8), radius = 6f, center = blip2)
            drawCircle(color = Color(0xFFD0BCFF).copy(alpha = 0.6f), radius = 22f, center = blip2, style = Stroke(width = 2f, pathEffect = dashEffect))

            // Active Blip GF-104
            drawCircle(color = Color(0xFFFFDAD1), radius = 5f, center = blip3)

            // Maintenance GF-808
            drawCircle(color = Color(0xFFF9DEDC), radius = 5f, center = blip4)
        }

        // Radar Overlay Labels
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFDAD1))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GHOST RADAR • NH-48 / NORTH SECTOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
            Text(
                text = "27.2046° N, 76.8491° E",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "ACTIVE: $unitsCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFDAD1),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "STEALTH: $stealthCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE8DEF8),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AxleTpmsDiagram(
    pressuresPsi: List<Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
            Column {
                EditorialIssueBadge(text = "AXLE TPMS MONITOR")
                Text(
                    text = "Multi-Axle Tire Pressure",
                    style = MaterialTheme.typography.titleMedium,
                    color = EditorialOnSurface
                )
            }
            Text(
                text = "NORMAL: 115-122 PSI",
                style = MaterialTheme.typography.labelSmall,
                color = EditorialPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Truck Chassis Layout with 6 Tires
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tire1 = pressuresPsi.getOrElse(0) { 118 }
            val tire2 = pressuresPsi.getOrElse(1) { 116 }
            val tire3 = pressuresPsi.getOrElse(2) { 120 }
            val tire4 = pressuresPsi.getOrElse(3) { 118 }
            val tire5 = pressuresPsi.getOrElse(4) { 114 }
            val tire6 = pressuresPsi.getOrElse(5) { 116 }

            // Front Axle
            TirePairItem(axleLabel = "Front Axle", leftPsi = tire1, rightPsi = tire2)

            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = EditorialOutline
            )

            // Middle Axle
            TirePairItem(axleLabel = "Mid Heavy", leftPsi = tire3, rightPsi = tire4)

            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = EditorialOutline
            )

            // Rear Heavy Axle
            TirePairItem(axleLabel = "Rear Dual", leftPsi = tire5, rightPsi = tire6)
        }
    }
}

@Composable
private fun TirePairItem(
    axleLabel: String,
    leftPsi: Int,
    rightPsi: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = axleLabel,
            style = MaterialTheme.typography.labelSmall,
            color = EditorialOnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TirePill(psi = leftPsi)
            TirePill(psi = rightPsi)
        }
    }
}

@Composable
private fun TirePill(psi: Int) {
    val isWarning = psi < 112
    val bg = if (isWarning) EditorialAlertContainer else EditorialPrimaryContainer
    val textColor = if (isWarning) EditorialAlertRed else EditorialOnPrimaryContainer

    Box(
        modifier = Modifier
            .width(36.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Compress,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "$psi",
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "PSI",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = textColor
            )
        }
    }
}
