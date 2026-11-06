package uz.moneymanager.feature.paymentmethods.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.feature.paymentmethods.presentation.PaymentMethodEvent
import uz.moneymanager.feature.paymentmethods.presentation.PaymentMethodsViewModel
import uz.moneymanager.feature.paymentmethods.ui.dialog.AddPaymentMethodDialog
import uz.moneymanager.feature.paymentmethods.ui.dialog.DeletePaymentMethodDialog
import uz.moneymanager.feature.paymentmethods.ui.dialog.RenamePaymentMethodDialog

@Composable
fun PaymentMethodsRoute(
    onNavigateBack: () -> Unit,
    viewModel: PaymentMethodsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val paymentMethods = state.paymentMethods

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var renameTargetId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deleteTargetId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PaymentMethodEvent.Success -> {}
                is PaymentMethodEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    PaymentMethodsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onNavigateBack,
        onAddClick = { showAddDialog = true },
        onRename = { renameTargetId = it.paymentMethod.id },
        onDelete = { item ->
            if (item.transactionCount > 0) {
                viewModel.deletePaymentMethod(item.paymentMethod.id)
            } else {
                deleteTargetId = item.paymentMethod.id
            }
        }
    )

    if (showAddDialog) {
        AddPaymentMethodDialog(
            onConfirm = { name ->
                viewModel.addPaymentMethod(name)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
    paymentMethods.firstOrNull { it.paymentMethod.id == renameTargetId }?.let { item ->
        RenamePaymentMethodDialog(
            item = item,
            onConfirm = { name ->
                viewModel.renamePaymentMethod(item.paymentMethod.id, name)
                renameTargetId = null
            },
            onDismiss = { renameTargetId = null }
        )
    }
    paymentMethods.firstOrNull { it.paymentMethod.id == deleteTargetId }?.let { item ->
        DeletePaymentMethodDialog(
            item = item,
            onConfirm = {
                viewModel.deletePaymentMethod(item.paymentMethod.id)
                deleteTargetId = null
            },
            onDismiss = { deleteTargetId = null }
        )
    }
}
