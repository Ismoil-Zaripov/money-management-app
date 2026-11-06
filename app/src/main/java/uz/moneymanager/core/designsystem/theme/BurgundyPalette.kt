package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Burgundy: Material3 schemes built from the Burgundy constants in Color.kt.
// Dark variant is derived (same hue, lifted primary/accent for AA contrast).

internal val BurgundyLightScheme: ColorScheme = lightColorScheme(
    primary = BurgundyPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEDDFDF),
    onPrimaryContainer = Color(0xFF531313),
    secondary = BurgundyAccent,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF0DDCE),
    onSecondaryContainer = Color(0xFF632E05),
    tertiary = BurgundyAccent,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF0DDCE),
    onTertiaryContainer = Color(0xFF632E05),
    background = BurgundyBackground,
    onBackground = Color(0xFF211212),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF211212),
    surfaceVariant = Color(0xFFEFEAEA),
    onSurfaceVariant = Color(0xFF5F5454),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFCFAFA),
    surfaceContainer = Color(0xFFFAF5F5),
    surfaceContainerHigh = Color(0xFFEFEAEA),
    surfaceContainerHighest = Color(0xFFE6E1E1),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

internal val BurgundyDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFFD74F4F),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF4F1F1F),
    onPrimaryContainer = Color(0xFFEFB9B9),
    secondary = Color(0xFFCC5E0A),
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF4C230B),
    onSecondaryContainer = Color(0xFFEBBF9D),
    tertiary = Color(0xFFCC5E0A),
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF4C230B),
    onTertiaryContainer = Color(0xFFEBBF9D),
    background = Color(0xFF1A0C0C),
    onBackground = Color(0xFFEEE8E8),
    surface = Color(0xFF1A0C0C),
    onSurface = Color(0xFFEEE8E8),
    surfaceVariant = Color(0xFF482323),
    onSurfaceVariant = Color(0xFFB3AAAA),
    surfaceContainerLowest = Color(0xFF110808),
    surfaceContainerLow = Color(0xFF221111),
    surfaceContainer = Color(0xFF2B1515),
    surfaceContainerHigh = Color(0xFF391B1B),
    surfaceContainerHighest = Color(0xFF482323),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
