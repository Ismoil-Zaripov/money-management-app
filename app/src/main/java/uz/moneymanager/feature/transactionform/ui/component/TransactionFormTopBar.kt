package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmTopBar

@Composable
internal fun TransactionFormTopBar(isNew: Boolean, onBack: () -> Unit) {
    MmTopBar(
        title = stringResource(if (isNew) R.string.title_new_transaction else R.string.title_edit_transaction),
        onBack = onBack
    )
}
