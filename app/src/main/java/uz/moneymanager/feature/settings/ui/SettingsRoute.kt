package uz.moneymanager.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.data.repository.SettingsRepository
import uz.moneymanager.feature.settings.presentation.SettingsViewModel

@Composable
fun SettingsRoute(
    onNavigateCurrencies: () -> Unit,
    onNavigateRates: () -> Unit,
    onNavigateCategories: () -> Unit,
    onNavigatePaymentMethods: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val callbacks = SettingsCallbacks(
        onThemeChange = viewModel::setThemeMode,
        onPaletteChange = viewModel::setPalette,
        onBarStyleChange = viewModel::setBarStyle,
        onGlassTransparencyChange = viewModel::setBarGlassTransparency,
        onFontSizeChange = viewModel::setContentFontSize,
        onFontSizeReset = { viewModel.setContentFontSize(SettingsRepository.DEFAULT_FONT_SIZE) },
        onLanguageChange = viewModel::setLanguage,
        onNavigateCurrencies = onNavigateCurrencies,
        onNavigateRates = onNavigateRates,
        onNavigateCategories = onNavigateCategories,
        onNavigatePaymentMethods = onNavigatePaymentMethods,
    )
    SettingsScreen(state = state, callbacks = callbacks)
}
