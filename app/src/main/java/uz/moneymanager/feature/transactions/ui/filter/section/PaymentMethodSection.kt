package uz.moneymanager.feature.transactions.ui.filter.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.PaymentMethodEntity

/** Single-choice payment method chips; tapping the selected one clears it. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentMethodSection(
    paymentMethods: List<PaymentMethodEntity>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit,
) {
    FilterSectionTitle(stringResource(R.string.filter_payment_method))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        paymentMethods.forEach { pm ->
            FilterChip(
                selected = selectedId == pm.id,
                onClick = { onSelect(if (selectedId == pm.id) null else pm.id) },
                label = { Text(pm.name, style = MaterialTheme.contentText, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}
