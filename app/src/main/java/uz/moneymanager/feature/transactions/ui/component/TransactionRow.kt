package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.icon.categoryIcon
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.AmountKind
import uz.moneymanager.core.designsystem.component.AmountText
import uz.moneymanager.core.designsystem.component.MmBadge
import uz.moneymanager.core.designsystem.component.MmCard
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.designsystem.theme.moneyColors
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.feature.transactions.presentation.TransactionListItemUiModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionRow(
    item: TransactionListItemUiModel.Item,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val isIncome = item.type == TransactionType.INCOME
    MmCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(onClick = onClick, onLongClick = onDelete)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryIconCircle(categoryKey = item.categoryKey, isIncome = isIncome)
            Column(modifier = Modifier.weight(1f)) {
                TransactionTitleLine(title = item.title, isIncome = isIncome)
                Text(
                    text = listOf(item.description, item.paymentMethodName)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    style = MaterialTheme.contentText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                AmountText(
                    text = item.amountText,
                    kind = if (isIncome) AmountKind.Income else AmountKind.Expense,
                    style = MaterialTheme.contentText
                )
                if (item.timeText.isNotEmpty()) {
                    Text(
                        text = item.timeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionTitleLine(title: String, isIncome: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (isIncome) {
            MmBadge(text = stringResource(R.string.type_income))
        }
    }
}

@Composable
private fun CategoryIconCircle(categoryKey: String, isIncome: Boolean) {
    val icon = remember(categoryKey) { categoryIcon(categoryKey) }
    val income = MaterialTheme.moneyColors.income
    val container = if (isIncome) income.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceContainerHighest
    val tint = if (isIncome) income else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(container, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}
