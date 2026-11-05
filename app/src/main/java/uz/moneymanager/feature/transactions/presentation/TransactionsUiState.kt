package uz.moneymanager.feature.transactions.presentation

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import kotlinx.coroutines.flow.filter
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.MonitoringTotals

/** Ekranning yagona state'i (Paging oqimi [TransactionsViewModel.transactions] alohida). */
@Immutable
data class TransactionsUiState(
    val filter: MonitoringFilter = MonitoringFilter(),
    val currencies: List<CurrencyEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val paymentMethods: List<PaymentMethodEntity> = emptyList(),
    val defaultCurrency: CurrencyEntity? = null,
    val totals: MonitoringTotals = MonitoringTotals(),
    val dailyTotals: Map<LocalDate, MonitoringTotals> = emptyMap(),
) {
    val displayCurrency: CurrencyEntity?
        get() = currencies.firstOrNull { it.id == filter.displayCurrencyId } ?: defaultCurrency

    val displayCode: String get() = displayCurrency?.code ?: "UZS"

    val fallbackCode: String get() = defaultCurrency?.code ?: "UZS"

    val showFallback: Boolean
        get() = totals.fallback.count > 0 && displayCurrency?.id != defaultCurrency?.id
}
