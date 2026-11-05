package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmTopBar

@Composable
fun TransactionsTopBar(onReset: () -> Unit) {
    MmTopBar(
        title = stringResource(R.string.title_transactions),
        actions = {
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = stringResource(R.string.action_reset)
                )
            }
        }
    )
}
