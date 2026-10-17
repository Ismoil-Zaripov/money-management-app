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

// 1. OCEAN PALETTE (Cobalt Blue)
val OceanLightScheme = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCE6FF),
    onPrimaryContainer = Color(0xFF0A1F5C),
    secondary = Color(0xFF455A7C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E2F8),
    onSecondaryContainer = Color(0xFF001B3F),
    tertiary = Color(0xFF6A527A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF2DAFF),
    onTertiaryContainer = Color(0xFF250E33),
    background = Color(0xFFF7F9FE),
    onBackground = Color(0xFF181C22),
    surface = Color(0xFFF7F9FE),
    onSurface = Color(0xFF181C22),
    surfaceVariant = Color(0xFFE1E6F2),
    onSurfaceVariant = Color(0xFF424754),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F4F9),
    surfaceContainer = Color(0xFFEBEFF5),
    surfaceContainerHigh = Color(0xFFE5E9EF),
    surfaceContainerHighest = Color(0xFFDFE3E9),
    outline = Color(0xFF737785),
    outlineVariant = Color(0xFFC3C7D4),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

val OceanDarkScheme = darkColorScheme(
    primary = Color(0xFFA9C0FF),
    onPrimary = Color(0xFF0B2B78),
    primaryContainer = Color(0xFF1A3A91), // BAR in dark mode
    onPrimaryContainer = Color(0xFFDCE6FF),
    secondary = Color(0xFFB5C7E8),
    onSecondary = Color(0xFF1E314F),
    secondaryContainer = Color(0xFF354867),
    onSecondaryContainer = Color(0xFFD7E2F8),
    tertiary = Color(0xFFD6BEE5),
    onTertiary = Color(0xFF3A2449),
    tertiaryContainer = Color(0xFF513B60),
    onTertiaryContainer = Color(0xFFF2DAFF),
    background = Color(0xFF0F131C),
    onBackground = Color(0xFFE0E2EC),
    surface = Color(0xFF0F131C),
    onSurface = Color(0xFFE0E2EC),
    surfaceVariant = Color(0xFF424754),
    onSurfaceVariant = Color(0xFFC3C7D4),
    surfaceContainerLowest = Color(0xFF0A0E17),
    surfaceContainerLow = Color(0xFF161B24),
    surfaceContainer = Color(0xFF1B202A),
    surfaceContainerHigh = Color(0xFF252A35),
    surfaceContainerHighest = Color(0xFF303540),
    outline = Color(0xFF8C919E),
    outlineVariant = Color(0xFF424754),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

// 2. EMERALD PALETTE (Green - Preserved as loved by user)
val EmeraldLightScheme = lightColorScheme(
    primary = Color(0xFF006C4C),
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
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF4FAF5),
    surfaceContainer = Color(0xFFEDF5F0),
    surfaceContainerHigh = Color(0xFFE3EDE6),
    surfaceContainerHighest = Color(0xFFDCE7E0),
    outline = Color(0xFF707974),
    outlineVariant = Color(0xFFBFC9C2),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

val EmeraldDarkScheme = darkColorScheme(
    primary = Color(0xFF6CDBAC),
    onPrimary = Color(0xFF003825),
    primaryContainer = Color(0xFF005138), // BAR in dark mode
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
    surfaceContainerLowest = Color(0xFF0B0F0D),
    surfaceContainerLow = Color(0xFF181D1A),
    surfaceContainer = Color(0xFF1C211E),
    surfaceContainerHigh = Color(0xFF262B28),
    surfaceContainerHighest = Color(0xFF313733),
    outline = Color(0xFF8A938D),
    outlineVariant = Color(0xFF404944),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

// 3. VIOLET PALETTE (Indigo-Violet)
val VioletLightScheme = lightColorScheme(
    primary = Color(0xFF5A3FD1),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE7E0FF),
    onPrimaryContainer = Color(0xFF1D0B63),
    secondary = Color(0xFF5D577A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE4DFFF),
    onSecondaryContainer = Color(0xFF1A1534),
    tertiary = Color(0xFF7C4F72),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD7F3),
    onTertiaryContainer = Color(0xFF320B2C),
    background = Color(0xFFFAF8FF),
    onBackground = Color(0xFF1A1923),
    surface = Color(0xFFFAF8FF),
    onSurface = Color(0xFF1A1923),
    surfaceVariant = Color(0xFFE6E1F2),
    onSurfaceVariant = Color(0xFF474454),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF4F0FD),
    surfaceContainer = Color(0xFFEEE9F7),
    surfaceContainerHigh = Color(0xFFE8E3F2),
    surfaceContainerHighest = Color(0xFFE2DDEC),
    outline = Color(0xFF787586),
    outlineVariant = Color(0xFFC8C4D6),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

