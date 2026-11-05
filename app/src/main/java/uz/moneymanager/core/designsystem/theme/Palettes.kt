package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import uz.moneymanager.domain.model.AppPalette

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
