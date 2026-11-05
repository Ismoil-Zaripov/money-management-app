package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.moneymanager.data.local.CurrencyEntity

/** Valyuta tanlash: chip'lar qatori (tanlash id bo'yicha, avvalgi dropdown bilan bir xil ma'noda). */
@Composable
internal fun CurrencyChips(
    currencies: List<CurrencyEntity>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        contentPadding = PaddingValues(0.dp)
    ) {
        items(currencies, key = { it.id }) { currency ->
            FormFilterChip(
                selected = currency.id == selectedId,
                onClick = { onSelect(currency.id) },
                label = currency.code
            )
        }
    }
}
