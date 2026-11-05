package uz.moneymanager.feature.transactionform.presentation

import java.time.LocalDate
import uz.moneymanager.data.local.CurrencyEntity

sealed interface FormEvent {
    data object Saved : FormEvent
    data object Deleted : FormEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : FormEvent
    data class ShowMissingRates(
        val base: CurrencyEntity,
        val date: LocalDate,
        val missing: List<CurrencyEntity>,
        val suggestedRates: Map<Long, String>,
    ) : FormEvent
}
