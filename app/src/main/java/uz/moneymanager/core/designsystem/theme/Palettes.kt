package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import uz.moneymanager.domain.model.AppPalette

fun getPaletteColorScheme(palette: AppPalette, darkTheme: Boolean): ColorScheme = when (palette) {
    AppPalette.EMERALD -> if (darkTheme) EmeraldDarkScheme else EmeraldLightScheme
    AppPalette.OCEAN_TEAL -> if (darkTheme) OceanTealDarkScheme else OceanTealLightScheme
    AppPalette.ROYAL_INDIGO -> if (darkTheme) RoyalIndigoDarkScheme else RoyalIndigoLightScheme
    AppPalette.WARM_CORAL -> if (darkTheme) WarmCoralDarkScheme else WarmCoralLightScheme
    AppPalette.GRAPHITE_MINT -> if (darkTheme) GraphiteMintDarkScheme else GraphiteMintLightScheme
}

fun getPaletteSwatchColor(palette: AppPalette, darkTheme: Boolean): Color =
    getPaletteColorScheme(palette, darkTheme).primary