val VioletDarkScheme = darkColorScheme(
    primary = Color(0xFFCBBEFF),
    onPrimary = Color(0xFF2C1384),
    primaryContainer = Color(0xFF3F2BA0), // BAR in dark mode
    onPrimaryContainer = Color(0xFFE7E0FF),
    secondary = Color(0xFFC6BFE6),
    onSecondary = Color(0xFF2F294A),
    secondaryContainer = Color(0xFF453F62),
    onSecondaryContainer = Color(0xFFE4DFFF),
    tertiary = Color(0xFFEEB7E2),
    onTertiary = Color(0xFF492042),
    tertiaryContainer = Color(0xFF623759),
    onTertiaryContainer = Color(0xFFFFD7F3),
    background = Color(0xFF13111A),
    onBackground = Color(0xFFE5E0EE),
    surface = Color(0xFF13111A),
    onSurface = Color(0xFFE5E0EE),
    surfaceVariant = Color(0xFF474454),
    onSurfaceVariant = Color(0xFFC8C4D6),
    surfaceContainerLowest = Color(0xFF0D0C13),
    surfaceContainerLow = Color(0xFF1A1723),
    surfaceContainer = Color(0xFF201E2B),
    surfaceContainerHigh = Color(0xFF2B2836),
    surfaceContainerHighest = Color(0xFF363342),
    outline = Color(0xFF928EA0),
    outlineVariant = Color(0xFF474454),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

// 4. SUNSET PALETTE (Rose-Raspberry / Shafaq)
val SunsetLightScheme = lightColorScheme(
    primary = Color(0xFFB8235A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9E4),
    onPrimaryContainer = Color(0xFF3F0020),
    secondary = Color(0xFF785361),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E6),
    onSecondaryContainer = Color(0xFF2D121F),
    tertiary = Color(0xFF7C573A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDCBE),
    onTertiaryContainer = Color(0xFF2E1602),
    background = Color(0xFFFFF8F9),
    onBackground = Color(0xFF22191C),
    surface = Color(0xFFFFF8F9),
    onSurface = Color(0xFF22191C),
    surfaceVariant = Color(0xFFF3DFE4),
    onSurfaceVariant = Color(0xFF514247),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFDF0F3),
    surfaceContainer = Color(0xFFF7EAEF),
    surfaceContainerHigh = Color(0xFFF1E4E9),
    surfaceContainerHighest = Color(0xFFEBDEE3),
    outline = Color(0xFF847277),
    outlineVariant = Color(0xFFD6C2C7),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

