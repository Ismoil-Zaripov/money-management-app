package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
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
