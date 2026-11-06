package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Aubergine: Material3 schemes built from the Aubergine constants in Color.kt.
// Dark variant is derived (same hue, lifted primary/accent for AA contrast).

internal val AubergineLightScheme: ColorScheme = lightColorScheme(
    primary = AuberginePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEBE6EA),
    onPrimaryContainer = Color(0xFF493143),
    secondary = AubergineAccent,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE5E6),
    onSecondaryContainer = Color(0xFF014549),
    tertiary = AubergineAccent,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCCE5E6),
    onTertiaryContainer = Color(0xFF014549),
    background = AubergineBackground,
    onBackground = Color(0xFF21121D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF21121D),
    surfaceVariant = Color(0xFFEEEEF0),
    onSurfaceVariant = Color(0xFF5F545C),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFCFCFD),
    surfaceContainer = Color(0xFFF9FAFB),
    surfaceContainerHigh = Color(0xFFEEEEF0),
    surfaceContainerHighest = Color(0xFFE6E5E7),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

internal val AubergineDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF9F6F92),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF3D2A38),
    onPrimaryContainer = Color(0xFFD9C5D3),
    secondary = Color(0xFF018C93),
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF113238),
    onSecondaryContainer = Color(0xFF99D1D4),
    tertiary = Color(0xFF018C93),
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF113238),
    onTertiaryContainer = Color(0xFF99D1D4),
    background = Color(0xFF170F15),
    onBackground = Color(0xFFEEE8EC),
    surface = Color(0xFF170F15),
    onSurface = Color(0xFFEEE8EC),
    surfaceVariant = Color(0xFF402B3B),
    onSurfaceVariant = Color(0xFFB2ABB0),
    surfaceContainerLowest = Color(0xFF0F0A0E),
    surfaceContainerLow = Color(0xFF1F141C),
    surfaceContainer = Color(0xFF261923),
    surfaceContainerHigh = Color(0xFF33222E),
    surfaceContainerHighest = Color(0xFF402B3B),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
