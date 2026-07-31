package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EditorialLightColorScheme = lightColorScheme(
    primary = EditorialPrimary,
    onPrimary = Color.White,
    primaryContainer = EditorialPrimaryContainer,
    onPrimaryContainer = EditorialOnPrimaryContainer,
    secondary = EditorialSecondary,
    onSecondary = Color.White,
    secondaryContainer = EditorialSecondaryContainer,
    onSecondaryContainer = EditorialOnSecondaryContainer,
    tertiary = EditorialTertiary,
    tertiaryContainer = EditorialTertiaryContainer,
    background = EditorialBackground,
    onBackground = EditorialOnSurface,
    surface = EditorialSurface,
    onSurface = EditorialOnSurface,
    surfaceVariant = EditorialSurfaceVariant,
    onSurfaceVariant = EditorialOnSurfaceVariant,
    outline = EditorialOutline,
    outlineVariant = EditorialOutlineVariant
)

private val EditorialDarkColorScheme = darkColorScheme(
    primary = EditorialPrimaryContainer,
    onPrimary = EditorialOnPrimaryContainer,
    primaryContainer = EditorialPrimary,
    onPrimaryContainer = Color.White,
    secondary = EditorialSecondaryContainer,
    onSecondary = EditorialOnSecondaryContainer,
    secondaryContainer = EditorialSecondary,
    onSecondaryContainer = Color.White,
    background = EditorialStealthDark,
    onBackground = Color(0xFFFDF8F6),
    surface = Color(0xFF272325),
    onSurface = Color(0xFFFDF8F6),
    surfaceVariant = Color(0xFF383234),
    onSurfaceVariant = Color(0xFFD4C3BF)
)

@Composable
fun GhostFleetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EditorialDarkColorScheme else EditorialLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GhostFleetTheme(darkTheme = darkTheme, content = content)
}

