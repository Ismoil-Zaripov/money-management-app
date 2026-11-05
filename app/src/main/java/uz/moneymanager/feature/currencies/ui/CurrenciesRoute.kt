package uz.moneymanager.feature.currencies.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.data.local.CurrencyWithUsage
import uz.moneymanager.feature.currencies.presentation.CurrenciesViewModel
import uz.moneymanager.feature.currencies.presentation.CurrencyEvent
import uz.moneymanager.feature.currencies.ui.dialog.AddCurrencyDialog
import uz.moneymanager.feature.currencies.ui.dialog.DeleteCurrencyDialog
import uz.moneymanager.feature.currencies.ui.dialog.EditCurrencyDialog

@Composable
fun CurrenciesRoute(
    onNavigateBack: () -> Unit,
    viewModel: CurrenciesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var currencyToEdit by remember { mutableStateOf<CurrencyWithUsage?>(null) }
    var currencyToDelete by remember { mutableStateOf<CurrencyWithUsage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CurrencyEvent.Success -> {}
                is CurrencyEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    CurrenciesScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onNavigateBack,
        onAddClick = { showAddDialog = true },
        onEdit = { currencyToEdit = it },
        onDelete = { currencyToDelete = it }
    )

    if (showAddDialog) {
        AddCurrencyDialog(
            onConfirm = { code, name ->
                viewModel.addCurrency(code, name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
    currencyToEdit?.let { item ->
        EditCurrencyDialog(
            item = item,
            onConfirm = { code, name ->
                viewModel.updateCurrency(item.currency.id, code, name)
                currencyToEdit = null
            },
            onDismiss = { currencyToEdit = null }
        )
    }
    currencyToDelete?.let { item ->
        DeleteCurrencyDialog(
            item = item,
            onConfirm = {
                viewModel.deleteCurrency(item.currency.id)
                currencyToDelete = null
            },
            onDismiss = { currencyToDelete = null }
        )
    }
}
