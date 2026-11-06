package uz.moneymanager.feature.settings.ui

import androidx.compose.runtime.Immutable
import uz.moneymanager.domain.model.AppLanguage
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.domain.model.BarStyle
import uz.moneymanager.domain.model.ThemeMode

@Immutable
class SettingsCallbacks(
    val onThemeChange: (ThemeMode) -> Unit,
    val onPaletteChange: (AppPalette) -> Unit,
    val onBarStyleChange: (BarStyle) -> Unit,
    val onGlassTransparencyChange: (Int) -> Unit,
    val onFontSizeChange: (Int) -> Unit,
    val onFontSizeReset: () -> Unit,
    val onLanguageChange: (AppLanguage) -> Unit,
    val onNavigateCurrencies: () -> Unit,
    val onNavigateRates: () -> Unit,
    val onNavigateCategories: () -> Unit,
    val onNavigatePaymentMethods: () -> Unit,
)
