package com.example.devmock.ui.theme

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
    primary = DevmockAccent,
    onPrimary = Color(0xFF0F172A),
    secondary = DevmockSecondary,
    onSecondary = Color.White,
    tertiary = DevmockSecondary,
    background = DevmockDarkBackground,
    surface = DevmockDarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = DevmockDarkSurfaceVariant,
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = Color(0xFFF87171),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DevmockPrimary,
    onPrimary = Color.White,
    secondary = DevmockSecondary,
    onSecondary = Color.White,
    tertiary = DevmockAccent,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B)
)

object ThemeConfig {
    var isDarkMode by mutableStateOf(false)
}

@Composable
fun isAppInDarkTheme(): Boolean = ThemeConfig.isDarkMode

@Composable
fun DevmockTheme(
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