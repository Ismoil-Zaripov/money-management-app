package uz.moneymanager.feature.transactionform.presentation

import androidx.lifecycle.SavedStateHandle
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity

internal data class FormDependencies(
    val currencies: List<CurrencyEntity>,
    val categories: List<CategoryEntity>,
    val suggestions: List<String>,
    val paymentMethods: List<PaymentMethodEntity>,
    val usage: Map<String, Int>,
)

/** Applies freshly observed lists; resolves (and remembers) the initial currency / payment method if still unset. */
internal fun FormUiState.withDependencies(handle: SavedStateHandle, deps: FormDependencies): FormUiState {
    val resolvedCurrencyId = currencyId ?: resolveInitialCurrencyId(handle, deps.currencies)
    if (currencyId == null && resolvedCurrencyId != null) {
        handle[KEY_CURRENCY_ID] = resolvedCurrencyId
        if (handle.get<Long>(KEY_ORIG_CURRENCY_ID) == null) {
            handle[KEY_ORIG_CURRENCY_ID] = resolvedCurrencyId
        }
    }

    val resolvedPaymentMethodId = paymentMethodId ?: resolveInitialPaymentMethodId(handle, deps.paymentMethods)
    if (paymentMethodId == null && resolvedPaymentMethodId != null) {
        handle[KEY_PAYMENT_METHOD_ID] = resolvedPaymentMethodId
        if (handle.get<Long>(KEY_ORIG_PAYMENT_METHOD_ID) == null) {
            handle[KEY_ORIG_PAYMENT_METHOD_ID] = resolvedPaymentMethodId
        }
    }

    return copy(
        currencyId = resolvedCurrencyId,
        currencies = deps.currencies,
        allCategories = deps.categories,
        categorySuggestions = deps.suggestions,
        paymentMethodId = resolvedPaymentMethodId,
        paymentMethods = deps.paymentMethods,
        categoryUsage = deps.usage,
    )
}
