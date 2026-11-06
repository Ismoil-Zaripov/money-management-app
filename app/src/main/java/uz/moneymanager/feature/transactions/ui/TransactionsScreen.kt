package uz.moneymanager.feature.transactions.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import uz.moneymanager.core.designsystem.component.MmScaffold
import uz.moneymanager.feature.transactions.presentation.TransactionListItemUiModel
import uz.moneymanager.feature.transactions.presentation.TransactionsEvent
import uz.moneymanager.feature.transactions.presentation.TransactionsUiState
import uz.moneymanager.feature.transactions.ui.component.TotalSummaryFooter
import uz.moneymanager.feature.transactions.ui.component.TransactionsTopBar
import uz.moneymanager.feature.transactions.ui.dialog.DeleteTransactionDialog
import uz.moneymanager.feature.transactions.ui.filter.TransactionFilterRoute
import uz.moneymanager.feature.transactions.ui.sheet.MoreActionsSheet
import uz.moneymanager.feature.transactions.ui.sheet.summary.SummaryReportBottomSheet

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
    var openSheet by remember { mutableStateOf<TransactionsSheet?>(null) }
    var isAmountVisible by rememberSaveable { mutableStateOf(true) }

    val displayCode = uiState.displayCode
    val fallbackCode = uiState.fallbackCode

    MmScaffold(
        topBar = { TransactionsTopBar(onMore = { openSheet = TransactionsSheet.More }) },
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
        TransactionsContent(
            uiState = uiState,
            items = items,
            onEdit = onNavigateToEdit,
            onDelete = { transactionToDelete = it },
            listBottomPadding = padding.calculateBottomPadding(),
            modifier = Modifier.padding(top = padding.calculateTopPadding())
        )
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

    when (openSheet) {
        TransactionsSheet.More -> MoreActionsSheet(
            onAddTransaction = {
                openSheet = null
                onNavigateToCreate()
            },
            onOpenFilter = { openSheet = TransactionsSheet.Filter },
            onDismiss = { openSheet = null }
        )
        TransactionsSheet.Filter -> TransactionFilterRoute(
            uiState = uiState,
            onEvent = onEvent,
            onDismiss = { openSheet = null }
        )
        null -> Unit
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
