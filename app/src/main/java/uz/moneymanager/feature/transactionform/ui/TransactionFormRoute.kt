package uz.moneymanager.feature.transactionform.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.feature.transactionform.presentation.FormEvent
import uz.moneymanager.feature.transactionform.presentation.TransactionFormViewModel

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
    val dialogs = rememberFormDialogState()

    val handleBack = {
        focusManager.clearFocus()
        keyboard?.hide()
        val current = viewModel.uiState.value
        if (current.isDirty && !current.isSaving) {
            dialogs.showDiscard = true
        } else {
            currentOnNavigateBack()
        }
    }

    BackHandler(enabled = state.isDirty && !state.isSaving && !state.showNoInternetDialog) { handleBack() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FormEvent.Saved, is FormEvent.Deleted -> currentOnNavigateBack()
                is FormEvent.Error ->
                    snackbarHostState.showSnackbar(context.getString(event.messageRes, *event.args.toTypedArray()))
                is FormEvent.ShowMissingRates -> dialogs.missingRates = event
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
        onDeleteClick = { dialogs.showDelete = true },
        onTypeChange = viewModel::setType,
        onAmountChange = viewModel::setAmount,
        onAmountBlur = viewModel::onAmountBlur,
        onCurrencySelect = viewModel::setCurrencyId,
        onCategoryChange = viewModel::setCategoryText,
        onCategoryBlur = viewModel::onCategoryBlur,
        onPaymentMethodSelect = viewModel::setPaymentMethodId,
        onAddPaymentMethod = {
            dialogs.newPaymentMethodName = ""
            dialogs.showAddPaymentMethod = true
        },
        onDateChange = viewModel::setDate,
        onDescriptionChange = viewModel::setDescription,
    )

    TransactionFormScreen(state = state, snackbarHostState = snackbarHostState, callbacks = callbacks)

    TransactionFormDialogs(
        state = state,
        dialogs = dialogs,
        actions = FormDialogActions(
            onDiscardConfirm = { currentOnNavigateBack() },
            onDeleteConfirm = viewModel::delete,
            onAddPaymentMethod = viewModel::addQuickPaymentMethod,
            onSaveWithRates = viewModel::save,
            onDismissNoInternet = viewModel::dismissNoInternetDialog,
        )
    )
}
