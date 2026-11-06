package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.feature.transactions.ui.filter.section.CategorySection

/** Single-choice category list with search; [onApply] gets the picked id or null for "all". */
@Composable
fun CategoryFilterSheet(
    categories: List<CategoryEntity>,
    selectedId: Long?,
    onApply: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    var pending by remember { mutableStateOf(selectedId) }

    FilterSheetContainer(title = stringResource(R.string.sheet_category_title), onDismiss = onDismiss) {
        CategorySection(categories = categories, selectedId = pending, onSelect = { pending = it })
        FilterSheetFooter(
            onReset = { pending = null },
            onApply = { onApply(pending) },
            applyLabel = applyLabel(if (pending != null) 1 else 0)
        )
    }
}
