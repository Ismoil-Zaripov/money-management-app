package uz.moneymanager.feature.transactions.presentation

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.model.TransactionType

/**
 * Filter values being edited in the filter UI. Applied to the real [MonitoringFilter] only on confirm.
 * Display currency is not a filter and is not part of the draft.
 */
@Immutable
data class TransactionFilterDraft(
    val type: TransactionType? = null,
    val categoryId: Long? = null,
    val paymentMethodId: Long? = null,
    val currencyId: Long? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
) {
    /** Number of chosen filters; the date range counts as one. */
    val activeCount: Int
        get() = listOf(type, categoryId, paymentMethodId, currencyId).count { it != null } +
            if (from != null || to != null) 1 else 0

    fun cleared(): TransactionFilterDraft = TransactionFilterDraft()

    /** [base] keeps everything that is not a filter (display currency). */
    fun toFilter(base: MonitoringFilter): MonitoringFilter = base.copy(
        categoryId = categoryId,
        currencyId = currencyId,
        paymentMethodId = paymentMethodId,
        type = type,
        from = from,
        to = to,
    )

    companion object {
        fun of(filter: MonitoringFilter): TransactionFilterDraft = TransactionFilterDraft(
            type = filter.type,
            categoryId = filter.categoryId,
            paymentMethodId = filter.paymentMethodId,
            currencyId = filter.currencyId,
            from = filter.from,
            to = filter.to,
        )
    }
}
