package uz.moneymanager.feature.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.feature.settings.presentation.SettingsUiState
import uz.moneymanager.feature.settings.ui.component.DataManagementSection
import uz.moneymanager.feature.settings.ui.component.FontSizeSetting
import uz.moneymanager.feature.settings.ui.component.LanguageSelector
import uz.moneymanager.feature.settings.ui.component.PalettePicker
import uz.moneymanager.feature.settings.ui.component.SettingsDivider
import uz.moneymanager.feature.settings.ui.component.SettingsSection
import uz.moneymanager.feature.settings.ui.component.SettingsTopBar
import uz.moneymanager.feature.settings.ui.component.ThemeSelector

@Composable
fun SettingsScreen(state: SettingsUiState, callbacks: SettingsCallbacks) {
    Scaffold(topBar = { SettingsTopBar() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSection(stringResource(R.string.settings_section_general))
            ThemeSelector(state.themeMode, callbacks.onThemeChange)
            PalettePicker(state.palette, callbacks.onPaletteChange)
            SettingsDivider()
            FontSizeSetting(state.contentFontSize, callbacks.onFontSizeChange, callbacks.onFontSizeReset)
            SettingsDivider()
            LanguageSelector(state.language, callbacks.onLanguageChange)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSection(stringResource(R.string.settings_section_management))
            DataManagementSection(
                onNavigateCurrencies = callbacks.onNavigateCurrencies,
                onNavigateRates = callbacks.onNavigateRates,
                onNavigateCategories = callbacks.onNavigateCategories,
                onNavigatePaymentMethods = callbacks.onNavigatePaymentMethods,
            )
        }
    }
}
