package uz.moneymanager.feature.rates.ui.component

import java.math.BigDecimal
import java.text.DecimalFormat

fun formatRate(rate: BigDecimal): String {
    val stripped = rate.stripTrailingZeros()
    val pattern = if (stripped.scale() <= 4) "#,##0.####" else "#,##0.######"
    return DecimalFormat(pattern).format(stripped)
}
