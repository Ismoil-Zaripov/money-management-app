package uz.moneymanager.feature.transactions.presentation

sealed interface TransactionsEvent {
    /** Filter screen confirmed ("Show N Results"): draft values replace the active filter. */
    data class FilterApplied(val draft: TransactionFilterDraft) : TransactionsEvent
    /** Draft edited in the filter screen (null = screen closed); drives the result count only. */
    data class FilterPreviewChanged(val draft: TransactionFilterDraft?) : TransactionsEvent
    /** View setting, applied immediately; not a filter. */
    data class DisplayCurrencySelected(val id: Long) : TransactionsEvent
    data class DeleteConfirmed(val id: Long) : TransactionsEvent
}
