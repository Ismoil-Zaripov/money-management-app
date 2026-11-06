package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R

/** "More" (⋮) actions of the Transactions screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreActionsSheet(
    onAddTransaction: () -> Unit,
    onOpenFilter: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = colors.surface) {
        Column(
            modifier = Modifier.navigationBarsPadding().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MoreActionCard(
                icon = Icons.Default.Add,
                title = stringResource(R.string.more_add_transaction),
                hint = stringResource(R.string.more_add_transaction_hint),
                containerColor = colors.primaryContainer,
                contentColor = colors.onPrimaryContainer,
                tileColor = colors.primary,
                tileContentColor = colors.onPrimary,
                onClick = onAddTransaction
            )
            MoreActionCard(
                icon = Icons.Default.FilterList,
                title = stringResource(R.string.title_filter),
                hint = stringResource(R.string.more_filter_hint),
                containerColor = colors.surfaceContainerHighest,
                contentColor = colors.onSurface,
                tileColor = colors.surface,
                tileContentColor = colors.primary,
                onClick = onOpenFilter
            )
        }
    }
}
