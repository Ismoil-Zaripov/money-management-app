package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import java.time.LocalDate
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmEmptyState
import uz.moneymanager.domain.MonitoringTotals
import uz.moneymanager.feature.transactions.presentation.TransactionListItemUiModel

/** Paging ro'yxat + loading / empty holatlari. */
@Composable
fun TransactionList(
    items: LazyPagingItems<TransactionListItemUiModel>,
    dailyTotals: Map<LocalDate, MonitoringTotals>,
    onEdit: (Long) -> Unit,
    onDelete: (TransactionListItemUiModel.Item) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (items.loadState.refresh is LoadState.Loading) {
            TransactionSkeletonList()
        } else if (items.itemCount == 0 && items.loadState.refresh !is LoadState.Loading) {
            MmEmptyState(title = stringResource(R.string.empty_transactions))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(
                    count = items.itemCount,
                    key = items.itemKey { item ->
                        when (item) {
                            is TransactionListItemUiModel.Header -> "h_${item.date}"
                            is TransactionListItemUiModel.Item -> "t_${item.id}"
                        }
                    },
                    contentType = items.itemContentType { item ->
                        when (item) {
                            is TransactionListItemUiModel.Header -> "header"
                            is TransactionListItemUiModel.Item -> "transaction_row"
                        }
                    }
                ) { index ->
                    when (val item = items[index]) {
                        is TransactionListItemUiModel.Header -> {
                            DateHeader(
                                dateText = item.dateText,
                                operationsCount = dailyTotals[item.date]?.let { it.display.count + it.fallback.count },
                            )
                        }
                        is TransactionListItemUiModel.Item -> {
                            TransactionRow(
                                item = item,
                                onClick = { onEdit(item.id) },
                                onDelete = { onDelete(item) }
                            )
                        }
                        null -> Unit
                    }
                }

                if (items.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}
