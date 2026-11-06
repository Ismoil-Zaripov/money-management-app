package uz.moneymanager.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

/** M3 alert dialog with the app's 28dp corner radius. */
@Composable
fun MmDialog(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String? = null,
    confirmEnabled: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = { Text(title) },
        text = content,
        confirmButton = { TextButton(onClick = onConfirm, enabled = confirmEnabled) { Text(confirmText) } },
        dismissButton = dismissText?.let { { TextButton(onClick = onDismiss) { Text(it) } } },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    )
}

@MmPreviews
@Composable
private fun MmDialogPreview() = MmPreviewHost {
    MmDialog(title = "Delete?", confirmText = "Delete", dismissText = "Cancel", onConfirm = {}, onDismiss = {}) {
        Text("This cannot be undone.")
    }
}
