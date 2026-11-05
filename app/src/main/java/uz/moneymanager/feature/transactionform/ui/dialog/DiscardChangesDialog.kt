package uz.moneymanager.feature.transactionform.ui.dialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.core.designsystem.theme.contentText

@Composable
internal fun DiscardChangesDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    MmDialog(
        title = stringResource(R.string.discard_changes_title),
        confirmText = stringResource(R.string.action_discard),
        dismissText = stringResource(R.string.action_keep_editing),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Text(stringResource(R.string.discard_changes_desc), style = MaterialTheme.contentText)
    }
}
