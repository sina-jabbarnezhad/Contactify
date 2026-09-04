package com.contactify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ContactifyBackground = Color(0xFF08090C)
val ContactifyCard = Color(0xFF111318)
val ContactifyInput = Color(0xFF0C0E12)
val ContactifyGreen = Color(0xFFB8FF4D)
val ContactifyRed = Color(0xFFE53935)
val ContactifyBorder = Color(0x17FFFFFF)
val ContactifySecondary = Color(0x99FFFFFF)

private val DarkColors = darkColorScheme(
    primary = ContactifyGreen,
    onPrimary = Color(0xFF111318),
    background = ContactifyBackground,
    surface = ContactifyCard,
    surfaceVariant = ContactifyInput,
    onBackground = Color.White,
    onSurface = Color.White,
    error = ContactifyRed
)

@Composable
fun ContactifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(),
        content = content
    )
}
