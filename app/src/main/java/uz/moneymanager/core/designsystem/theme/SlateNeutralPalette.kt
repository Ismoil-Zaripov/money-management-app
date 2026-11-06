package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Slate Neutral: Material3 schemes built from the SlateNeutral constants in Color.kt.
// Dark variant is derived (same hue, lifted primary/accent for AA contrast).

internal val SlateNeutralLightScheme: ColorScheme = lightColorScheme(
    primary = SlateNeutralPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE2E4E7),
    onPrimaryContainer = Color(0xFF212A37),
    secondary = SlateNeutralAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFFD8E6FD),
    onSecondaryContainer = Color(0xFF204887),
    tertiary = SlateNeutralAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFFD8E6FD),
    onTertiaryContainer = Color(0xFF204887),
    background = SlateNeutralBackground,
    onBackground = Color(0xFF121821),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121821),
    surfaceVariant = Color(0xFFE6EAEE),
    onSurfaceVariant = Color(0xFF54595F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE6EAEE),
    surfaceContainerHighest = Color(0xFFDDE1E6),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

internal val SlateNeutralDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF657EA3),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF26303F),
    onPrimaryContainer = Color(0xFFC1CBDA),
    secondary = SlateNeutralAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF1B3156),
    onSecondaryContainer = Color(0xFFB1CDFB),
    tertiary = SlateNeutralAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF1B3156),
    onTertiaryContainer = Color(0xFFB1CDFB),
    background = Color(0xFF0E1218),
    onBackground = Color(0xFFE8EAEE),
    surface = Color(0xFF0E1218),
    onSurface = Color(0xFFE8EAEE),
    surfaceVariant = Color(0xFF283343),
    onSurfaceVariant = Color(0xFFABAEB2),
    surfaceContainerLowest = Color(0xFF0A0C10),
    surfaceContainerLow = Color(0xFF131820),
    surfaceContainer = Color(0xFF181E28),
    surfaceContainerHigh = Color(0xFF202835),
    surfaceContainerHighest = Color(0xFF283343),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
