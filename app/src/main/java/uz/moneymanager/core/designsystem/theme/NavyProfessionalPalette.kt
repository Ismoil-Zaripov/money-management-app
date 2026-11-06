package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Navy Professional: Material3 schemes built from the NavyProfessional constants in Color.kt.
// Dark variant is derived (same hue, lifted primary/accent for AA contrast).

internal val NavyProfessionalLightScheme: ColorScheme = lightColorScheme(
    primary = NavyProfessionalPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDE0E5),
    onPrimaryContainer = Color(0xFF07182D),
    secondary = NavyProfessionalAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFFE8EEF3),
    onSecondaryContainer = Color(0xFF43505D),
    tertiary = NavyProfessionalAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFFE8EEF3),
    onTertiaryContainer = Color(0xFF43505D),
    background = NavyProfessionalBackground,
    onBackground = Color(0xFF121921),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121921),
    surfaceVariant = Color(0xFFE3E9E3),
    onSurfaceVariant = Color(0xFF54595F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF6FAF6),
    surfaceContainer = Color(0xFFEEF4ED),
    surfaceContainerHigh = Color(0xFFE3E9E3),
    surfaceContainerHighest = Color(0xFFDAE0DB),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

internal val NavyProfessionalDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF2F7DDE),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF163051),
    onPrimaryContainer = Color(0xFFACCBF2),
    secondary = NavyProfessionalAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF303C4A),
    onSecondaryContainer = Color(0xFFD1DDE7),
    tertiary = NavyProfessionalAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF303C4A),
    onTertiaryContainer = Color(0xFFD1DDE7),
    background = Color(0xFF0C121A),
    onBackground = Color(0xFFE8EAEE),
    surface = Color(0xFF0C121A),
    onSurface = Color(0xFFE8EAEE),
    surfaceVariant = Color(0xFF233448),
    onSurfaceVariant = Color(0xFFAAAEB3),
    surfaceContainerLowest = Color(0xFF080C11),
    surfaceContainerLow = Color(0xFF111922),
    surfaceContainer = Color(0xFF151F2B),
    surfaceContainerHigh = Color(0xFF1B2939),
    surfaceContainerHighest = Color(0xFF233448),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
