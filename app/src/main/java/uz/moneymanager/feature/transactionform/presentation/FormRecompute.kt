package uz.moneymanager.feature.transactionform.presentation

import androidx.lifecycle.SavedStateHandle
import java.time.LocalDate
import uz.moneymanager.R
import uz.moneymanager.domain.AmountResult
import uz.moneymanager.domain.AmountValidator
import uz.moneymanager.domain.TransactionDescription
import uz.moneymanager.domain.model.TransactionType

/** Re-derives validation errors, `canSave` and `isDirty` from the current fields and the saved original snapshot. */
internal fun FormUiState.recomputed(
    handle: SavedStateHandle,
    isNew: Boolean,
    saveAttempted: Boolean,
    amountTouched: Boolean,
    categoryTouched: Boolean,
): FormUiState {
    val rawAmountError = when (val res = AmountValidator.validate(amountText)) {
        is AmountResult.Invalid -> res.messageRes
        is AmountResult.Valid -> null
    }
    val rawCategoryError = if (categoryText.isBlank()) R.string.err_category_required else null
    val descriptionError = if (TransactionDescription.isValid(TransactionDescription.normalize(description))) {
        null
    } else {
        R.string.err_description_too_long
    }
    val selectedCurrency = currencies.firstOrNull { it.id == currencyId }

    val origType = handle.get<String>(KEY_ORIG_TYPE)?.let {
        runCatching { TransactionType.valueOf(it) }.getOrNull()
    } ?: TransactionType.EXPENSE
    val origAmount = handle.get<String>(KEY_ORIG_AMOUNT) ?: ""
    val origCurrencyId = handle.get<Long>(KEY_ORIG_CURRENCY_ID)
    val origCategory = handle.get<String>(KEY_ORIG_CATEGORY) ?: ""
    val origPaymentMethodId = handle.get<Long>(KEY_ORIG_PAYMENT_METHOD_ID)
    val origDescription = handle.get<String>(KEY_ORIG_DESCRIPTION) ?: ""
    val origDate = handle.get<Long>(KEY_ORIG_DATE)?.let(LocalDate::ofEpochDay) ?: LocalDate.now()

    val dirty = if (isNew) {
        amountText.isNotBlank() ||
            categoryText.isNotBlank() ||
            description.isNotBlank() ||
            type != origType ||
            date != origDate ||
            (origCurrencyId != null && currencyId != null && currencyId != origCurrencyId) ||
            (origPaymentMethodId != null && paymentMethodId != null && paymentMethodId != origPaymentMethodId)
    } else {
        val loaded = handle.get<Boolean>(KEY_ORIG_LOADED) == true
        loaded && (
            type != origType ||
                amountText != origAmount ||
                currencyId != origCurrencyId ||
                paymentMethodId != origPaymentMethodId ||
                categoryText.trim() != origCategory.trim() ||
                description.trim() != origDescription.trim() ||
                date != origDate
            )
    }

    val showAmountError = saveAttempted || (amountTouched && amountText.isNotEmpty())
    val showCategoryError = saveAttempted || (categoryTouched && categoryText.isNotEmpty())

    return copy(
        currency = selectedCurrency,
        amountErrorRes = if (showAmountError) rawAmountError else null,
        categoryErrorRes = if (showCategoryError) rawCategoryError else null,
        descriptionErrorRes = descriptionError,
        canSave = !isLoading && rawAmountError == null && rawCategoryError == null &&
            descriptionError == null && currencyId != null && paymentMethodId != null,
        isDirty = dirty,
    )
}
