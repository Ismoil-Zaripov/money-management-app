package uz.moneymanager.feature.rates.ui.dialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.ExchangeRateRow

@Composable
fun DeleteRateDialog(
    item: ExchangeRateRow,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    MmDialog(
        title = stringResource(R.string.rates_delete_title),
        confirmText = stringResource(R.string.action_delete),
        dismissText = stringResource(R.string.action_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Text(
            stringResource(R.string.delete_rate_confirm, item.baseCode, item.quoteCode),
            style = MaterialTheme.contentText
        )
    }
}
