package uz.moneymanager.feature.transactions.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.paging.compose.LazyPagingItems
import uz.moneymanager.R
import uz.moneymanager.feature.transactions.presentation.TransactionListItemUiModel
import uz.moneymanager.feature.transactions.presentation.TransactionsEvent
import uz.moneymanager.feature.transactions.presentation.TransactionsUiState
import uz.moneymanager.feature.transactions.ui.component.TotalSummaryFooter
import uz.moneymanager.feature.transactions.ui.component.TransactionFilterBar
import uz.moneymanager.feature.transactions.ui.component.TransactionList
import uz.moneymanager.feature.transactions.ui.component.TransactionsTopBar
import uz.moneymanager.feature.transactions.ui.dialog.DeleteTransactionDialog
import uz.moneymanager.feature.transactions.ui.sheet.SummaryReportBottomSheet

/** Stateless: ViewModel'ni bilmaydi. Faqat [uiState], [items] va callback'lar. */
@Composable
fun TransactionsScreen(
    uiState: TransactionsUiState,
    items: LazyPagingItems<TransactionListItemUiModel>,
    onEvent: (TransactionsEvent) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
) {
    var transactionToDelete by remember { mutableStateOf<TransactionListItemUiModel.Item?>(null) }
    var showSummarySheet by remember { mutableStateOf(false) }
    var isAmountVisible by rememberSaveable { mutableStateOf(true) }

    val displayCode = uiState.displayCode
    val fallbackCode = uiState.fallbackCode

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TransactionsTopBar(onReset = { onEvent(TransactionsEvent.ResetFilters) }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add)
                )
            }
        },
        bottomBar = {
            TotalSummaryFooter(
                balance = uiState.totals.display.balance,
                displayCode = displayCode,
                isAmountVisible = isAmountVisible,
                onToggleAmountVisibility = { isAmountVisible = !isAmountVisible },
                onClick = { showSummarySheet = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                onDateRangeSelect = { from, to -> onEvent(TransactionsEvent.DateRangeSelected(from, to)) }
            )

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            TransactionList(
                items = items,
                dailyTotals = uiState.dailyTotals,
                displayCode = displayCode,
                fallbackCode = fallbackCode,
                onEdit = onNavigateToEdit,
                onDelete = { transactionToDelete = it }
            )
        }
    }

    transactionToDelete?.let { tx ->
        DeleteTransactionDialog(
            onConfirm = {
                onEvent(TransactionsEvent.DeleteConfirmed(tx.id))
                transactionToDelete = null
            },
            onDismiss = { transactionToDelete = null }
        )
    }

    if (showSummarySheet) {
        SummaryReportBottomSheet(
            onDismiss = { showSummarySheet = false },
            totals = uiState.totals,
            displayCode = displayCode,
            fallbackCode = fallbackCode,
            showFallback = uiState.showFallback
        )
    }
}
