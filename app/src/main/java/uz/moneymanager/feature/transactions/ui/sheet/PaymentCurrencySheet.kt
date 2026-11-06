package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.feature.transactions.ui.filter.section.CurrencySection
import uz.moneymanager.feature.transactions.ui.filter.section.PaymentMethodSection

/** Payment method and currency pickers in one sheet (each single-choice, tap again to clear). */
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
        PaymentMethodSection(paymentMethods = paymentMethods, selectedId = paymentId, onSelect = { paymentId = it })
        Spacer(modifier = Modifier.height(20.dp))
        CurrencySection(currencies = currencies, selectedId = currencyId, onSelect = { currencyId = it })
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
