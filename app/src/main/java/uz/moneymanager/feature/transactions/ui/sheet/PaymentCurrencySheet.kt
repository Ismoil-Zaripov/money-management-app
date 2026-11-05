package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity

/** Payment method and currency pickers in one sheet (each single-choice, tap again to clear). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentCurrencySheet(
    paymentMethods: List<PaymentMethodEntity>,
    currencies: List<CurrencyEntity>,
    selectedPaymentMethodId: Long?,
    selectedCurrencyId: Long?,
    onApply: (paymentMethodId: Long?, currencyId: Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    var paymentId by remember { mutableStateOf(selectedPaymentMethodId) }
    var currencyId by remember { mutableStateOf(selectedCurrencyId) }

    FilterSheetContainer(title = null, onDismiss = onDismiss) {
        SectionTitle(stringResource(R.string.filter_payment_method))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            paymentMethods.forEach { pm ->
                FilterChip(
                    selected = paymentId == pm.id,
                    onClick = { paymentId = if (paymentId == pm.id) null else pm.id },
                    label = { Text(pm.name, style = MaterialTheme.contentText, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle(stringResource(R.string.filter_currency))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currencies.forEach { c ->
                FilterChip(
                    selected = currencyId == c.id,
                    onClick = { currencyId = if (currencyId == c.id) null else c.id },
                    label = { Text(c.code, style = MaterialTheme.contentText) }
                )
            }
        }
        FilterSheetFooter(
            onReset = {
                paymentId = null
                currencyId = null
            },
            onApply = { onApply(paymentId, currencyId) },
            applyLabel = applyLabel(listOfNotNull(paymentId, currencyId).size)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
