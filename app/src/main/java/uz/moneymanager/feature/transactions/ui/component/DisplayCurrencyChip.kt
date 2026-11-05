package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.data.local.CurrencyEntity

@Composable
internal fun DisplayCurrencyChip(
    displayCurrency: CurrencyEntity?,
    currencies: List<CurrencyEntity>,
    onSelect: (Long) -> Unit,
) {
    FilterMenuChip(
        label = "${stringResource(R.string.display_currency)}: ${displayCurrency?.code ?: "UZS"}",
        selected = true
    ) { dismiss ->
        currencies.forEach { c ->
            FilterMenuItem("${c.code} - ${c.name}") {
                onSelect(c.id)
                dismiss()
            }
        }
    }
}
