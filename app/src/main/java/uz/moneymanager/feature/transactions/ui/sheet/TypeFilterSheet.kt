package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.feature.transactions.ui.filter.section.TypeSection

/** All / Income / Expense (single choice). */
@Composable
fun TypeFilterSheet(
    selected: TransactionType?,
    onApply: (TransactionType?) -> Unit,
    onDismiss: () -> Unit,
) {
    var pending by remember { mutableStateOf(selected) }

    FilterSheetContainer(title = stringResource(R.string.filter_type), onDismiss = onDismiss) {
        TypeSection(selected = pending, onSelect = { pending = it })
        FilterSheetFooter(
            onReset = { pending = null },
            onApply = { onApply(pending) },
            applyLabel = applyLabel(if (pending != null) 1 else 0)
        )
    }
}
