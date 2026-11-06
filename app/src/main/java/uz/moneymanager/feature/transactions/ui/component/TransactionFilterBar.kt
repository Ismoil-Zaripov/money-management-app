package uz.moneymanager.feature.transactions.ui.component

import uz.moneymanager.core.designsystem.theme.ScreenHorizontalPadding
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmFilterChip
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.feature.transactions.ui.sheet.FilterSheet

@Composable
fun TransactionFilterBar(
    filter: MonitoringFilter,
    currencies: List<CurrencyEntity>,
    categories: List<CategoryEntity>,
    paymentMethods: List<PaymentMethodEntity>,
    displayCurrency: CurrencyEntity?,
    onCategorySelect: (Long?) -> Unit,
    onCurrencyFilterSelect: (Long?) -> Unit,
    onPaymentMethodSelect: (Long?) -> Unit,
    onDisplayCurrencySelect: (Long) -> Unit,
    onTypeSelect: (TransactionType?) -> Unit,
    onDateRangeSelect: (LocalDate?, LocalDate?) -> Unit,
    onOpenSheet: (FilterSheet) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item { DisplayCurrencyChip(displayCurrency, currencies, onDisplayCurrencySelect) }
        item {
            val name = categories.firstOrNull { it.id == filter.categoryId }?.name
            SheetFilterChip(R.string.filter_category, name, filter.categoryId != null, { onOpenSheet(FilterSheet.Category) }) {
                onCategorySelect(null)
            }
        }
        item {
            val name = paymentMethods.firstOrNull { it.id == filter.paymentMethodId }?.name
            SheetFilterChip(R.string.filter_payment_method, name, filter.paymentMethodId != null, { onOpenSheet(FilterSheet.PaymentCurrency) }) {
                onPaymentMethodSelect(null)
            }
        }
        item {
            val code = currencies.firstOrNull { it.id == filter.currencyId }?.code
            SheetFilterChip(R.string.filter_currency, code, filter.currencyId != null, { onOpenSheet(FilterSheet.PaymentCurrency) }) {
                onCurrencyFilterSelect(null)
            }
        }
        item {
            val typeName = when (filter.type) {
                TransactionType.INCOME -> stringResource(R.string.type_income)
                TransactionType.EXPENSE -> stringResource(R.string.type_expense)
                null -> null
            }
            SheetFilterChip(R.string.filter_type, typeName, filter.type != null, { onOpenSheet(FilterSheet.Type) }) {
                onTypeSelect(null)
            }
        }
        item {
            val active = filter.from != null || filter.to != null
            MmFilterChip(
                label = if (active) "${filter.from ?: "..."} - ${filter.to ?: "..."}" else stringResource(R.string.filter_date_range),
                selected = active,
                onClick = { onOpenSheet(FilterSheet.DateRange) },
                onClear = { onDateRangeSelect(null, null) }
            )
        }
    }
}

/** "Title: value" chip (value = "All" when null) that opens a filter sheet; (x) clears when selected. */
@Composable
private fun SheetFilterChip(
    @StringRes titleRes: Int,
    value: String?,
    selected: Boolean,
    onClick: () -> Unit,
    onClear: () -> Unit,
) {
    MmFilterChip(
        label = "${stringResource(titleRes)}: ${value ?: stringResource(R.string.filter_all)}",
        selected = selected,
        onClick = onClick,
        onClear = onClear
    )
}
