package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.domain.Totals

/** Donut (markazda matn yo'q) + "Cash flow distribution" izohi. [active] true bo'lganda yengil ochilish animatsiyasi. */
@Composable
internal fun SummaryDonutChartView(
    totals: Totals,
    currencyCode: String,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val strokeWidthPx = remember(density) { with(density) { 28.dp.toPx() } }
    val emptyColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val incColor = IncomeColor
    val expColor = ExpenseColor
    val progress by animateFloatAsState(if (active) 1f else 0f, tween(600), label = "donut")

    val incFloat = totals.income.toFloat()
    val expFloat = totals.expense.toFloat()
    val volume = incFloat + expFloat
    val (incShare, expShare) = remember(totals.income, totals.expense) { summaryShares(totals.income, totals.expense) }
    val description = if (volume <= 0f) {
        stringResource(R.string.empty_transactions)
    } else {
        "${stringResource(R.string.label_income)} ${formatShare(incShare)}, " +
            "${stringResource(R.string.label_expense)} ${formatShare(expShare)}, " +
            "${stringResource(R.string.label_balance)} " +
            FormatUtils.formatAmount(totals.balance, currencyCode, includePositiveSign = true)
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
                .semantics { contentDescription = description }
        ) {
            if (volume <= 0f) {
                drawCircle(color = emptyColor, style = Stroke(width = strokeWidthPx))
            } else {
                val incomeAngle = incFloat / volume * 360f
                val expenseAngle = expFloat / volume * 360f
                val gap = if (incFloat > 0f && expFloat > 0f) 3f else 0f
                val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                drawArc(
                    color = incColor,
                    startAngle = -90f + gap / 2,
                    sweepAngle = (incomeAngle - gap).coerceAtLeast(0f) * progress,
                    useCenter = false,
                    style = stroke
                )
                drawArc(
                    color = expColor,
                    startAngle = -90f + incomeAngle + gap / 2,
                    sweepAngle = (expenseAngle - gap).coerceAtLeast(0f) * progress,
                    useCenter = false,
                    style = stroke
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.summary_cash_flow, currencyCode),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
