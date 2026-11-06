package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Forest Green: Material3 schemes built from the ForestGreen constants in Color.kt.
// Dark variant is derived (same hue, lifted primary/accent for AA contrast).

internal val ForestGreenLightScheme: ColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDEE9E3),
    onPrimaryContainer = Color(0xFF0E4222),
    secondary = ForestGreenAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFFD3F3DF),
    onSecondaryContainer = Color(0xFF105A2B),
    tertiary = ForestGreenAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFFD3F3DF),
    onTertiaryContainer = Color(0xFF105A2B),
    background = ForestGreenBackground,
    onBackground = Color(0xFF122118),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF122118),
    surfaceVariant = Color(0xFFE5F2E9),
    onSurfaceVariant = Color(0xFF545F59),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FEFA),
    surfaceContainer = Color(0xFFF0FDF4),
    surfaceContainerHigh = Color(0xFFE5F2E9),
    surfaceContainerHighest = Color(0xFFDCE9E0),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

internal val ForestGreenDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF20934C),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF123C22),
    onPrimaryContainer = Color(0xFFA6D4B7),
    secondary = ForestGreenAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF124A27),
    onSecondaryContainer = Color(0xFFA7E8BF),
    tertiary = ForestGreenAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF124A27),
    onTertiaryContainer = Color(0xFFA7E8BF),
    background = Color(0xFF0C1A12),
    onBackground = Color(0xFFE8EEEA),
    surface = Color(0xFF0C1A12),
    onSurface = Color(0xFFE8EEEA),
    surfaceVariant = Color(0xFF234831),
    onSurfaceVariant = Color(0xFFAAB3AE),
    surfaceContainerLowest = Color(0xFF08110C),
    surfaceContainerLow = Color(0xFF112217),
    surfaceContainer = Color(0xFF152B1D),
    surfaceContainerHigh = Color(0xFF1B3927),
    surfaceContainerHighest = Color(0xFF234831),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
