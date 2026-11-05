package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.domain.Totals
import uz.moneymanager.domain.model.TransactionType

@Composable
fun SummaryDonutChartView(
    totals: Totals,
    currencyCode: String,
    title: String? = null,
) {
    val density = LocalDensity.current
    val strokeWidthPx = remember(density) { with(density) { 26.dp.toPx() } }
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val incColor = IncomeColor
    val expColor = ExpenseColor

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val incFloat = totals.income.toFloat()
        val expFloat = totals.expense.toFloat()
        val totalVolume = incFloat + expFloat

        val incPct = if (totalVolume > 0f) (incFloat / totalVolume * 100).toInt() else 0
        val expPct = if (totalVolume > 0f) 100 - incPct else 0

        val emptyText = stringResource(R.string.empty_transactions)
        val incomeLabel = stringResource(R.string.label_income)
        val expenseLabel = stringResource(R.string.label_expense)
        val balanceLabel = stringResource(R.string.label_balance)
        val balanceText = FormatUtils.formatAmount(totals.balance, currencyCode, includePositiveSign = true)
        val chartSemanticsDesc = if (totalVolume <= 0f) {
            emptyText
        } else {
            "$incomeLabel $incPct%, $expenseLabel $expPct%, $balanceLabel $balanceText"
        }

        // Donut markazi ataylab bo'sh: balans legendada ko'rsatiladi
        Canvas(
            modifier = Modifier
                .size(160.dp)
                .padding(14.dp)
                .semantics { contentDescription = chartSemanticsDesc }
        ) {
            if (totalVolume <= 0f) {
                drawCircle(
                    color = outlineVariantColor,
                    style = Stroke(width = strokeWidthPx)
                )
            } else {
                val incomeAngle = (incFloat / totalVolume) * 360f
                val expenseAngle = (expFloat / totalVolume) * 360f
                val gapAngle = if (incPct > 0 && expPct > 0) 3f else 0f
                val adjInc = (incomeAngle - gapAngle).coerceAtLeast(0f)
                val adjExp = (expenseAngle - gapAngle).coerceAtLeast(0f)

                drawArc(
                    color = incColor,
                    startAngle = -90f + gapAngle / 2,
                    sweepAngle = adjInc,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
                drawArc(
                    color = expColor,
                    startAngle = -90f + incomeAngle + gapAngle / 2,
                    sweepAngle = adjExp,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legenda: Income, Expense, divider, Balance
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LegendRow(
                label = "$incomeLabel ($incPct%)",
                amount = FormatUtils.formatAmount(totals.income, currencyCode, TransactionType.INCOME),
                amountColor = incColor,
                dotColor = incColor
            )
            LegendRow(
                label = "$expenseLabel ($expPct%)",
                amount = FormatUtils.formatAmount(totals.expense, currencyCode, TransactionType.EXPENSE),
                amountColor = expColor,
                dotColor = expColor
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            LegendRow(
                label = balanceLabel,
                amount = balanceText,
                amountColor = balanceColor(totals.balance),
                dotColor = null,
                bold = true
            )
        }
    }
}