val SunsetDarkScheme = darkColorScheme(
    primary = Color(0xFFFFB1C8),
    onPrimary = Color(0xFF650033),
    primaryContainer = Color(0xFF8A1544), // BAR in dark mode
    onPrimaryContainer = Color(0xFFFFD9E4),
    secondary = Color(0xFFE6BACB),
    onSecondary = Color(0xFF452633),
    secondaryContainer = Color(0xFF5E3C49),
    onSecondaryContainer = Color(0xFFFFD9E6),
    tertiary = Color(0xFFEFBD94),
    onTertiary = Color(0xFF472A0F),
    tertiaryContainer = Color(0xFF614023),
    onTertiaryContainer = Color(0xFFFFDCBE),
    background = Color(0xFF191114),
    onBackground = Color(0xFFEFE0E3),
    surface = Color(0xFF191114),
    onSurface = Color(0xFFEFE0E3),
    surfaceVariant = Color(0xFF514247),
    onSurfaceVariant = Color(0xFFD6C2C7),
    surfaceContainerLowest = Color(0xFF130B0E),
    surfaceContainerLow = Color(0xFF20171B),
    surfaceContainer = Color(0xFF271D21),
    surfaceContainerHigh = Color(0xFF32272C),
    surfaceContainerHighest = Color(0xFF3E3236),
    outline = Color(0xFF9F8B91),
    outlineVariant = Color(0xFF514247),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

// 5. GRAPHITE PALETTE (Dark Ink-Blue / Slate)
val GraphiteLightScheme = lightColorScheme(
    primary = Color(0xFF243049),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDE3F1),
    onPrimaryContainer = Color(0xFF0E1627),
    secondary = Color(0xFF525D73),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD8E2F9),
    onSecondaryContainer = Color(0xFF0E1A2D),
    tertiary = Color(0xFF645A77),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEBDDFE),
    onTertiaryContainer = Color(0xFF201730),
    background = Color(0xFFF6F7FA),
    onBackground = Color(0xFF181B20),
    surface = Color(0xFFF6F7FA),
    onSurface = Color(0xFF181B20),
    surfaceVariant = Color(0xFFE0E3EA),
    onSurfaceVariant = Color(0xFF434750),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF1F5),
    surfaceContainer = Color(0xFFE9EBEF),
    surfaceContainerHigh = Color(0xFFE3E5E9),
    surfaceContainerHighest = Color(0xFFDDE0E4),
    outline = Color(0xFF747781),
    outlineVariant = Color(0xFFC4C7CE),
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

val GraphiteDarkScheme = darkColorScheme(
    primary = Color(0xFFB9C7E8),
    onPrimary = Color(0xFF1A2540),
    primaryContainer = Color(0xFF2A3753), // BAR in dark mode
    onPrimaryContainer = Color(0xFFDDE3F1),
    secondary = Color(0xFFB9C6E2),
    onSecondary = Color(0xFF233045),
    secondaryContainer = Color(0xFF3A475D),
    onSecondaryContainer = Color(0xFFD8E2F9),
    tertiary = Color(0xFFCEC1E4),
    onTertiary = Color(0xFF352C46),
    tertiaryContainer = Color(0xFF4C425E),
    onTertiaryContainer = Color(0xFFEBDDFE),
    background = Color(0xFF0F1218),
    onBackground = Color(0xFFDFE2E8),
    surface = Color(0xFF0F1218),
    onSurface = Color(0xFFDFE2E8),
    surfaceVariant = Color(0xFF434750),
    onSurfaceVariant = Color(0xFFC4C7CE),
    surfaceContainerLowest = Color(0xFF090C11),
    surfaceContainerLow = Color(0xFF151920),
    surfaceContainer = Color(0xFF1A1E25),
    surfaceContainerHigh = Color(0xFF242830),
    surfaceContainerHighest = Color(0xFF2F333B),
    outline = Color(0xFF8D919B),
    outlineVariant = Color(0xFF434750),
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

fun getPaletteColorScheme(palette: AppPalette, darkTheme: Boolean): ColorScheme {
    return when (palette) {
        AppPalette.OCEAN -> if (darkTheme) OceanDarkScheme else OceanLightScheme
        AppPalette.EMERALD -> if (darkTheme) EmeraldDarkScheme else EmeraldLightScheme
        AppPalette.VIOLET -> if (darkTheme) VioletDarkScheme else VioletLightScheme
        AppPalette.SUNSET -> if (darkTheme) SunsetDarkScheme else SunsetLightScheme
        AppPalette.GRAPHITE -> if (darkTheme) GraphiteDarkScheme else GraphiteLightScheme
    }
}

fun getPaletteSwatchColor(palette: AppPalette, darkTheme: Boolean): Color {
    return when (palette) {
        AppPalette.OCEAN -> if (darkTheme) Color(0xFFA9C0FF) else Color(0xFF1D4ED8)
        AppPalette.EMERALD -> if (darkTheme) Color(0xFF6CDBAC) else Color(0xFF006C4C)
        AppPalette.VIOLET -> if (darkTheme) Color(0xFFCBBEFF) else Color(0xFF5A3FD1)
        AppPalette.SUNSET -> if (darkTheme) Color(0xFFFFB1C8) else Color(0xFFB8235A)
        AppPalette.GRAPHITE -> if (darkTheme) Color(0xFFB9C7E8) else Color(0xFF243049)
    }
}
