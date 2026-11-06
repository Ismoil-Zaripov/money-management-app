package uz.moneymanager.ui

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import uz.moneymanager.domain.model.AppLanguage
import uz.moneymanager.feature.categories.presentation.CategoriesUiState
import uz.moneymanager.feature.currencies.presentation.CurrenciesUiState
import uz.moneymanager.feature.paymentmethods.presentation.PaymentMethodsUiState
import uz.moneymanager.feature.paymentmethods.presentation.PaymentMethodsViewModel
import uz.moneymanager.feature.rates.presentation.RatesUiState
import uz.moneymanager.feature.settings.presentation.SettingsUiState
import uz.moneymanager.feature.transactionform.presentation.FormUiState

/** T21 (A): every page state starts "not loaded" so its skeleton shows before the first data. */
class PageLoadedFlagTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpMainDispatcher() {
            System.setProperty("kotlinx.coroutines.fast.service.loader", "false")
        }
    }

    @Test
    fun defaultStates_startNotLoaded() {
        assertFalse(CategoriesUiState().loaded)
        assertFalse(CurrenciesUiState().loaded)
        assertFalse(PaymentMethodsUiState().loaded)
        assertFalse(RatesUiState().loaded)
        assertFalse(SettingsUiState(language = AppLanguage.EN).loaded)
    }

    @Test
    fun settings_loadedOnceFontSizeIsRead() {
        assertTrue(SettingsUiState(language = AppLanguage.EN, contentFontSize = 16).loaded)
    }

    @Test
    fun form_loadedFollowsIsLoading() {
        assertTrue(FormUiState().loaded)
        assertFalse(FormUiState(isLoading = true).loaded)
    }

    @Test
    fun paymentMethodsViewModel_becomesLoadedAfterFirstEmission() = runBlocking {
        val env = FakeRepositoriesEnv()
        val viewModel = PaymentMethodsViewModel(env.paymentMethodRepository)
        val job = launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) {
            viewModel.uiState.collect {}
        }
        assertTrue(viewModel.uiState.value.loaded)
        job.cancel()
    }
}
