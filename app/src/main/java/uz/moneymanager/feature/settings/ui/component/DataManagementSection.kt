package uz.moneymanager.feature.settings.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R

@Composable
internal fun DataManagementSection(
    onNavigateCurrencies: () -> Unit,
    onNavigateRates: () -> Unit,
    onNavigateCategories: () -> Unit,
    onNavigatePaymentMethods: () -> Unit,
) {
    SettingsItem(
        icon = Icons.Default.MonetizationOn,
        title = stringResource(R.string.title_currencies),
        subtitle = stringResource(R.string.desc_currencies),
        onClick = onNavigateCurrencies
    )
    SettingsItem(
        icon = Icons.Default.CurrencyExchange,
        title = stringResource(R.string.title_exchange_rates),
        subtitle = stringResource(R.string.desc_exchange_rates),
        onClick = onNavigateRates
    )
    SettingsItem(
        icon = Icons.Default.Category,
        title = stringResource(R.string.title_categories),
        subtitle = stringResource(R.string.desc_categories),
        onClick = onNavigateCategories
    )
    SettingsItem(
        icon = Icons.Default.Payments,
        title = stringResource(R.string.title_payment_methods),
        subtitle = stringResource(R.string.desc_payment_methods),
        onClick = onNavigatePaymentMethods
    )
}
