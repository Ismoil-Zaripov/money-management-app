package uz.moneymanager.feature.transactionform.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.feature.transactionform.ui.component.FormFieldShape

@Composable
internal fun AddPaymentMethodDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    MmDialog(
        title = stringResource(R.string.action_add_payment_method),
        confirmText = stringResource(R.string.action_save),
        dismissText = stringResource(R.string.action_cancel),
        onConfirm = {
            val trimmed = name.trim()
            if (trimmed.isNotEmpty()) onConfirm(trimmed)
        },
        onDismiss = onDismiss
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.label_payment_method_name)) },
            singleLine = true,
            shape = FormFieldShape,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
