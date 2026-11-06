package uz.moneymanager.feature.transactions.ui.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.feature.transactions.presentation.TransactionFilterDraft
import uz.moneymanager.feature.transactions.ui.filter.section.CategorySection
import uz.moneymanager.feature.transactions.ui.filter.section.CurrencySection
import uz.moneymanager.feature.transactions.ui.filter.section.DateRangeSection
import uz.moneymanager.feature.transactions.ui.filter.section.DisplayCurrencySection
import uz.moneymanager.feature.transactions.ui.filter.section.FilterSectionTitle
import uz.moneymanager.feature.transactions.ui.filter.section.PaymentMethodSection
import uz.moneymanager.feature.transactions.ui.filter.section.TypeSection

/** Stateless filter UI: header, scrollable sections, sticky "Show N Results". */
@Composable
fun TransactionFilterScreen(
    draft: TransactionFilterDraft,
    categories: List<CategoryEntity>,
    paymentMethods: List<PaymentMethodEntity>,
    currencies: List<CurrencyEntity>,
    displayCurrencyId: Long?,
    resultCount: Int?,
    onDraftChange: (TransactionFilterDraft) -> Unit,
    onDisplayCurrencySelect: (Long) -> Unit,
    onClearAll: () -> Unit,
    onClose: () -> Unit,
    onShow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxHeight()) {
        TransactionFilterHeader(activeCount = draft.activeCount, onClose = onClose, onClearAll = onClearAll)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            FilterSection {
                FilterSectionTitle(stringResource(R.string.filter_type))
                TypeSection(draft.type) { onDraftChange(draft.copy(type = it)) }
            }
            FilterSection {
                FilterSectionTitle(stringResource(R.string.filter_category))
                CategorySection(categories, draft.categoryId) { onDraftChange(draft.copy(categoryId = it)) }
            }
            FilterSection {
                PaymentMethodSection(paymentMethods, draft.paymentMethodId) { onDraftChange(draft.copy(paymentMethodId = it)) }
            }
            FilterSection {
                CurrencySection(currencies, draft.currencyId) { onDraftChange(draft.copy(currencyId = it)) }
            }
            FilterSection {
                FilterSectionTitle(stringResource(R.string.filter_date_range))
                DateRangeSection(draft.from, draft.to) { from, to -> onDraftChange(draft.copy(from = from, to = to)) }
            }
            FilterSection(divider = false) {
                DisplayCurrencySection(currencies, displayCurrencyId, onDisplayCurrencySelect)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        TransactionFilterFooter(count = resultCount, onShow = onShow)
    }
}

@Composable
private fun FilterSection(divider: Boolean = true, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) { content() }
    if (divider) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}
