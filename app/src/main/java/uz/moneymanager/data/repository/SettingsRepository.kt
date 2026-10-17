package uz.moneymanager.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.moneymanager.domain.model.AppLanguage
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.domain.model.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

data class AppSettings(
    val themeMode: ThemeMode,
    val palette: AppPalette,
)

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        val mode = prefs[THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
        val pal = prefs[PALETTE]?.let { runCatching { AppPalette.valueOf(it) }.getOrNull() } ?: AppPalette.EMERALD
        AppSettings(themeMode = mode, palette = pal)
    }

    val themeMode: Flow<ThemeMode> = settings.map { it.themeMode }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME] = mode.name }
    }

    val palette: Flow<AppPalette> = settings.map { it.palette }

    suspend fun setPalette(palette: AppPalette) {
        dataStore.edit { it[PALETTE] = palette.name }
    }

    private companion object {
        val THEME = stringPreferencesKey("theme_mode")
        val PALETTE = stringPreferencesKey("app_palette")
    }
}

/**
 * Per-app language. Kept in SharedPreferences so it can be read synchronously on startup;
 * English is the default regardless of the device language.
 */
@Singleton
class LanguageStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences("language", Context.MODE_PRIVATE)

    fun current(): AppLanguage =
        AppLanguage.entries.firstOrNull { it.tag == prefs.getString(KEY, null) } ?: AppLanguage.EN

    fun set(language: AppLanguage) {
        prefs.edit { putString(KEY, language.tag) }
        apply(language)
    }

    fun applySaved() = apply(current())

    private fun apply(language: AppLanguage) {
        val desired = LocaleListCompat.forLanguageTags(language.tag)
        if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != desired.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(desired)
        }
    }

    private companion object {
        const val KEY = "app_language"
    }
}
