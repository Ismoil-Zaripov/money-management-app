package uz.moneymanager.feature.transactionform.presentation

import androidx.lifecycle.SavedStateHandle
import java.time.LocalDate
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.model.TransactionType

internal fun buildInitialFormState(
    savedStateHandle: SavedStateHandle,
    isNew: Boolean,
    transactionId: Long?,
    showNoInternet: Boolean,
): FormUiState {
    val savedType = savedStateHandle.get<String>(KEY_TYPE)?.let {
        runCatching { TransactionType.valueOf(it) }.getOrNull()
    } ?: TransactionType.EXPENSE
    val savedAmount = savedStateHandle.get<String>(KEY_AMOUNT) ?: ""
    val savedCurrencyId = savedStateHandle.get<Long>(KEY_CURRENCY_ID)
    val savedCategory = savedStateHandle.get<String>(KEY_CATEGORY) ?: ""
    val savedPaymentMethodId = savedStateHandle.get<Long>(KEY_PAYMENT_METHOD_ID)
    val savedDescription = savedStateHandle.get<String>(KEY_DESCRIPTION) ?: ""
    val savedDate = savedStateHandle.get<Long>(KEY_DATE)?.let(LocalDate::ofEpochDay) ?: LocalDate.now()
    val origLoaded = savedStateHandle.get<Boolean>(KEY_ORIG_LOADED) == true

    if (isNew && savedStateHandle.get<Long>(KEY_ORIG_DATE) == null) {
        savedStateHandle[KEY_ORIG_TYPE] = TransactionType.EXPENSE.name
        savedStateHandle[KEY_ORIG_AMOUNT] = ""
        savedStateHandle[KEY_ORIG_CATEGORY] = ""
        savedStateHandle[KEY_ORIG_DATE] = savedDate.toEpochDay()
    }

    return FormUiState(
        isNew = isNew,
        type = savedType,
        amountText = savedAmount,
        currencyId = savedCurrencyId,
        categoryText = savedCategory,
        paymentMethodId = savedPaymentMethodId,
        description = savedDescription,
        date = savedDate,
        isLoading = !isNew && !origLoaded,
        showNoInternetDialog = showNoInternet,
        transactionId = transactionId,
        createdAt = savedStateHandle.get<Long>(KEY_CREATED_AT) ?: 0L,
    )
}

internal fun resolveInitialCurrencyId(savedStateHandle: SavedStateHandle, currList: List<CurrencyEntity>): Long? {
    if (currList.isEmpty()) return null
    val preferredId = savedStateHandle.get<Long>(KEY_ORIG_CURRENCY_ID)
    return (currList.firstOrNull { it.id == preferredId }
        ?: currList.firstOrNull { it.isDefault }
        ?: currList.first()).id
}

internal fun resolveInitialPaymentMethodId(savedStateHandle: SavedStateHandle, pmList: List<PaymentMethodEntity>): Long? {
    if (pmList.isEmpty()) return null
    val preferredId = savedStateHandle.get<Long>(KEY_ORIG_PAYMENT_METHOD_ID)
    return (pmList.firstOrNull { it.id == preferredId }
        ?: pmList.firstOrNull { it.isDefault }
        ?: pmList.first()).id
}

internal data class LastUsedDefaults(val currencyId: Long?, val paymentMethodId: Long?)

/**
 * New transaction: preselects the last used currency / payment method (once, remembered in [handle]).
 * Returns only the ids that were newly applied to the draft.
 */
internal suspend fun resolveLastUsedDefaults(
    handle: SavedStateHandle,
    repository: TransactionRepository,
): LastUsedDefaults {
    var currencyId: Long? = null
    var paymentMethodId: Long? = null
    val lastUsedId = repository.lastUsedCurrencyId()
    if (lastUsedId != null && handle.get<Long>(KEY_ORIG_CURRENCY_ID) == null) {
        handle[KEY_ORIG_CURRENCY_ID] = lastUsedId
        if (handle.get<Long>(KEY_CURRENCY_ID) == null) {
            handle[KEY_CURRENCY_ID] = lastUsedId
            currencyId = lastUsedId
        }
    }
    val lastUsedPmId = repository.lastUsedPaymentMethodId()
    if (lastUsedPmId != null && handle.get<Long>(KEY_ORIG_PAYMENT_METHOD_ID) == null) {
        handle[KEY_ORIG_PAYMENT_METHOD_ID] = lastUsedPmId
        if (handle.get<Long>(KEY_PAYMENT_METHOD_ID) == null) {
            handle[KEY_PAYMENT_METHOD_ID] = lastUsedPmId
            paymentMethodId = lastUsedPmId
        }
    }
    return LastUsedDefaults(currencyId, paymentMethodId)
}
