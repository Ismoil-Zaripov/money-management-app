package uz.moneymanager.domain

import uz.moneymanager.data.local.DailyTotalsRow
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

object DailyTotalsCalculator {
    fun calculate(rows: List<DailyTotalsRow>): Map<LocalDate, MonitoringTotals> {
        val map = mutableMapOf<LocalDate, MutableList<TotalsRow>>()
        for (row in rows) {
            val list = map.getOrPut(row.date) { mutableListOf() }
            list.add(TotalsRow(row.type, row.amount, row.displayRate, row.fallbackRate))
        }
        return map.mapValues { (_, list) -> TotalsCalculator.calculate(list) }
    }
}

/**
 * Result of converting a transaction amount using snapshot exchange rates.
 * [originalAmount] and [originalCurrencyCode] are non-null only when the target currency
 * differs from the transaction's original currency.
 */
data class ConvertedTransactionAmount(
    val amount: BigDecimal,
    val currencyCode: String,
    val originalAmount: BigDecimal? = null,
    val originalCurrencyCode: String? = null,
)

object TransactionAmountCalculator {
    /**
     * Converts a transaction amount following the exact same rules as [TotalsCalculator]:
     * 1. If [displayRate] is present, convert to [displayCurrencyCode] (`amount * displayRate`).
     * 2. Else if [fallbackRate] is present, convert to [fallbackCurrencyCode] (`amount * fallbackRate`).
     * 3. Else, leave in the transaction's original [currencyCode] (`amount`).
     *
     * If the resulting currency differs from the original [currencyCode], the original
     * amount and currency code are preserved in the result for secondary display.
     */
    fun calculate(
        amount: BigDecimal,
        currencyCode: String,
        displayCurrencyCode: String,
        fallbackCurrencyCode: String,
        displayRate: BigDecimal?,
        fallbackRate: BigDecimal?,
    ): ConvertedTransactionAmount {
        val (targetAmount, targetCode) = when {
            displayRate != null -> amount.multiply(displayRate) to displayCurrencyCode
            fallbackRate != null -> amount.multiply(fallbackRate) to fallbackCurrencyCode
            else -> amount to currencyCode
        }

        val hasDifferentCurrency = currencyCode != targetCode
        return ConvertedTransactionAmount(
            amount = targetAmount,
            currencyCode = targetCode,
            originalAmount = if (hasDifferentCurrency) amount else null,
            originalCurrencyCode = if (hasDifferentCurrency) currencyCode else null,
        )
    }
}
