package uz.moneymanager.feature.rates.presentation

import androidx.compose.runtime.Immutable
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.ExchangeRateRow
import java.time.LocalDate

@Immutable
data class RatesUiState(
    val date: LocalDate = LocalDate.now(),
    val currencies: List<CurrencyEntity> = emptyList(),
    val rates: List<ExchangeRateRow> = emptyList(),
    val isLoading: Boolean = false,
    /** False until the first emission from the data layer; the screen shows its skeleton meanwhile. */
    val loaded: Boolean = false,
)
