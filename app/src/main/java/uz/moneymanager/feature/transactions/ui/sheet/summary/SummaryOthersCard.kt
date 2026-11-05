package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

/** Pie tabidagi "Others" bloki: kursi yo'q operatsiyalar o'z valyutasida (UZS). */
@Composable
internal fun SummaryOthersCard(totals: Totals, currencyCode: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CurrencyExchange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.summary_fallback_title, totals.count),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                MmBadge(text = currencyCode)
            }
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainerLowest) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OthersRow(
                        stringResource(R.string.label_income),
                        FormatUtils.formatAmount(totals.income, currencyCode, TransactionType.INCOME),
                        IncomeColor
                    )
                    OthersRow(
                        stringResource(R.string.label_expense),
                        FormatUtils.formatAmount(totals.expense, currencyCode, TransactionType.EXPENSE),
                        ExpenseColor
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    OthersRow(
                        stringResource(R.string.label_balance),
                        FormatUtils.formatAmount(totals.balance, currencyCode, includePositiveSign = true),
                        balanceColor(totals.balance),
                        bold = true
                    )
                }
            }
            SummaryNote(stringResource(R.string.summary_fallback_note, currencyCode))
        }
    }
}

@Composable
private fun OthersRow(label: String, amount: String, color: Color, bold: Boolean = false) {
    val weight = if (bold) FontWeight.Bold else FontWeight.Medium
    AdaptiveAmountRow(
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, style = MaterialTheme.contentText.copy(fontWeight = weight), color = MaterialTheme.colorScheme.onSurface) },
        amount = {
            Text(amount, style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold), color = color, maxLines = 1, softWrap = false)
        }
    )
}
