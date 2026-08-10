package com.vault39.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Hatsune Miku palette ──────────────────────────────────────────────────
val MikuTeal        = Color(0xFF39C5BB)   // iconic teal
val MikuTealDark    = Color(0xFF1A9E95)
val MikuTealLight   = Color(0xFF72E8E1)
val MikuNavy        = Color(0xFF0D1B2A)   // deep background
val MikuNavySurface = Color(0xFF132336)
val MikuNavyCard    = Color(0xFF1C3148)
val MikuPink        = Color(0xFFF48FB1)   // accent / FAB
val MikuPinkDark    = Color(0xFFE91E8C)
val MikuWhite       = Color(0xFFE8F8F7)
val MikuGray        = Color(0xFF8BAAB8)
val MikuGreen       = Color(0xFF4CAF50)
val MikuRed         = Color(0xFFEF5350)

private val MikuDarkColorScheme = darkColorScheme(
    primary          = MikuTeal,
    onPrimary        = MikuNavy,
    primaryContainer = MikuTealDark,
    secondary        = MikuPink,
    onSecondary      = MikuNavy,
    background       = MikuNavy,
    onBackground     = MikuWhite,
    surface          = MikuNavySurface,
    onSurface        = MikuWhite,
    surfaceVariant   = MikuNavyCard,
    onSurfaceVariant = MikuGray,
    error            = MikuRed,
    onError          = MikuWhite,
    outline          = MikuTeal.copy(alpha = 0.5f)
)

@Composable
fun MikuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MikuDarkColorScheme,
        content = content
    )
}
