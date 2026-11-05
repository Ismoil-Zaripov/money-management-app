package uz.moneymanager.feature.currencies.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import uz.moneymanager.R

@Composable
internal fun CurrencyActions(
    canEdit: Boolean,
    canDelete: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val disabled = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
    Row {
        IconButton(onClick = onEdit, enabled = canEdit) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.action_edit),
                tint = if (canEdit) MaterialTheme.colorScheme.primary else disabled
            )
        }
        IconButton(onClick = onDelete, enabled = canDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.action_delete),
                tint = if (canDelete) MaterialTheme.colorScheme.error else disabled
            )
        }
    }
}
