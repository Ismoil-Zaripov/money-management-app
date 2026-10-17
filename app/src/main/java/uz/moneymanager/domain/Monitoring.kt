package uz.moneymanager.domain

import uz.moneymanager.data.local.TotalsRow
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

data class MonitoringFilter(
    val categoryId: Long? = null,
    val currencyId: Long? = null,
    val type: TransactionType? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
    /** Currency all amounts are converted to; null = national default (UZS). */
    val displayCurrencyId: Long? = null,
)

data class Totals(
    val income: BigDecimal = BigDecimal.ZERO,
    val expense: BigDecimal = BigDecimal.ZERO,
    val count: Int = 0,
) {
    val balance: BigDecimal get() = income - expense
}

/**
 * [display]  – transactions whose snapshot has a rate for the display currency.
 * [fallback] – transactions created before the display currency existed;
 *              converted to the national currency (UZS) instead.
 */
data class MonitoringTotals(
    val display: Totals = Totals(),
    val fallback: Totals = Totals(),
)

object TotalsCalculator {
    fun calculate(rows: List<TotalsRow>): MonitoringTotals {
        var dIncome = BigDecimal.ZERO
        var dExpense = BigDecimal.ZERO
        var dCount = 0
        var fIncome = BigDecimal.ZERO
        var fExpense = BigDecimal.ZERO
        var fCount = 0
        for (row in rows) {
            if (row.displayRate != null) {
                val v = row.amount.multiply(row.displayRate)
                if (row.type == TransactionType.INCOME) dIncome += v else dExpense += v
                dCount++
            } else if (row.fallbackRate != null) {
                val v = row.amount.multiply(row.fallbackRate)
                if (row.type == TransactionType.INCOME) fIncome += v else fExpense += v
                fCount++
            }
        }
        return MonitoringTotals(
            display = Totals(dIncome, dExpense, dCount),
            fallback = Totals(fIncome, fExpense, fCount),
        )
    }
}
