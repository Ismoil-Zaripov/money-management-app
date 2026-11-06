package uz.moneymanager.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.data.repository.LanguageStore
import uz.moneymanager.data.repository.SettingsRepository
import uz.moneymanager.domain.model.AppLanguage
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.domain.model.BarStyle
import uz.moneymanager.domain.model.ThemeMode

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val languageStore: LanguageStore,
) : ViewModel() {

    private val currentLanguage = MutableStateFlow(languageStore.current())

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.palette,
        settingsRepository.contentFontSize,
        settingsRepository.barStyle,
        currentLanguage,
    ) { theme, palette, fontSize, barStyle, language ->
        SettingsUiState(
            language = language,
            themeMode = theme,
            palette = palette,
            contentFontSize = fontSize,
            barStyle = barStyle,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState(language = languageStore.current()))

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setPalette(palette: AppPalette) {
        viewModelScope.launch { settingsRepository.setPalette(palette) }
    }

    fun setBarStyle(style: BarStyle) {
        viewModelScope.launch { settingsRepository.setBarStyle(style) }
    }

    fun setContentFontSize(size: Int) {
        viewModelScope.launch { settingsRepository.setContentFontSize(size) }
    }

    fun setLanguage(language: AppLanguage) {
        languageStore.set(language)
        currentLanguage.value = language
    }
}
