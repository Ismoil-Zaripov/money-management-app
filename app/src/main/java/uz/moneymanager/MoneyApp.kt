package uz.moneymanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import uz.moneymanager.data.repository.LanguageStore
import javax.inject.Inject

@HiltAndroidApp
class MoneyApp : Application() {

    @Inject
    lateinit var languageStore: LanguageStore

    override fun onCreate() {
        super.onCreate()
        // Apply default (EN) or saved per-app language on startup
        languageStore.applySaved()
    }
}
