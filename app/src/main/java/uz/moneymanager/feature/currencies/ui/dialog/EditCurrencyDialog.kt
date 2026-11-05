package uz.moneymanager.feature.currencies.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.data.local.CurrencyWithUsage
import uz.moneymanager.feature.currencies.ui.component.CurrencyFormFields

@Composable
internal fun EditCurrencyDialog(
    item: CurrencyWithUsage,
    onConfirm: (code: String, name: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var code by remember { mutableStateOf(item.currency.code) }
    var name by remember { mutableStateOf(item.currency.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.action_edit_currency)) },
        text = {
            CurrencyFormFields(
                code = code,
                name = name,
                onCodeChange = { code = it },
                onNameChange = { name = it },
                codeEnabled = !item.isUsed && !item.currency.isDefault
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(code, name) }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
