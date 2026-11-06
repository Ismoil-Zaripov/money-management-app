package uz.moneymanager.feature.paymentmethods.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R

/**
 * Edit is always available. Delete is disabled for the default method; for a used method it stays
 * clickable (dimmed) so the repository reports `err_payment_method_in_use` (behavior unchanged).
 */
@Composable
fun PaymentMethodActions(
    isDefault: Boolean,
    isUsed: Boolean,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Row {
        IconButton(onClick = onRename) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.action_edit),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onDelete, enabled = !isDefault) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.action_delete),
                tint = if (isUsed || isDefault) {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
