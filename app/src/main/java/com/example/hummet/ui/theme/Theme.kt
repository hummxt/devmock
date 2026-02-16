package com.example.hummet.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HummetAccent,
    onPrimary = HummetPrimary,
    secondary = HummetSecondary,
    onSecondary = HummetPrimary,
    tertiary = HummetSecondary,
    background = HummetDarkBackground,
    surface = HummetDarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = HummetPrimary,
    onSurfaceVariant = Color.White.copy(alpha = 0.7f)
)

private val LightColorScheme = lightColorScheme(
    primary = HummetPrimary,
    onPrimary = Color.White,
    secondary = HummetSecondary,
    onSecondary = HummetPrimary,
    tertiary = HummetAccent,
    background = Color.White,
    surface = Color.White,
    onBackground = HummetPrimary,
    onSurface = HummetPrimary,
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = HummetPrimary.copy(alpha = 0.7f)
)

object ThemeConfig {
    var isDarkMode by mutableStateOf(false)
}

@Composable
fun isAppInDarkTheme(): Boolean = ThemeConfig.isDarkMode

@Composable
fun HummetTheme(
    darkTheme: Boolean = isAppInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}