package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// High Contrast: Material3 schemes built from the HighContrast constants in Color.kt.
// Dark variant is derived (same hue, lifted primary/accent for AA contrast).

internal val HighContrastLightScheme: ColorScheme = lightColorScheme(
    primary = HighContrastPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDBDBDB),
    onPrimaryContainer = Color(0xFF000000),
    secondary = HighContrastAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFFFFF7CC),
    onSecondaryContainer = Color(0xFF625300),
    tertiary = HighContrastAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFFFFF7CC),
    onTertiaryContainer = Color(0xFF625300),
    background = HighContrastBackground,
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFF2F2F2),
    onSurfaceVariant = Color(0xFF474747),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFF2F2F2),
    surfaceContainerHighest = Color(0xFFE8E8E8),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

internal val HighContrastDarkScheme: ColorScheme = darkColorScheme(
    primary = HighContrastBackground,
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF474747),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = HighContrastAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF473C00),
    onSecondaryContainer = Color(0xFFFFEF99),
    tertiary = HighContrastAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF473C00),
    onTertiaryContainer = Color(0xFFFFEF99),
    background = HighContrastPrimary,
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF262626),
    onSurfaceVariant = Color(0xFFB8B8B8),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF0A0A0A),
    surfaceContainer = Color(0xFF121212),
    surfaceContainerHigh = Color(0xFF1C1C1C),
    surfaceContainerHighest = Color(0xFF262626),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
