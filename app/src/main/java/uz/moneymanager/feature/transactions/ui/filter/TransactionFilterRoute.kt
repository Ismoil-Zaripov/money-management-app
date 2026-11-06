package uz.moneymanager.feature.transactions.ui.filter

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uz.moneymanager.feature.transactions.presentation.TransactionFilterDraft
import uz.moneymanager.feature.transactions.presentation.TransactionsEvent
import uz.moneymanager.feature.transactions.presentation.TransactionsUiState

/**
 * Full-height sheet that edits a [TransactionFilterDraft]. The filter changes only on "Show N Results";
 * closing (X / outside tap) discards the draft.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterRoute(
    uiState: TransactionsUiState,
    onEvent: (TransactionsEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember { mutableStateOf(TransactionFilterDraft.of(uiState.filter)) }

    LaunchedEffect(draft) { onEvent(TransactionsEvent.FilterPreviewChanged(draft)) }
    DisposableEffect(Unit) { onDispose { onEvent(TransactionsEvent.FilterPreviewChanged(null)) } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        TransactionFilterScreen(
            draft = draft,
            categories = uiState.categories,
            paymentMethods = uiState.paymentMethods,
            currencies = uiState.currencies,
            displayCurrencyId = uiState.displayCurrency?.id,
            resultCount = uiState.previewCount,
            onDraftChange = { draft = it },
            onDisplayCurrencySelect = { onEvent(TransactionsEvent.DisplayCurrencySelected(it)) },
            onClearAll = { draft = draft.cleared() },
            onClose = onDismiss,
            onShow = {
                onEvent(TransactionsEvent.FilterApplied(draft))
                onDismiss()
            }
        )
    }
}
