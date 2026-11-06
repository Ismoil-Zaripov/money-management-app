package uz.moneymanager.feature.transactionform.presentation

import uz.moneymanager.R
import uz.moneymanager.data.repository.TransactionInput
import uz.moneymanager.domain.AmountResult
import uz.moneymanager.domain.AmountValidator

internal sealed interface SaveCheck {
    data class Ready(val input: TransactionInput) : SaveCheck

    /** [errorRes] is shown as a snackbar; `null` means the inline field errors already explain it. */
    data class Rejected(val errorRes: Int?) : SaveCheck
}

internal fun FormUiState.checkForSave(transactionId: Long?): SaveCheck {
    val currId = currencyId ?: return SaveCheck.Rejected(R.string.err_currency_required)
    val pmId = paymentMethodId ?: return SaveCheck.Rejected(R.string.err_payment_method_required)

    val amount = when (val result = AmountValidator.validate(amountText)) {
        is AmountResult.Invalid ->
            return SaveCheck.Rejected(result.messageRes.takeIf { it == R.string.err_amount_invalid })
        is AmountResult.Valid -> result.value
    }
    if (categoryText.isBlank() || descriptionErrorRes != null) return SaveCheck.Rejected(null)

    return SaveCheck.Ready(
        TransactionInput(
            id = transactionId,
            type = type,
            amount = amount,
            currencyId = currId,
            categoryName = categoryText,
            paymentMethodId = pmId,
            date = date,
            description = description,
        )
    )
}
