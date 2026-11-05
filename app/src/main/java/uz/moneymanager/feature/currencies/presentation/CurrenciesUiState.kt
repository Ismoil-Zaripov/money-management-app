package uz.moneymanager.feature.currencies.presentation

import androidx.compose.runtime.Immutable
import uz.moneymanager.data.local.CurrencyWithUsage

@Immutable
data class CurrenciesUiState(
    val currencies: List<CurrencyWithUsage> = emptyList(),
)
