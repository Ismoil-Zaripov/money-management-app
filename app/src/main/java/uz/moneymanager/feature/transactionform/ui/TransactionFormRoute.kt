package uz.moneymanager.feature.transactionform.ui

import uz.moneymanager.feature.transactionform.ui.dialog.DeleteTransactionConfirmDialog
import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.feature.transactionform.presentation.FormEvent
import uz.moneymanager.feature.transactionform.presentation.TransactionFormViewModel
import uz.moneymanager.feature.transactionform.ui.dialog.AddPaymentMethodDialog
import uz.moneymanager.feature.transactionform.ui.dialog.DiscardChangesDialog
import uz.moneymanager.feature.transactionform.ui.dialog.MissingRatesDialog
import uz.moneymanager.feature.transactionform.ui.dialog.NoInternetDialog

@Composable
fun TransactionFormRoute(
    onNavigateBack: () -> Unit,
    viewModel: TransactionFormViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var missingRatesEvent by remember { mutableStateOf<FormEvent.ShowMissingRates?>(null) }
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showAddPaymentMethodDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var newPaymentMethodName by rememberSaveable { mutableStateOf("") }

    val handleBack = {
        focusManager.clearFocus()
        keyboard?.hide()
        val current = viewModel.uiState.value
        if (current.isDirty && !current.isSaving) {
            showDiscardDialog = true
        } else {
            currentOnNavigateBack()
        }
    }

    BackHandler(enabled = state.isDirty && !state.isSaving && !state.showNoInternetDialog) {
        handleBack()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FormEvent.Saved, is FormEvent.Deleted -> currentOnNavigateBack()
                is FormEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
                    snackbarHostState.showSnackbar(msg)
                }
                is FormEvent.ShowMissingRates -> missingRatesEvent = event
            }
        }
    }

    val callbacks = TransactionFormCallbacks(
        onBack = handleBack,
        onSave = {
            focusManager.clearFocus()
            keyboard?.hide()
            viewModel.save()
        },
        onDeleteClick = { showDeleteDialog = true },
        onTypeChange = viewModel::setType,
        onAmountChange = viewModel::setAmount,
        onAmountBlur = viewModel::onAmountBlur,
        onCurrencySelect = viewModel::setCurrencyId,
        onCategoryChange = viewModel::setCategoryText,
        onCategoryBlur = viewModel::onCategoryBlur,
        onPaymentMethodSelect = viewModel::setPaymentMethodId,
        onAddPaymentMethod = {
            newPaymentMethodName = ""
            showAddPaymentMethodDialog = true
        },
        onDateChange = viewModel::setDate,
        onDescriptionChange = viewModel::setDescription,
    )

    TransactionFormScreen(state = state, snackbarHostState = snackbarHostState, callbacks = callbacks)

    if (showDiscardDialog) {
        DiscardChangesDialog(
            onConfirm = {
                showDiscardDialog = false
                currentOnNavigateBack()
            },
            onDismiss = { showDiscardDialog = false }
        )
    }
    if (showDeleteDialog) {
        DeleteTransactionConfirmDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.delete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    if (showAddPaymentMethodDialog) {
        AddPaymentMethodDialog(
            name = newPaymentMethodName,
            onNameChange = { newPaymentMethodName = it },
            onConfirm = {
                viewModel.addQuickPaymentMethod(it)
                showAddPaymentMethodDialog = false
            },
            onDismiss = { showAddPaymentMethodDialog = false }
        )
    }
    if (state.showNoInternetDialog) {
        NoInternetDialog(onDismiss = viewModel::dismissNoInternetDialog)
    }
    missingRatesEvent?.let { event ->
        MissingRatesDialog(
            event = event,
            onSave = { rates ->
                missingRatesEvent = null
                viewModel.save(rates)
            },
            onDismiss = { missingRatesEvent = null }
        )
    }
}
