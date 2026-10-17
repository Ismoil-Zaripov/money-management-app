package uz.moneymanager.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import uz.moneymanager.domain.model.AppPalette

// Standard Error Roles
private val LightError = Color(0xFFBA1A1A)
private val LightOnError = Color(0xFFFFFFFF)
private val LightErrorContainer = Color(0xFFFFDAD6)
private val LightOnErrorContainer = Color(0xFF410002)

private val DarkError = Color(0xFFFFB4AB)
private val DarkOnError = Color(0xFF690005)
private val DarkErrorContainer = Color(0xFF93000A)
private val DarkOnErrorContainer = Color(0xFFFFDAD6)

// 1. OCEAN PALETTE (MUI Blue)
val OceanPrimary = Color(0xFF1976D2)
private val OceanLight = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6B5778),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF2DAFF),
    onTertiaryContainer = Color(0xFF251432),
    background = Color(0xFFFDFBFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF43474E),
    surfaceContainer = Color(0xFFF0F4F8),
    surfaceContainerHigh = Color(0xFFE8EEF4),
    surfaceContainerLow = Color(0xFFF6F9FD),
    outline = Color(0xFF73777F),
    outlineVariant = Color(0xFFC3C7CF),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

private val OceanDark = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFD6BEE4),
    onTertiary = Color(0xFF3B2948),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C20),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C7CF),
    surfaceContainer = Color(0xFF1D2024),
    surfaceContainerHigh = Color(0xFF282A2F),
    surfaceContainerLow = Color(0xFF191C20),
    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF43474E),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

// 2. EMERALD PALETTE (Green)
val EmeraldPrimary = Color(0xFF006C4C)
private val EmeraldLight = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF89F8C7),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF4D6356),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE9D8),
    onSecondaryContainer = Color(0xFF0B1F15),
    tertiary = Color(0xFF3D6373),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC1E8FC),
    onTertiaryContainer = Color(0xFF001F29),
    background = Color(0xFFFBFDFA),
    onBackground = Color(0xFF191C1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C1A),
    surfaceVariant = Color(0xFFDBE5DE),
    onSurfaceVariant = Color(0xFF404944),
    surfaceContainer = Color(0xFFEDF5F0),
    surfaceContainerHigh = Color(0xFFE3EDE6),
    surfaceContainerLow = Color(0xFFF4FAF5),
    outline = Color(0xFF707974),
    outlineVariant = Color(0xFFBFC9C2),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

private val EmeraldDark = darkColorScheme(
    primary = Color(0xFF6CDBAC),
    onPrimary = Color(0xFF003825),
    primaryContainer = Color(0xFF005138),
    onPrimaryContainer = Color(0xFF89F8C7),
    secondary = Color(0xFFB4CCBC),
    onSecondary = Color(0xFF20352A),
    secondaryContainer = Color(0xFF364B3F),
    onSecondaryContainer = Color(0xFFCFE9D8),
    tertiary = Color(0xFFA5CCDF),
    onTertiary = Color(0xFF073544),
    tertiaryContainer = Color(0xFF244C5B),
    onTertiaryContainer = Color(0xFFC1E8FC),
    background = Color(0xFF101412),
    onBackground = Color(0xFFE1E3E0),
    surface = Color(0xFF171D1A),
    onSurface = Color(0xFFE1E3E0),
    surfaceVariant = Color(0xFF404944),
    onSurfaceVariant = Color(0xFFBFC9C2),
    surfaceContainer = Color(0xFF1C211E),
    surfaceContainerHigh = Color(0xFF262B28),
    surfaceContainerLow = Color(0xFF181D1A),
    outline = Color(0xFF8A938D),
    outlineVariant = Color(0xFF404944),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

// 3. VIOLET PALETTE (Indigo / Purple)
val VioletPrimary = Color(0xFF5E49AC)
private val VioletLight = lightColorScheme(
    primary = VioletPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE5DEFF),
    onPrimaryContainer = Color(0xFF190065),
    secondary = Color(0xFF605B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE6DFF9),
    onSecondaryContainer = Color(0xFF1C192B),
    tertiary = Color(0xFF7C5264),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9E6),
    onTertiaryContainer = Color(0xFF301120),
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE5E0EC),
    onSurfaceVariant = Color(0xFF47454E),
    surfaceContainer = Color(0xFFF2EFF7),
    surfaceContainerHigh = Color(0xFFECE8F1),
    surfaceContainerLow = Color(0xFFF7F4FD),
    outline = Color(0xFF787680),
    outlineVariant = Color(0xFFC9C5D0),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

