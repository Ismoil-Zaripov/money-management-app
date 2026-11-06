package uz.moneymanager.feature.transactionform.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.math.BigDecimal
import uz.moneymanager.feature.transactionform.presentation.FormEvent
import uz.moneymanager.feature.transactionform.presentation.FormUiState
import uz.moneymanager.feature.transactionform.ui.dialog.AddPaymentMethodDialog
import uz.moneymanager.feature.transactionform.ui.dialog.DeleteTransactionConfirmDialog
import uz.moneymanager.feature.transactionform.ui.dialog.DiscardChangesDialog
import uz.moneymanager.feature.transactionform.ui.dialog.MissingRatesDialog
import uz.moneymanager.feature.transactionform.ui.dialog.NoInternetDialog

/** Which form dialogs are open. The boolean/name flags survive configuration change (saveable). */
@Stable
internal class FormDialogState(
    discard: MutableState<Boolean>,
    delete: MutableState<Boolean>,
    addPaymentMethod: MutableState<Boolean>,
    paymentMethodName: MutableState<String>,
) {
    var showDiscard by discard
    var showDelete by delete
    var showAddPaymentMethod by addPaymentMethod
    var newPaymentMethodName by paymentMethodName
    var missingRates by mutableStateOf<FormEvent.ShowMissingRates?>(null)
}

@Composable
internal fun rememberFormDialogState(): FormDialogState {
    val discard = rememberSaveable { mutableStateOf(false) }
    val delete = rememberSaveable { mutableStateOf(false) }
    val addPaymentMethod = rememberSaveable { mutableStateOf(false) }
    val paymentMethodName = rememberSaveable { mutableStateOf("") }
    return remember { FormDialogState(discard, delete, addPaymentMethod, paymentMethodName) }
}

internal class FormDialogActions(
    val onDiscardConfirm: () -> Unit,
    val onDeleteConfirm: () -> Unit,
    val onAddPaymentMethod: (String) -> Unit,
    val onSaveWithRates: (Map<Long, BigDecimal>) -> Unit,
    val onDismissNoInternet: () -> Unit,
)

@Composable
internal fun TransactionFormDialogs(
    state: FormUiState,
    dialogs: FormDialogState,
    actions: FormDialogActions,
) {
    if (dialogs.showDiscard) {
        DiscardChangesDialog(
            onConfirm = {
                dialogs.showDiscard = false
                actions.onDiscardConfirm()
            },
            onDismiss = { dialogs.showDiscard = false }
        )
    }
    if (dialogs.showDelete) {
        DeleteTransactionConfirmDialog(
            onConfirm = {
                dialogs.showDelete = false
                actions.onDeleteConfirm()
            },
            onDismiss = { dialogs.showDelete = false }
        )
    }
    if (dialogs.showAddPaymentMethod) {
        AddPaymentMethodDialog(
            name = dialogs.newPaymentMethodName,
            onNameChange = { dialogs.newPaymentMethodName = it },
            onConfirm = {
                actions.onAddPaymentMethod(it)
                dialogs.showAddPaymentMethod = false
            },
            onDismiss = { dialogs.showAddPaymentMethod = false }
        )
    }
    if (state.showNoInternetDialog) {
        NoInternetDialog(onDismiss = actions.onDismissNoInternet)
    }
    dialogs.missingRates?.let { event ->
        MissingRatesDialog(
            event = event,
            onSave = { rates ->
                dialogs.missingRates = null
                actions.onSaveWithRates(rates)
            },
            onDismiss = { dialogs.missingRates = null }
        )
    }
}
