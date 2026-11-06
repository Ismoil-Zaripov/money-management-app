package uz.moneymanager.feature.paymentmethods.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog

@Composable
fun AddPaymentMethodDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }

    MmDialog(
        title = stringResource(R.string.action_add_payment_method),
        confirmText = stringResource(R.string.action_save),
        dismissText = stringResource(R.string.action_cancel),
        confirmEnabled = name.isNotBlank(),
        onConfirm = {
            val trimmed = name.trim()
            if (trimmed.isNotEmpty()) onConfirm(trimmed)
        },
        onDismiss = onDismiss
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.label_payment_method_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
