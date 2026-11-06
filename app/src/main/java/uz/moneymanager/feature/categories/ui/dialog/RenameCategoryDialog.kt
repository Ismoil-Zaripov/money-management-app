package uz.moneymanager.feature.categories.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.data.local.CategoryWithUsage

@Composable
fun RenameCategoryDialog(
    item: CategoryWithUsage,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(item.category.name) }
    val count = item.transactionCount

    MmDialog(
        title = stringResource(R.string.action_rename_category),
        confirmText = stringResource(R.string.action_save_changes),
        dismissText = stringResource(R.string.action_cancel),
        onConfirm = { onConfirm(name) },
        onDismiss = onDismiss
    ) {
        Column {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.label_category_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (count > 0) {
                Text(
                    text = pluralStringResource(R.plurals.category_linked_records, count, count),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
