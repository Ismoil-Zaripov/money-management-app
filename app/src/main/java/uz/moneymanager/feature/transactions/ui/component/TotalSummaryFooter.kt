package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.format.FormatUtils

/** Footer: jami summa. Bosilganda Summary Report sheet ochiladi. */
@Composable
fun TotalSummaryFooter(
    balance: BigDecimal,
    displayCode: String,
    isAmountVisible: Boolean,
    onToggleAmountVisibility: () -> Unit,
    onClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    IconButton(
                        onClick = onToggleAmountVisibility,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isAmountVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = stringResource(R.string.action_show_footer),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_show_footer),
                        style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val totalText = if (isAmountVisible) {
                    FormatUtils.formatAmount(balance, displayCode, includePositiveSign = true)
                } else {
                    "••••••"
                }

                Text(
                    text = totalText,
                    style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    color = if (isAmountVisible) {
                        if (balance >= BigDecimal.ZERO) IncomeColor else ExpenseColor
                    } else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    }
}
