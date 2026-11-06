package uz.moneymanager.feature.transactions.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import uz.moneymanager.feature.transactions.presentation.TransactionListItemUiModel
import uz.moneymanager.feature.transactions.presentation.TransactionsEvent
import uz.moneymanager.feature.transactions.presentation.TransactionsUiState
import uz.moneymanager.feature.transactions.ui.component.TransactionFilterBar
import uz.moneymanager.feature.transactions.ui.component.TransactionList
import uz.moneymanager.feature.transactions.ui.sheet.FilterSheet

/** Filter chips + paged list (the part of the Transactions screen inside the Scaffold). */
@Composable
internal fun TransactionsContent(
    uiState: TransactionsUiState,
    items: LazyPagingItems<TransactionListItemUiModel>,
    onEvent: (TransactionsEvent) -> Unit,
    onOpenSheet: (FilterSheet) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (TransactionListItemUiModel.Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TransactionFilterBar(
            filter = uiState.filter,
            currencies = uiState.currencies,
            categories = uiState.categories,
            paymentMethods = uiState.paymentMethods,
            displayCurrency = uiState.displayCurrency,
            onCategorySelect = { onEvent(TransactionsEvent.CategorySelected(it)) },
            onCurrencyFilterSelect = { onEvent(TransactionsEvent.CurrencyFilterSelected(it)) },
            onPaymentMethodSelect = { onEvent(TransactionsEvent.PaymentMethodSelected(it)) },
            onDisplayCurrencySelect = { onEvent(TransactionsEvent.DisplayCurrencySelected(it)) },
            onTypeSelect = { onEvent(TransactionsEvent.TypeSelected(it)) },
            onDateRangeSelect = { from, to -> onEvent(TransactionsEvent.DateRangeSelected(from, to)) },
            onOpenSheet = onOpenSheet
        )

        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        TransactionList(
            items = items,
            dailyTotals = uiState.dailyTotals,
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}