private val VioletDark = darkColorScheme(
    primary = Color(0xFFC9BFFF),
    onPrimary = Color(0xFF2F117B),
    primaryContainer = Color(0xFF463093),
    onPrimaryContainer = Color(0xFFE5DEFF),
    secondary = Color(0xFFC9C3DC),
    onSecondary = Color(0xFF312D41),
    secondaryContainer = Color(0xFF484459),
    onSecondaryContainer = Color(0xFFE6DFF9),
    tertiary = Color(0xFFEDB7CB),
    onTertiary = Color(0xFF482535),
    tertiaryContainer = Color(0xFF623B4C),
    onTertiaryContainer = Color(0xFFFFD9E6),
    background = Color(0xFF141318),
    onBackground = Color(0xFFE6E1E6),
    surface = Color(0xFF1E1C24),
    onSurface = Color(0xFFE6E1E6),
    surfaceVariant = Color(0xFF47454E),
    onSurfaceVariant = Color(0xFFC9C5D0),
    surfaceContainer = Color(0xFF201F25),
    surfaceContainerHigh = Color(0xFF2B2930),
    surfaceContainerLow = Color(0xFF1C1B20),
    outline = Color(0xFF928F99),
    outlineVariant = Color(0xFF47454E),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

// 4. SUNSET PALETTE (Terracotta / Warm Coral)
val SunsetPrimary = Color(0xFFA33E00)
private val SunsetLight = lightColorScheme(
    primary = SunsetPrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBCE),
    onPrimaryContainer = Color(0xFF370E00),
    secondary = Color(0xFF76574C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDBCE),
    onSecondaryContainer = Color(0xFF2B160E),
    tertiary = Color(0xFF675F31),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEFE3A9),
    onTertiaryContainer = Color(0xFF201C00),
    background = Color(0xFFFFF8F6),
    onBackground = Color(0xFF221A16),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF221A16),
    surfaceVariant = Color(0xFFF4DED5),
    onSurfaceVariant = Color(0xFF52443E),
    surfaceContainer = Color(0xFFF9EAE4),
    surfaceContainerHigh = Color(0xFFF3E0D9),
    surfaceContainerLow = Color(0xFFFDF0EA),
    outline = Color(0xFF85736C),
    outlineVariant = Color(0xFFD7C2BA),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

private val SunsetDark = darkColorScheme(
    primary = Color(0xFFFFB596),
    onPrimary = Color(0xFF581E00),
    primaryContainer = Color(0xFF7D2D00),
    onPrimaryContainer = Color(0xFFFFDBCE),
    secondary = Color(0xFFE6BEB0),
    onSecondary = Color(0xFF432A20),
    secondaryContainer = Color(0xFF5C4035),
    onSecondaryContainer = Color(0xFFFFDBCE),
    tertiary = Color(0xFFD2C790),
    onTertiary = Color(0xFF373107),
    tertiaryContainer = Color(0xFF4E471C),
    onTertiaryContainer = Color(0xFFEFE3A9),
    background = Color(0xFF19120F),
    onBackground = Color(0xFFEDE0DC),
    surface = Color(0xFF231A16),
    onSurface = Color(0xFFEDE0DC),
    surfaceVariant = Color(0xFF52443E),
    onSurfaceVariant = Color(0xFFD7C2BA),
    surfaceContainer = Color(0xFF261D19),
    surfaceContainerHigh = Color(0xFF322723),
    surfaceContainerLow = Color(0xFF211815),
    outline = Color(0xFF9F8C85),
    outlineVariant = Color(0xFF52443E),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

// 5. GRAPHITE PALETTE (Slate / Neutral Charcoal)
val GraphitePrimary = Color(0xFF3E474F)
private val GraphiteLight = lightColorScheme(
    primary = GraphitePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E0EE),
    onPrimaryContainer = Color(0xFF051722),
    secondary = Color(0xFF535F69),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD6E3EF),
    onSecondaryContainer = Color(0xFF101C25),
    tertiary = Color(0xFF625B71),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE8DEF8),
    onTertiaryContainer = Color(0xFF1E192B),
    background = Color(0xFFF9F9FB),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDEE2E8),
    onSurfaceVariant = Color(0xFF42474C),
    surfaceContainer = Color(0xFFECEEF2),
    surfaceContainerHigh = Color(0xFFE4E7EC),
    surfaceContainerLow = Color(0xFFF2F4F7),
    outline = Color(0xFF72777E),
    outlineVariant = Color(0xFFC2C7CD),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

private val GraphiteDark = darkColorScheme(
    primary = Color(0xFFB6C7D5),
    onPrimary = Color(0xFF202A32),
    primaryContainer = Color(0xFF343E46),
    onPrimaryContainer = Color(0xFFD1E0EE),
    secondary = Color(0xFFBBC7D3),
    onSecondary = Color(0xFF25313A),
    secondaryContainer = Color(0xFF3C4851),
    onSecondaryContainer = Color(0xFFD6E3EF),
    tertiary = Color(0xFFCBC2DB),
    onTertiary = Color(0xFF332D41),
    tertiaryContainer = Color(0xFF4A4358),
    onTertiaryContainer = Color(0xFFE8DEF8),
    background = Color(0xFF121416),
    onBackground = Color(0xFFE2E2E5),
    surface = Color(0xFF1B1E21),
    onSurface = Color(0xFFE2E2E5),
    surfaceVariant = Color(0xFF42474C),
    onSurfaceVariant = Color(0xFFC2C7CD),
    surfaceContainer = Color(0xFF1D2023),
    surfaceContainerHigh = Color(0xFF282B2E),
    surfaceContainerLow = Color(0xFF191C1E),
    outline = Color(0xFF8C9197),
    outlineVariant = Color(0xFF42474C),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

fun getPaletteColorScheme(palette: AppPalette, darkTheme: Boolean): ColorScheme {
    return when (palette) {
        AppPalette.OCEAN -> if (darkTheme) OceanDark else OceanLight
        AppPalette.EMERALD -> if (darkTheme) EmeraldDark else EmeraldLight
        AppPalette.VIOLET -> if (darkTheme) VioletDark else VioletLight
        AppPalette.SUNSET -> if (darkTheme) SunsetDark else SunsetLight
        AppPalette.GRAPHITE -> if (darkTheme) GraphiteDark else GraphiteLight
    }
}

fun getPalettePrimaryColor(palette: AppPalette): Color {
    return when (palette) {
        AppPalette.OCEAN -> OceanPrimary
        AppPalette.EMERALD -> EmeraldPrimary
        AppPalette.VIOLET -> VioletPrimary
        AppPalette.SUNSET -> SunsetPrimary
        AppPalette.GRAPHITE -> GraphitePrimary
    }
}
