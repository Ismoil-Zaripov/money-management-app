package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.data.local.CategoryEntity

@Composable
internal fun CategoryChip(
    selectedId: Long?,
    categories: List<CategoryEntity>,
    onSelect: (Long?) -> Unit,
) {
    val selected = categories.firstOrNull { it.id == selectedId }
    FilterMenuChip(
        label = "${stringResource(R.string.filter_category)}: ${selected?.name ?: stringResource(R.string.filter_all)}",
        selected = selectedId != null,
        onClear = { onSelect(null) }
    ) { dismiss ->
        FilterMenuItem(stringResource(R.string.filter_all)) {
            onSelect(null)
            dismiss()
        }
        categories.forEach { cat ->
            FilterMenuItem(cat.name) {
                onSelect(cat.id)
                dismiss()
            }
        }
    }
}
