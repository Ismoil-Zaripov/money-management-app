package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Graphite + Red: Material3 schemes built from the GraphiteRed constants in Color.kt.

internal val GraphiteRedDarkScheme: ColorScheme = darkColorScheme(
    primary = GraphiteRedPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF5B342D),
    onPrimaryContainer = Color(0xFFEFC9C4),
    secondary = GraphiteRedAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF665A3E),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = GraphiteRedAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF665A3E),
    onTertiaryContainer = Color(0xFFFFFFFF),
    background = GraphiteRedBackground,
    onBackground = Color(0xFFF2EEE9),
    surface = Color(0xFF312D2A),
    onSurface = Color(0xFFF2EEE9),
    surfaceVariant = Color(0xFF4E4A48),
    onSurfaceVariant = Color(0xFFBCB8B4),
    surfaceContainerLowest = Color(0xFF221F1D),
    surfaceContainerLow = Color(0xFF373330),
    surfaceContainer = Color(0xFF3D3A37),
    surfaceContainerHigh = Color(0xFF46423F),
    surfaceContainerHighest = Color(0xFF4E4A48),
    outline = Color(0xFF6D7C92),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
