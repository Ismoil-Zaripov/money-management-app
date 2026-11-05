package uz.moneymanager.feature.transactions.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import uz.moneymanager.feature.transactions.presentation.TransactionsViewModel

/** ViewModel'ni oladi, state yig'adi, stateless [TransactionsScreen] ni chaqiradi. */
@Composable
fun TransactionsRoute(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val items = viewModel.transactions.collectAsLazyPagingItems()

    TransactionsScreen(
        uiState = uiState,
        items = items,
        onEvent = viewModel::onEvent,
        onNavigateToCreate = onNavigateToCreate,
        onNavigateToEdit = onNavigateToEdit,
    )
}
