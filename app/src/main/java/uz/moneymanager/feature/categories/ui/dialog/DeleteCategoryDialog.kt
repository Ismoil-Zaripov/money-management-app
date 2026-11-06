package uz.moneymanager.feature.categories.ui.dialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.CategoryWithUsage

@Composable
fun DeleteCategoryDialog(
    item: CategoryWithUsage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    MmDialog(
        title = stringResource(R.string.action_delete),
        confirmText = stringResource(R.string.action_delete),
        dismissText = stringResource(R.string.action_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Text(
            stringResource(R.string.delete_category_confirm, item.category.name),
            style = MaterialTheme.contentText
        )
    }
}
