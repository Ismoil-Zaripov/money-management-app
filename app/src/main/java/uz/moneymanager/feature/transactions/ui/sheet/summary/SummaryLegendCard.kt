package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.domain.Totals
import uz.moneymanager.domain.model.TransactionType

/** Legenda: Income (ulush), Expense (ulush), keyin Balance — donut ichida emas, uchinchi qator. */
@Composable
internal fun SummaryLegendCard(totals: Totals, currencyCode: String, modifier: Modifier = Modifier) {
    val (incShare, expShare) = remember(totals.income, totals.expense) { summaryShares(totals.income, totals.expense) }
    val incColor = IncomeColor
    val expColor = ExpenseColor
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendRow(
                label = stringResource(R.string.label_income),
                caption = stringResource(R.string.summary_share, formatShare(incShare)),
                amount = FormatUtils.formatAmount(totals.income, currencyCode, TransactionType.INCOME),
                amountColor = incColor,
                dotColor = incColor
            )
            LegendRow(
                label = stringResource(R.string.label_expense),
                caption = stringResource(R.string.summary_share, formatShare(expShare)),
                amount = FormatUtils.formatAmount(totals.expense, currencyCode, TransactionType.EXPENSE),
                amountColor = expColor,
                dotColor = expColor
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            LegendRow(
                label = stringResource(R.string.label_balance),
                amount = FormatUtils.formatAmount(totals.balance, currencyCode, includePositiveSign = true),
                amountColor = balanceColor(totals.balance),
                dotColor = null,
                bold = true
            )
        }
    }
}
