package uz.moneymanager.feature.transactions.ui.filter.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.CurrencyEntity

/** Currency all amounts are converted to. Not a filter: it applies immediately and cannot be cleared. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisplayCurrencySection(
    currencies: List<CurrencyEntity>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
) {
    FilterSectionTitle(stringResource(R.string.filter_display_currency))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        currencies.forEach { c ->
            FilterChip(
                selected = selectedId == c.id,
                onClick = { onSelect(c.id) },
                label = { Text(c.code, style = MaterialTheme.contentText) }
            )
        }
    }
}
