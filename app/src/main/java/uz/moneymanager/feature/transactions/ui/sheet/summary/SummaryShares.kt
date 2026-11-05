package uz.moneymanager.feature.transactions.ui.sheet.summary

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

/** Income / expense ulushi (%), 1 kasrgacha; yig'indisi doim 100.0 (yoki hajm 0 bo'lsa 0.0 / 0.0). */
internal fun summaryShares(income: BigDecimal, expense: BigDecimal): Pair<BigDecimal, BigDecimal> {
    val total = income + expense
    if (total.signum() <= 0) return BigDecimal.ZERO.setScale(1) to BigDecimal.ZERO.setScale(1)
    val incomeShare = income.multiply(BigDecimal(100)).divide(total, 1, RoundingMode.HALF_UP)
    return incomeShare to BigDecimal(100).setScale(1).subtract(incomeShare)
}

internal fun formatShare(share: BigDecimal): String = String.format(Locale.getDefault(), "%.1f%%", share)
