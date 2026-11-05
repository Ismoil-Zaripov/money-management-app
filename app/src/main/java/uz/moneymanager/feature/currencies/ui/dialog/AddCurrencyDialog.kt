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
import uz.moneymanager.feature.currencies.ui.component.CurrencyFormFields

@Composable
internal fun AddCurrencyDialog(onConfirm: (code: String, name: String) -> Unit, onDismiss: () -> Unit) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.action_add_currency)) },
        text = { CurrencyFormFields(code, name, onCodeChange = { code = it }, onNameChange = { name = it }) },
        confirmButton = {
            TextButton(onClick = { onConfirm(code, name) }) {
                Text(stringResource(R.string.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
