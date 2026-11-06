package uz.moneymanager.feature.rates.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.data.local.ExchangeRateRow
import uz.moneymanager.feature.rates.presentation.RatesViewModel
import uz.moneymanager.feature.rates.ui.dialog.AddRateDialog
import uz.moneymanager.feature.rates.ui.dialog.DeleteRateDialog
import uz.moneymanager.feature.rates.ui.dialog.EditRateDialog
import uz.moneymanager.feature.rates.ui.dialog.RateDatePickerDialog

@Composable
fun RatesRoute(
    onNavigateBack: () -> Unit,
    viewModel: RatesViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showScreenDatePicker by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var rateToEdit by remember { mutableStateOf<ExchangeRateRow?>(null) }
    var rateToDelete by remember { mutableStateOf<ExchangeRateRow?>(null) }

    RateEventsEffect(viewModel.events, snackbarHostState, onRetryImport = viewModel::importFromCbu)

    RatesScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onNavigateBack,
        onImport = viewModel::importFromCbu,
        onAddClick = { showAddDialog = true },
        onDateChange = viewModel::setDate,
        onPickDate = { showScreenDatePicker = true },
        onEdit = { rateToEdit = it },
        onDelete = { rateToDelete = it }
    )

    if (showScreenDatePicker) {
        RateDatePickerDialog(
            initialDate = state.date,
            onDateSelected = {
                viewModel.setDate(it)
                showScreenDatePicker = false
            },
            onDismiss = { showScreenDatePicker = false }
        )
    }
    if (showAddDialog) {
        AddRateDialog(
            currencies = state.currencies,
            onConfirm = { baseId, quoteId, rate, date ->
                viewModel.saveRate(baseId, quoteId, rate, date = date)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
    rateToEdit?.let { item ->
        EditRateDialog(
            item = item,
            onConfirm = { rate, date ->
                viewModel.saveRate(
                    baseId = item.baseCurrencyId,
                    quoteId = item.quoteCurrencyId,
                    rate = rate,
                    date = date,
                    originalDate = item.date,
                )
                rateToEdit = null
            },
            onDismiss = { rateToEdit = null }
        )
    }
    rateToDelete?.let { item ->
        DeleteRateDialog(
            item = item,
            onConfirm = {
                viewModel.deleteRate(item.baseCurrencyId, item.quoteCurrencyId, item.date)
                rateToDelete = null
            },
            onDismiss = { rateToDelete = null }
        )
    }
}
