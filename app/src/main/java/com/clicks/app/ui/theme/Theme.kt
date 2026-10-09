package com.clicks.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClicksDarkColorScheme = darkColorScheme(
    primary = ClicksGreenPrimary,
    onPrimary = ClicksDarkBgTop,

    primaryContainer = ClicksGreenDark,
    onPrimaryContainer = ClicksTextPrimary,

    background = ClicksDarkBgTop,
    onBackground = ClicksTextPrimary,

    surface = ClicksInputBg,
    onSurface = ClicksTextPrimary,

    surfaceVariant = ClicksInputBg,
    onSurfaceVariant = ClicksTextSecondary,

    outline = ClicksInputBorder,
    outlineVariant = ClicksInputFocusedBorder,

    error = Error,
    onError = Color.White
)

@Composable
fun ClicksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClicksDarkColorScheme,
        typography = ClicksTypography,
        content = content
    )
}
