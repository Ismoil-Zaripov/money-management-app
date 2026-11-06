package uz.moneymanager.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import uz.moneymanager.domain.model.AppPalette
import java.io.File

class SettingsRepositoryPaletteTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private val job = Job()

    private fun dataStore() = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(Dispatchers.Unconfined + job),
        produceFile = { File(tmp.root, "settings.preferences_pb") },
    )

    @Test
    fun noStoredValue_usesDefaultPalette() = runBlocking {
        val repo = SettingsRepository(dataStore())
        assertEquals(AppPalette.CORPORATE_BLUE, repo.settings.first().palette)
        job.cancel()
    }

    @Test
    fun selectedPalette_isPersistedAndEmitted() = runBlocking {
        val repo = SettingsRepository(dataStore())
        AppPalette.entries.forEach {
            repo.setPalette(it)
            assertEquals(it, repo.palette.first())
        }
        job.cancel()
    }

    @Test
    fun removedStoredPalette_fallsBackToDefault() = runBlocking {
        val store = dataStore()
        store.edit { it[stringPreferencesKey("app_palette")] = "EMERALD" }
        assertEquals(AppPalette.DEFAULT, SettingsRepository(store).palette.first())
        job.cancel()
    }
}
