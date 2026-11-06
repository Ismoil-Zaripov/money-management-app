package uz.moneymanager.feature.transactionform.presentation

import androidx.lifecycle.SavedStateHandle
import uz.moneymanager.data.repository.TransactionDetails

/** Stores a loaded transaction as both the original snapshot and the editable draft; returns its plain amount text. */
internal fun storeLoadedTransaction(handle: SavedStateHandle, tx: TransactionDetails): String {
    val plainAmount = tx.amount.stripTrailingZeros().toPlainString()
    handle[KEY_ORIG_LOADED] = true
    handle[KEY_ORIG_TYPE] = tx.type.name
    handle[KEY_ORIG_AMOUNT] = plainAmount
    handle[KEY_ORIG_CURRENCY_ID] = tx.currencyId
    handle[KEY_ORIG_CATEGORY] = tx.categoryName
    handle[KEY_ORIG_PAYMENT_METHOD_ID] = tx.paymentMethodId
    handle[KEY_ORIG_DESCRIPTION] = tx.description
    handle[KEY_ORIG_DATE] = tx.date.toEpochDay()
    handle[KEY_CREATED_AT] = tx.createdAt

    handle[KEY_TYPE] = tx.type.name
    handle[KEY_AMOUNT] = plainAmount
    handle[KEY_CURRENCY_ID] = tx.currencyId
    handle[KEY_CATEGORY] = tx.categoryName
    handle[KEY_PAYMENT_METHOD_ID] = tx.paymentMethodId
    handle[KEY_DESCRIPTION] = tx.description
    handle[KEY_DATE] = tx.date.toEpochDay()
    return plainAmount
}
