package uz.moneymanager.feature.currencies.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmBadge

@Composable
internal fun CurrencyUsageChip(transactionCount: Int) {
    MmBadge(pluralStringResource(R.plurals.currency_transactions_count, transactionCount, transactionCount))
}
