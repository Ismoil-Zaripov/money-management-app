package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.domain.MonitoringTotals

@Composable
fun DateHeader(
    dateText: String,
    totals: MonitoringTotals?,
    displayCode: String,
    fallbackCode: String,
) {
    val baseStyle = MaterialTheme.contentText

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateText,
                style = baseStyle.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            if (totals != null) {
                val hasDisplay = totals.display.count > 0
                val hasFallback = totals.fallback.count > 0 && displayCode != fallbackCode

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasDisplay) {
                        val isIncome = totals.display.balance >= BigDecimal.ZERO
                        Text(
                            text = FormatUtils.formatAmount(totals.display.balance, displayCode, includePositiveSign = true),
                            style = baseStyle.copy(fontWeight = FontWeight.Bold),
                            color = if (isIncome) IncomeColor else ExpenseColor,
                            maxLines = 1
                        )
                    } else if (!hasFallback) {
                        Text(
                            text = FormatUtils.formatAmount(BigDecimal.ZERO, displayCode, includePositiveSign = false),
                            style = baseStyle.copy(fontWeight = FontWeight.Bold),
                            color = IncomeColor,
                            maxLines = 1
                        )
                    }

                    if (hasFallback) {
                        val isIncome = totals.fallback.balance >= BigDecimal.ZERO
                        Text(
                            text = FormatUtils.formatAmount(totals.fallback.balance, fallbackCode, includePositiveSign = true),
                            style = baseStyle.copy(fontWeight = FontWeight.Bold),
                            color = if (isIncome) IncomeColor else ExpenseColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
