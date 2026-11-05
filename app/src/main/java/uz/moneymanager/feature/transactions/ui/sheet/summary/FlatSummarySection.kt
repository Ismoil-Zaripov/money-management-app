package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
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
fun FlatSummarySection(
    title: String,
    subtitle: String?,
    totals: Totals,
    currencyCode: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.contentText,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Kirim qatori
        SummaryRowItem(
            label = stringResource(R.string.label_income),
            amount = FormatUtils.formatAmount(totals.income, currencyCode, TransactionType.INCOME),
            color = IncomeColor,
            isIncome = true
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        // Chiqim qatori
        SummaryRowItem(
            label = stringResource(R.string.label_expense),
            amount = FormatUtils.formatAmount(totals.expense, currencyCode, TransactionType.EXPENSE),
            color = ExpenseColor,
            isIncome = false
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        // Balans qatori
        SummaryRowItem(
            label = stringResource(R.string.label_balance),
            amount = FormatUtils.formatAmount(totals.balance, currencyCode, includePositiveSign = true),
            color = balanceColor(totals.balance),
            isBalance = true
        )
    }
}
