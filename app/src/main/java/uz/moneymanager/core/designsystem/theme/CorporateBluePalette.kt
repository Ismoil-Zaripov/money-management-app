package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Corporate Blue: Material3 schemes built from the CorporateBlue constants in Color.kt.
// Dark variant is derived (same hue, lifted primary/accent for AA contrast).

internal val CorporateBlueLightScheme: ColorScheme = lightColorScheme(
    primary = CorporateBluePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDEBF9),
    onPrimaryContainer = Color(0xFF064888),
    secondary = CorporateBlueAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFFFBE3CE),
    onSecondaryContainer = Color(0xFF773B07),
    tertiary = CorporateBlueAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFFFBE3CE),
    onTertiaryContainer = Color(0xFF773B07),
    background = CorporateBlueBackground,
    onBackground = Color(0xFF121A21),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121A21),
    surfaceVariant = Color(0xFFEAEBEC),
    onSurfaceVariant = Color(0xFF545A5F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAFAFB),
    surfaceContainer = Color(0xFFF5F6F7),
    surfaceContainerHigh = Color(0xFFEAEBEC),
    surfaceContainerHighest = Color(0xFFE1E2E4),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

internal val CorporateBlueDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF0B7DEE),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF0C3155),
    onPrimaryContainer = Color(0xFF9DCBF8),
    secondary = CorporateBlueAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF4A2E16),
    onSecondaryContainer = Color(0xFFF6C79E),
    tertiary = CorporateBlueAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF4A2E16),
    onTertiaryContainer = Color(0xFFF6C79E),
    background = Color(0xFF0C131A),
    onBackground = Color(0xFFE8EBEE),
    surface = Color(0xFF0C131A),
    onSurface = Color(0xFFE8EBEE),
    surfaceVariant = Color(0xFF233648),
    onSurfaceVariant = Color(0xFFAAAFB3),
    surfaceContainerLowest = Color(0xFF080D11),
    surfaceContainerLow = Color(0xFF111A22),
    surfaceContainer = Color(0xFF15202B),
    surfaceContainerHigh = Color(0xFF1B2A39),
    surfaceContainerHighest = Color(0xFF233648),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
