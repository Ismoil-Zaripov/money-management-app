package uz.moneymanager.feature.transactionform.ui.dialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.core.designsystem.theme.contentText

@Composable
internal fun NoInternetDialog(onDismiss: () -> Unit) {
    MmDialog(
        title = stringResource(R.string.dialog_no_internet_title),
        confirmText = stringResource(R.string.action_understood),
        onConfirm = onDismiss,
        onDismiss = onDismiss
    ) {
        Text(stringResource(R.string.dialog_no_internet_desc), style = MaterialTheme.contentText)
    }
}
