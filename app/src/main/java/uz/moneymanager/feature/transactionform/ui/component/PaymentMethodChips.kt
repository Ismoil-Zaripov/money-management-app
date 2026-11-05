package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.data.local.PaymentMethodEntity

/** To'lov usuli: dinamik chip'lar va "+ Qo'shish" chip'i. */
@Composable
internal fun PaymentMethodChips(
    paymentMethods: List<PaymentMethodEntity>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.label_payment_method),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(paymentMethods, key = { it.id }) { pm ->
                FormFilterChip(
                    selected = pm.id == selectedId,
                    onClick = { onSelect(pm.id) },
                    label = pm.name
                )
            }
            item {
                FormFilterChip(
                    selected = false,
                    onClick = onAddClick,
                    label = "+ ${stringResource(R.string.action_add)}"
                )
            }
        }
    }
}
