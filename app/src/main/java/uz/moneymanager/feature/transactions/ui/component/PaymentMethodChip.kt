package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.data.local.PaymentMethodEntity

@Composable
internal fun PaymentMethodChip(
    selectedId: Long?,
    paymentMethods: List<PaymentMethodEntity>,
    onSelect: (Long?) -> Unit,
) {
    val selected = paymentMethods.firstOrNull { it.id == selectedId }
    FilterMenuChip(
        label = "${stringResource(R.string.filter_payment_method)}: ${selected?.name ?: stringResource(R.string.filter_all)}",
        selected = selectedId != null,
        onClear = { onSelect(null) }
    ) { dismiss ->
        FilterMenuItem(stringResource(R.string.filter_all)) {
            onSelect(null)
            dismiss()
        }
        paymentMethods.forEach { pm ->
            FilterMenuItem(pm.name) {
                onSelect(pm.id)
                dismiss()
            }
        }
    }
}
