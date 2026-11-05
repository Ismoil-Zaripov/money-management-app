package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.domain.model.TransactionType

@Composable
internal fun TypeChip(
    type: TransactionType?,
    onSelect: (TransactionType?) -> Unit,
) {
    val typeLabel = when (type) {
        TransactionType.INCOME -> stringResource(R.string.type_income)
        TransactionType.EXPENSE -> stringResource(R.string.type_expense)
        null -> stringResource(R.string.filter_all)
    }
    FilterMenuChip(
        label = "${stringResource(R.string.filter_type)}: $typeLabel",
        selected = type != null,
        onClear = { onSelect(null) }
    ) { dismiss ->
        FilterMenuItem(stringResource(R.string.filter_all)) {
            onSelect(null)
            dismiss()
        }
        FilterMenuItem(stringResource(R.string.type_income)) {
            onSelect(TransactionType.INCOME)
            dismiss()
        }
        FilterMenuItem(stringResource(R.string.type_expense)) {
            onSelect(TransactionType.EXPENSE)
            dismiss()
        }
    }
}
