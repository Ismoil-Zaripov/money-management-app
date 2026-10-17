package uz.moneymanager.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.data.repository.LanguageStore
import uz.moneymanager.data.repository.SettingsRepository
import uz.moneymanager.domain.model.AppLanguage
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.domain.model.ThemeMode
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val languageStore: LanguageStore,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val palette: StateFlow<AppPalette> = settingsRepository.palette
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppPalette.OCEAN)

    private val _currentLanguage = MutableStateFlow(languageStore.current())
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setPalette(palette: AppPalette) {
        viewModelScope.launch {
            settingsRepository.setPalette(palette)
        }
    }

    fun setLanguage(language: AppLanguage) {
        languageStore.set(language)
        _currentLanguage.value = language
    }
}
