package uz.moneymanager.feature.transactions.presentation

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.domain.model.TransactionType

@Immutable
sealed interface TransactionListItemUiModel {
    data class Header(
        val date: LocalDate,
        val dateText: String,
    ) : TransactionListItemUiModel

    data class Item(
        val id: Long,
        val title: String,
        val paymentMethodName: String = "",
        val amountText: String,
        val type: TransactionType,
        val date: LocalDate,
        val categoryKey: String = "",
        val description: String = "",
        val timeText: String = "",
    ) : TransactionListItemUiModel
}

typealias TransactionUiItem = TransactionListItemUiModel.Item

object TransactionDateSeparator {
    /**
     * Determines whether a date header should be inserted between [before] and [after].
     * Rule:
     * - If [after] is null -> returns null (end of list, no header).
     * - If [before] is null (start of list) OR [before.date] != [after.date] -> returns a [TransactionListItemUiModel.Header] for [after.date].
     * - Otherwise (same date) -> returns null.
     */
    fun createSeparator(
        before: TransactionListItemUiModel.Item?,
        after: TransactionListItemUiModel.Item?,
        dateFormatter: (LocalDate) -> String = { FormatUtils.formatHeaderDate(it) },
    ): TransactionListItemUiModel.Header? {
        return if (after != null && (before == null || before.date != after.date)) {
            TransactionListItemUiModel.Header(
                date = after.date,
                dateText = dateFormatter(after.date),
            )
        } else {
            null
        }
    }
}
