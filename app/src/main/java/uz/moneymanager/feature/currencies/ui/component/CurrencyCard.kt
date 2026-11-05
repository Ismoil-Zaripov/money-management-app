package uz.moneymanager.feature.currencies.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.component.MmCard
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.CurrencyWithUsage

@Composable
internal fun CurrencyCard(
    item: CurrencyWithUsage,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val isDefault = item.currency.isDefault
    MmCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CurrencyCodeTile(item.currency.code)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.currency.name,
                    style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (isDefault) CurrencyDefaultBadge()
            }
            Spacer(modifier = Modifier.width(8.dp))
            CurrencyUsageChip(item.transactionCount)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                if (isDefault) CurrencyLockedHint()
            }
            CurrencyActions(
                canEdit = !isDefault,
                canDelete = !isDefault && !item.isUsed,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}
