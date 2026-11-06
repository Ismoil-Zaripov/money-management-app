package uz.moneymanager.feature.currencies.presentation

import androidx.compose.runtime.Immutable
import uz.moneymanager.data.local.CurrencyWithUsage

@Immutable
data class CurrenciesUiState(
    val currencies: List<CurrencyWithUsage> = emptyList(),
    /** False until the first emission from the data layer; the screen shows its skeleton meanwhile. */
    val loaded: Boolean = false,
)
