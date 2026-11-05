package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.data.local.CurrencyEntity

@Composable
internal fun CurrencyFilterChip(
    selectedId: Long?,
    currencies: List<CurrencyEntity>,
    onSelect: (Long?) -> Unit,
) {
    val selected = currencies.firstOrNull { it.id == selectedId }
    FilterMenuChip(
        label = "${stringResource(R.string.filter_currency)}: ${selected?.code ?: stringResource(R.string.filter_all)}",
        selected = selectedId != null,
        onClear = { onSelect(null) }
    ) { dismiss ->
        FilterMenuItem(stringResource(R.string.filter_all)) {
            onSelect(null)
            dismiss()
        }
        currencies.forEach { c ->
            FilterMenuItem(c.code) {
                onSelect(c.id)
                dismiss()
            }
        }
    }
}
