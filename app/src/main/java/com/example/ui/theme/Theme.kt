package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SovanColorScheme = lightColorScheme(
    primary = SovanUserBubble,
    onPrimary = SovanText,
    primaryContainer = SovanBotBubble,
    onPrimaryContainer = SovanText,
    secondary = SovanBotBubble,
    onSecondary = SovanText,
    secondaryContainer = SovanPill,
    onSecondaryContainer = SovanText,
    tertiary = SovanAccent,
    onTertiary = SovanSurface,
    background = SovanBackground,
    onBackground = SovanText,
    surface = SovanSurface,
    onSurface = SovanText,
    surfaceVariant = SovanPill,
    onSurfaceVariant = SovanText,
    outline = SovanBorder,
    outlineVariant = SovanBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Sovan uses the strict custom design palette requested by the user
    MaterialTheme(
        colorScheme = SovanColorScheme,
        typography = Typography,
        content = content
    )
}
