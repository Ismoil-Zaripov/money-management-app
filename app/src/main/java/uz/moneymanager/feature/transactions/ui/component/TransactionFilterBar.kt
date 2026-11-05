package uz.moneymanager.feature.transactions.ui.component

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
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.model.TransactionType

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
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item { DisplayCurrencyChip(displayCurrency, currencies, onDisplayCurrencySelect) }
        item { CategoryChip(filter.categoryId, categories, onCategorySelect) }
        item { PaymentMethodChip(filter.paymentMethodId, paymentMethods, onPaymentMethodSelect) }
        item { CurrencyFilterChip(filter.currencyId, currencies, onCurrencyFilterSelect) }
        item { TypeChip(filter.type, onTypeSelect) }
        item { DateRangeFilterChip(filter.from, filter.to, onDateRangeSelect) }
    }
}

@Composable
private fun DisplayCurrencyChip(
    displayCurrency: CurrencyEntity?,
    currencies: List<CurrencyEntity>,
    onSelect: (Long) -> Unit,
) {
    FilterMenuChip(
        label = "${stringResource(R.string.display_currency)}: ${displayCurrency?.code ?: "UZS"}",
        selected = true
    ) { dismiss ->
        currencies.forEach { c ->
            FilterMenuItem("${c.code} - ${c.name}") {
                onSelect(c.id)
                dismiss()
            }
        }
    }
}

@Composable
private fun CategoryChip(
    selectedId: Long?,
    categories: List<CategoryEntity>,
    onSelect: (Long?) -> Unit,
) {
    val selected = categories.firstOrNull { it.id == selectedId }
    FilterMenuChip(
        label = "${stringResource(R.string.filter_category)}: ${selected?.name ?: stringResource(R.string.filter_all)}",
        selected = selectedId != null
    ) { dismiss ->
        FilterMenuItem(stringResource(R.string.filter_all)) {
            onSelect(null)
            dismiss()
        }
        categories.forEach { cat ->
            FilterMenuItem(cat.name) {
                onSelect(cat.id)
                dismiss()
            }
        }
    }
}

@Composable
private fun PaymentMethodChip(
    selectedId: Long?,
    paymentMethods: List<PaymentMethodEntity>,
    onSelect: (Long?) -> Unit,
) {
    val selected = paymentMethods.firstOrNull { it.id == selectedId }
    FilterMenuChip(
        label = "${stringResource(R.string.filter_payment_method)}: ${selected?.name ?: stringResource(R.string.filter_all)}",
        selected = selectedId != null
    ) { dismiss ->
        FilterMenuItem(stringResource(R.string.filter_all)) {
            onSelect(null)
            dismiss()
        }
        paymentMethods.forEach { pm ->
            FilterMenuItem(pm.name) {
                onSelect(pm.id)
                dismiss()
            }
        }
    }
}

@Composable
private fun CurrencyFilterChip(
    selectedId: Long?,
    currencies: List<CurrencyEntity>,
    onSelect: (Long?) -> Unit,
) {
    val selected = currencies.firstOrNull { it.id == selectedId }
    FilterMenuChip(
        label = "${stringResource(R.string.filter_currency)}: ${selected?.code ?: stringResource(R.string.filter_all)}",
        selected = selectedId != null
    ) { dismiss ->
        FilterMenuItem(stringResource(R.string.filter_all)) {
            onSelect(null)
            dismiss()
        }
        currencies.forEach { c ->
            FilterMenuItem(c.code) {
                onSelect(c.id)
                dismiss()
            }
        }
    }
}

@Composable
private fun TypeChip(
    type: TransactionType?,
    onSelect: (TransactionType?) -> Unit,
) {
    val typeLabel = when (type) {
        TransactionType.INCOME -> stringResource(R.string.type_income)
        TransactionType.EXPENSE -> stringResource(R.string.type_expense)
        null -> stringResource(R.string.filter_all)
    }
    FilterMenuChip(
        label = "${stringResource(R.string.filter_type)}: $typeLabel",
        selected = type != null
    ) { dismiss ->
        FilterMenuItem(stringResource(R.string.filter_all)) {
            onSelect(null)
            dismiss()
        }
        FilterMenuItem(stringResource(R.string.type_income)) {
            onSelect(TransactionType.INCOME)
            dismiss()
        }
        FilterMenuItem(stringResource(R.string.type_expense)) {
            onSelect(TransactionType.EXPENSE)
            dismiss()
        }
    }
}
