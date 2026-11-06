package uz.moneymanager.feature.transactionform.ui

import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmScaffold
import uz.moneymanager.core.designsystem.component.LocalPageReady
import uz.moneymanager.core.designsystem.component.MmPage
import uz.moneymanager.feature.transactionform.ui.component.TransactionFormSkeleton
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import uz.moneymanager.feature.transactionform.presentation.FormUiState
import uz.moneymanager.feature.transactionform.ui.component.SaveBar
import uz.moneymanager.feature.transactionform.ui.component.TransactionFormTopBar
import uz.moneymanager.feature.transactionform.ui.component.onFocusLost

/** STATELESS: faqat [state] va [callbacks]; ViewModel'ni bilmaydi. */
@Composable
internal fun TransactionFormScreen(
    state: FormUiState,
    snackbarHostState: SnackbarHostState,
    callbacks: TransactionFormCallbacks,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var amountHadFocus by rememberSaveable { mutableStateOf(false) }
    var categoryHadFocus by rememberSaveable { mutableStateOf(false) }
    var initialFocusRequested by rememberSaveable { mutableStateOf(false) }
    val amountFocus = remember { FocusRequester() }
    val categoryFocus = remember { FocusRequester() }

    // Yangi tranzaksiyada summa avtomatik fokusda va klaviatura ochiq (tahrirlashda emas)
    val pageReady = LocalPageReady.current
    LaunchedEffect(state.isNew, state.isLoading, pageReady) {
        if (state.isNew && !state.isLoading && pageReady && !initialFocusRequested) {
            initialFocusRequested = true
            withFrameNanos { }
            amountFocus.requestFocus()
            keyboard?.show()
        }
    }

    MmScaffold(
        topBar = { TransactionFormTopBar(isNew = state.isNew, onBack = callbacks.onBack) },
        bottomBar = {
            SaveBar(
                isSaving = state.isSaving,
                enabled = state.canSave,
                errorCount = listOfNotNull(state.amountErrorRes, state.categoryErrorRes, state.descriptionErrorRes).size,
                saveLabel = stringResource(if (state.isNew) R.string.action_save_transaction else R.string.action_save_changes),
                onSave = callbacks.onSave,
                onDelete = if (state.isNew) null else callbacks.onDeleteClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        MmPage(
            loaded = state.loaded,
            skeleton = { TransactionFormSkeleton(Modifier.fillMaxSize().padding(padding)) },
        ) {
            TransactionFormContent(
                state = state,
                callbacks = callbacks,
                amountModifier = Modifier
                    .focusRequester(amountFocus)
                    .onFocusLost(amountHadFocus, { amountHadFocus = true }, callbacks.onAmountBlur),
                categoryModifier = Modifier
                    .focusRequester(categoryFocus)
                    .onFocusLost(categoryHadFocus, { categoryHadFocus = true }, callbacks.onCategoryBlur),
                onAmountNext = { categoryFocus.requestFocus() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}
