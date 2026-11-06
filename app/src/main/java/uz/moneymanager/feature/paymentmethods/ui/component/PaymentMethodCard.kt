package uz.moneymanager.feature.paymentmethods.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmBadge
import uz.moneymanager.core.designsystem.component.MmCard
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.PaymentMethodWithUsage

@Composable
fun PaymentMethodCard(
    item: PaymentMethodWithUsage,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDefault = item.paymentMethod.isDefault
    val count = item.transactionCount
    MmCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PaymentMethodIconTile(isDefault = isDefault)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = item.paymentMethod.name,
                        style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isDefault) MmBadge(stringResource(R.string.payment_method_default_badge))
                }
                PaymentMethodBadges(isDefault = isDefault, count = count)
            }
            PaymentMethodActions(isDefault = isDefault, isUsed = count > 0, onRename = onRename, onDelete = onDelete)
        }
    }
}

@Composable
private fun PaymentMethodBadges(isDefault: Boolean, count: Int) {
    val neutralContainer = MaterialTheme.colorScheme.surfaceContainerHighest
    val neutralContent = MaterialTheme.colorScheme.onSurfaceVariant
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        MmBadge(pluralStringResource(R.plurals.payment_method_transactions_count, count, count))
        if (isDefault) {
            MmBadge(stringResource(R.string.payment_method_protected), containerColor = neutralContainer, contentColor = neutralContent)
        } else if (count == 0) {
            MmBadge(stringResource(R.string.payment_method_unused), containerColor = neutralContainer, contentColor = neutralContent)
        }
    }
}
