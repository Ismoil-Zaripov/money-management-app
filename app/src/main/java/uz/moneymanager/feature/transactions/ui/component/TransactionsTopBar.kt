package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmTopBar

@Composable
fun TransactionsTopBar(onMore: () -> Unit) {
    MmTopBar(
        title = stringResource(R.string.title_transactions),
        actions = {
            IconButton(onClick = onMore) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.action_more)
                )
            }
        }
    )
}
