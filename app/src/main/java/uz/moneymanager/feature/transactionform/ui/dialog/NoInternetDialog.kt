package uz.moneymanager.feature.transactionform.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText

@Composable
internal fun NoInternetDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_no_internet_title)) },
        text = { Text(stringResource(R.string.dialog_no_internet_desc), style = MaterialTheme.contentText) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_understood))
            }
        }
    )
}
