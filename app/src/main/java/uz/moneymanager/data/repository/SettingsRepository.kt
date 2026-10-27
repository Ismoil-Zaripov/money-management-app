package uz.moneymanager.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
    val contentFontSize: Int = SettingsRepository.DEFAULT_FONT_SIZE,
)

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        val mode = prefs[THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
        val pal = prefs[PALETTE]?.let { runCatching { AppPalette.valueOf(it) }.getOrNull() } ?: AppPalette.EMERALD
        val size = prefs[FONT_SIZE]?.let { clampFontSize(it) } ?: DEFAULT_FONT_SIZE
        AppSettings(themeMode = mode, palette = pal, contentFontSize = size)
    }

    val themeMode: Flow<ThemeMode> = settings.map { it.themeMode }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME] = mode.name }
    }

    val palette: Flow<AppPalette> = settings.map { it.palette }

    suspend fun setPalette(palette: AppPalette) {
        dataStore.edit { it[PALETTE] = palette.name }
    }

    val contentFontSize: Flow<Int> = settings.map { it.contentFontSize }

    suspend fun setContentFontSize(size: Int) {
        val clamped = clampFontSize(size)
        dataStore.edit { it[FONT_SIZE] = clamped }
    }

    companion object {
        const val DEFAULT_FONT_SIZE = 12
        const val MIN_FONT_SIZE = 10
        const val MAX_FONT_SIZE = 18

        fun clampFontSize(size: Int): Int = size.coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE)

        private val THEME = stringPreferencesKey("theme_mode")
        private val PALETTE = stringPreferencesKey("app_palette")
        private val FONT_SIZE = intPreferencesKey("content_font_size")
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
