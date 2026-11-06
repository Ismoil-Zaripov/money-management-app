package uz.moneymanager.feature.paymentmethods.ui.dialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.PaymentMethodWithUsage

@Composable
fun DeletePaymentMethodDialog(
    item: PaymentMethodWithUsage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    MmDialog(
        title = stringResource(R.string.action_delete),
        confirmText = stringResource(R.string.action_delete),
        dismissText = stringResource(R.string.action_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Text(
            text = stringResource(R.string.delete_payment_method_confirm, item.paymentMethod.name),
            style = MaterialTheme.contentText
        )
    }
}
