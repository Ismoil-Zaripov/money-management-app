package uz.moneymanager.feature.transactions.presentation

import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.data.local.TransactionListRow

/** Room row -> list item. Amount is shown in the transaction's own currency (AUDIT §4); sign comes from the UI. */
internal fun TransactionListRow.toUiItem(): TransactionListItemUiModel.Item =
    TransactionListItemUiModel.Item(
        id = id,
        title = categoryName,
        paymentMethodName = paymentMethodName,
        amountText = FormatUtils.formatAmount(amount.abs(), currencyCode),
        type = type,
        date = date,
        categoryKey = categoryKey,
        description = description,
        timeText = FormatUtils.formatTime(createdAt),
    )
