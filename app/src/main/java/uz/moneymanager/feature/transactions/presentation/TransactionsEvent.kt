package uz.moneymanager.feature.transactions.presentation

import java.time.LocalDate
import uz.moneymanager.domain.model.TransactionType

sealed interface TransactionsEvent {
    data class CategorySelected(val id: Long?) : TransactionsEvent
    data class CurrencyFilterSelected(val id: Long?) : TransactionsEvent
    data class PaymentMethodSelected(val id: Long?) : TransactionsEvent
    data class DisplayCurrencySelected(val id: Long) : TransactionsEvent
    data class TypeSelected(val type: TransactionType?) : TransactionsEvent
    data class DateRangeSelected(val from: LocalDate?, val to: LocalDate?) : TransactionsEvent
    data class DeleteConfirmed(val id: Long) : TransactionsEvent
    object ResetFilters : TransactionsEvent
}
