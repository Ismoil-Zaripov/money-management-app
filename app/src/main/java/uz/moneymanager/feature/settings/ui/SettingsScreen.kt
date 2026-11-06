package uz.moneymanager.feature.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmScaffold
import uz.moneymanager.core.designsystem.component.MmSectionHeader
import uz.moneymanager.core.designsystem.component.MmTopBar
import uz.moneymanager.feature.settings.presentation.SettingsUiState
import uz.moneymanager.feature.settings.ui.sheet.PaletteSheet
import uz.moneymanager.feature.settings.ui.component.DataManagementSection
import uz.moneymanager.feature.settings.ui.component.FontSizeSetting
import uz.moneymanager.feature.settings.ui.component.LanguageSelector
import uz.moneymanager.feature.settings.ui.component.PaletteSetting
import uz.moneymanager.feature.settings.ui.component.SettingsCard
import uz.moneymanager.feature.settings.ui.component.ThemeSelector

@Composable
fun SettingsScreen(state: SettingsUiState, callbacks: SettingsCallbacks) {
    var showPaletteSheet by rememberSaveable { mutableStateOf(false) }
    MmScaffold(
        topBar = { MmTopBar(title = stringResource(R.string.title_settings)) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(padding.calculateTopPadding()))
            MmSectionHeader(stringResource(R.string.settings_section_general))
            SettingsCard {
                ThemeSelector(state.themeMode, callbacks.onThemeChange)
                PaletteSetting(state.palette) { showPaletteSheet = true }
                FontSizeSetting(state.contentFontSize, callbacks.onFontSizeChange, callbacks.onFontSizeReset)
                LanguageSelector(state.language, callbacks.onLanguageChange)
            }
            Spacer(modifier = Modifier.height(8.dp))
            MmSectionHeader(stringResource(R.string.settings_section_management))
            SettingsCard {
                DataManagementSection(
                    onNavigateCurrencies = callbacks.onNavigateCurrencies,
                    onNavigateRates = callbacks.onNavigateRates,
                    onNavigateCategories = callbacks.onNavigateCategories,
                    onNavigatePaymentMethods = callbacks.onNavigatePaymentMethods,
                )
            }
            Spacer(modifier = Modifier.height(16.dp + padding.calculateBottomPadding()))
        }
    }

    if (showPaletteSheet) {
        PaletteSheet(
            selected = state.palette,
            onSelect = callbacks.onPaletteChange,
            onDismiss = { showPaletteSheet = false }
        )
    }
}
