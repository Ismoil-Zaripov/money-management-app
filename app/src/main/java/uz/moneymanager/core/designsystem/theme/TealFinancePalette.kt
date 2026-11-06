package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Teal Finance: Material3 schemes built from the TealFinance constants in Color.kt.
// Dark variant is derived (same hue, lifted primary/accent for AA contrast).

internal val TealFinanceLightScheme: ColorScheme = lightColorScheme(
    primary = TealFinancePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDECEB),
    onPrimaryContainer = Color(0xFF0A4D48),
    secondary = TealFinanceAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFFD0F1ED),
    onSecondaryContainer = Color(0xFF09574E),
    tertiary = TealFinanceAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFFD0F1ED),
    onTertiaryContainer = Color(0xFF09574E),
    background = TealFinanceBackground,
    onBackground = Color(0xFF122120),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF122120),
    surfaceVariant = Color(0xFFE5F2EF),
    onSurfaceVariant = Color(0xFF545F5E),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FEFC),
    surfaceContainer = Color(0xFFF0FDFA),
    surfaceContainerHigh = Color(0xFFE5F2EF),
    surfaceContainerHighest = Color(0xFFDCE9E6),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
)

internal val TealFinanceDarkScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF129187),
    onPrimary = Color(0xFF0B1220),
    primaryContainer = Color(0xFF0E3B38),
    onPrimaryContainer = Color(0xFFA0D3CF),
    secondary = TealFinanceAccent,
    onSecondary = Color(0xFF0B1220),
    secondaryContainer = Color(0xFF0E4640),
    onSecondaryContainer = Color(0xFFA1E3DB),
    tertiary = TealFinanceAccent,
    onTertiary = Color(0xFF0B1220),
    tertiaryContainer = Color(0xFF0E4640),
    onTertiaryContainer = Color(0xFFA1E3DB),
    background = Color(0xFF0C1A19),
    onBackground = Color(0xFFE8EEED),
    surface = Color(0xFF0C1A19),
    onSurface = Color(0xFFE8EEED),
    surfaceVariant = Color(0xFF234845),
    onSurfaceVariant = Color(0xFFAAB3B2),
    surfaceContainerLowest = Color(0xFF081111),
    surfaceContainerLow = Color(0xFF112221),
    surfaceContainer = Color(0xFF152B29),
    surfaceContainerHigh = Color(0xFF1B3937),
    surfaceContainerHighest = Color(0xFF234845),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
)
