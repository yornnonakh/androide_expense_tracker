package com.example.fintrack.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Emerald40,
    onPrimary = LightSurface,
    secondary = Slate40,
    background = LightBackground,
    onBackground = Navy20,
    surface = LightSurface,
    onSurface = Navy20,
    surfaceVariant = ColorTokens.LightSurfaceVariant,
    onSurfaceVariant = Slate40,
    error = ExpenseColor,
)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald80,
    onPrimary = Navy20,
    secondary = Slate80,
    background = DarkBackground,
    onBackground = Navy90,
    surface = DarkSurface,
    onSurface = Navy90,
    surfaceVariant = ColorTokens.DarkSurfaceVariant,
    onSurfaceVariant = Slate80,
    error = ColorTokens.DarkError,
)

private object ColorTokens {
    val LightSurfaceVariant = Color(0xFFE7EEF2)
    val DarkSurfaceVariant = Color(0xFF24313C)
    val DarkError = Color(0xFFFFB4AB)
}

@Composable
fun FinTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = FinTrackTypography,
        shapes = FinTrackShapes,
        content = content,
    )
}
