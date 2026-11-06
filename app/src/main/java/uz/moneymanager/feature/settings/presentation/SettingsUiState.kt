package uz.moneymanager.feature.settings.presentation

import androidx.compose.runtime.Immutable
import uz.moneymanager.data.repository.SettingsRepository
import uz.moneymanager.domain.model.AppLanguage
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.domain.model.BarStyle
import uz.moneymanager.domain.model.ThemeMode

/** Sozlamalar ekrani holati; [contentFontSize] DataStore'dan o'qilguncha null. */
@Immutable
data class SettingsUiState(
    val language: AppLanguage,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val palette: AppPalette = AppPalette.DEFAULT,
    val contentFontSize: Int? = null,
    val barStyle: BarStyle = BarStyle.DEFAULT,
    val glassTransparency: Int = SettingsRepository.DEFAULT_GLASS_TRANSPARENCY,
)
