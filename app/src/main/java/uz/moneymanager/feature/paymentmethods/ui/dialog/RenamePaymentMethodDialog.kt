package uz.moneymanager.feature.paymentmethods.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.data.local.PaymentMethodWithUsage

@Composable
fun RenamePaymentMethodDialog(
    item: PaymentMethodWithUsage,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable(item.paymentMethod.id) { mutableStateOf(item.paymentMethod.name) }
    val count = item.transactionCount

    MmDialog(
        title = stringResource(R.string.action_rename_payment_method),
        confirmText = stringResource(R.string.action_save),
        dismissText = stringResource(R.string.action_cancel),
        confirmEnabled = name.isNotBlank(),
        onConfirm = {
            val trimmed = name.trim()
            if (trimmed.isNotEmpty()) onConfirm(trimmed)
        },
        onDismiss = onDismiss
    ) {
        Column {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.label_payment_method_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (count > 0) {
                Text(
                    text = pluralStringResource(R.plurals.payment_method_linked_records, count, count),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
