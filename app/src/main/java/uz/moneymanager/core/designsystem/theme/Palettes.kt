package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import uz.moneymanager.domain.model.AppPalette

/** Dark-only palettes return their dark scheme regardless of [darkTheme]. */
fun getPaletteColorScheme(palette: AppPalette, darkTheme: Boolean): ColorScheme = when (palette) {
    AppPalette.CORPORATE_BLUE -> if (darkTheme) CorporateBlueDarkScheme else CorporateBlueLightScheme
    AppPalette.NAVY_PROFESSIONAL -> if (darkTheme) NavyProfessionalDarkScheme else NavyProfessionalLightScheme
    AppPalette.SLATE_NEUTRAL -> if (darkTheme) SlateNeutralDarkScheme else SlateNeutralLightScheme
    AppPalette.TEAL_FINANCE -> if (darkTheme) TealFinanceDarkScheme else TealFinanceLightScheme
    AppPalette.FOREST_GREEN -> if (darkTheme) ForestGreenDarkScheme else ForestGreenLightScheme
    AppPalette.AUBERGINE -> if (darkTheme) AubergineDarkScheme else AubergineLightScheme
    AppPalette.DARK_SLATE -> DarkSlateDarkScheme
    AppPalette.HIGH_CONTRAST -> if (darkTheme) HighContrastDarkScheme else HighContrastLightScheme
    AppPalette.BURGUNDY -> if (darkTheme) BurgundyDarkScheme else BurgundyLightScheme
    AppPalette.GRAPHITE_RED -> GraphiteRedDarkScheme
}

fun getPaletteSwatchColor(palette: AppPalette, darkTheme: Boolean): Color =
    getPaletteColorScheme(palette, darkTheme).primary
