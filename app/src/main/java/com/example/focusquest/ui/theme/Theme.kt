package com.example.focusquest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = FocusViolet,
    onPrimary = Color.White,
    primaryContainer = FocusSoftViolet,
    onPrimaryContainer = Color(0xFF32105D),
    secondary = Color(0xFF255FCB),
    onSecondary = Color.White,
    secondaryContainer = FocusSoftBlue,
    onSecondaryContainer = Color(0xFF102C61),
    tertiary = FocusPink,
    onTertiary = Color(0xFF390018),
    tertiaryContainer = FocusSoftPink,
    onTertiaryContainer = Color(0xFF3F001B),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = FocusCanvas,
    onBackground = FocusInk,
    surface = Color(0xFFFFFCFF),
    onSurface = FocusInk,
    surfaceVariant = Color(0xFFEEE9F4),
    onSurfaceVariant = Color(0xFF4C4655),
    outline = Color(0xFF7A7281),
    outlineVariant = Color(0xFFD0C6D8)
)

@Composable
fun FocusQuestTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
