package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.runtime.Composable
import uz.moneymanager.feature.transactions.presentation.TransactionsEvent
import uz.moneymanager.feature.transactions.presentation.TransactionsUiState

/** Renders the currently open filter sheet and turns its result into [TransactionsEvent]s. */
@Composable
fun FilterSheetHost(
    sheet: FilterSheet?,
    uiState: TransactionsUiState,
    onEvent: (TransactionsEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val filter = uiState.filter
    when (sheet) {
        null -> Unit
        FilterSheet.Category -> CategoryFilterSheet(
            categories = uiState.categories,
            selectedId = filter.categoryId,
            onApply = {
                onEvent(TransactionsEvent.CategorySelected(it))
                onDismiss()
            },
            onDismiss = onDismiss
        )
        FilterSheet.PaymentCurrency -> PaymentCurrencySheet(
            paymentMethods = uiState.paymentMethods,
            currencies = uiState.currencies,
            selectedPaymentMethodId = filter.paymentMethodId,
            selectedCurrencyId = filter.currencyId,
            onApply = { paymentMethodId, currencyId ->
                onEvent(TransactionsEvent.PaymentMethodSelected(paymentMethodId))
                onEvent(TransactionsEvent.CurrencyFilterSelected(currencyId))
                onDismiss()
            },
            onDismiss = onDismiss
        )
        FilterSheet.Type -> TypeFilterSheet(
            selected = filter.type,
            onApply = {
                onEvent(TransactionsEvent.TypeSelected(it))
                onDismiss()
            },
            onDismiss = onDismiss
        )
        FilterSheet.DateRange -> DateRangeSheet(
            from = filter.from,
            to = filter.to,
            onApply = { from, to ->
                onEvent(TransactionsEvent.DateRangeSelected(from, to))
                onDismiss()
            },
            onDismiss = onDismiss
        )
    }
}
