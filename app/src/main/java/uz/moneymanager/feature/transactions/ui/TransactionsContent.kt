package uz.moneymanager.feature.transactions.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.paging.compose.LazyPagingItems
import uz.moneymanager.feature.transactions.presentation.TransactionListItemUiModel
import uz.moneymanager.feature.transactions.presentation.TransactionsUiState
import uz.moneymanager.feature.transactions.ui.component.TransactionList

/** Paged list (the part of the Transactions screen inside the Scaffold). */
@Composable
internal fun TransactionsContent(
    uiState: TransactionsUiState,
    items: LazyPagingItems<TransactionListItemUiModel>,
    onEdit: (Long) -> Unit,
    onDelete: (TransactionListItemUiModel.Item) -> Unit,
    listBottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TransactionList(
            items = items,
            dailyTotals = uiState.dailyTotals,
            onEdit = onEdit,
            onDelete = onDelete,
            bottomPadding = listBottomPadding,
        )
    }
}
