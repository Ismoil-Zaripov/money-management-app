package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmBadge
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.domain.Totals
import uz.moneymanager.domain.model.TransactionType

/** Bitta guruh (display yoki fallback): sarlavha + badge + izoh + Income / Expense / Balance kartalari. */
@Composable
internal fun SummaryDetailsSection(
    title: String,
    badge: String,
    note: @Composable (() -> Unit)?,
    totals: Totals,
    currencyCode: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f, fill = false),
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            MmBadge(text = badge)
        }
        note?.invoke()
        SummaryAmountCard(
            label = stringResource(R.string.label_income),
            itemsCount = totals.incomeCount,
            amount = FormatUtils.formatAmount(totals.income, currencyCode, TransactionType.INCOME),
            color = IncomeColor,
            isIncome = true
        )
        SummaryAmountCard(
            label = stringResource(R.string.label_expense),
            itemsCount = totals.expenseCount,
            amount = FormatUtils.formatAmount(totals.expense, currencyCode, TransactionType.EXPENSE),
            color = ExpenseColor,
            isIncome = false
        )
        SummaryBalanceCard(totals = totals, currencyCode = currencyCode)
    }
}
