package uz.moneymanager.ui.transactions

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.MonitoringTotals
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@Immutable
data class TransactionUiItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val amountText: String,
    val type: TransactionType,
)

object FormatUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun formatDate(date: LocalDate): String = date.format(dateFormatter)

    fun formatAmount(
        amount: BigDecimal,
        currencyCode: String,
        type: TransactionType? = null,
        includePositiveSign: Boolean = false,
    ): String {
        val activeLocale = Locale.getDefault()
        val localeSymbols = DecimalFormatSymbols.getInstance(activeLocale)
        val symbols = DecimalFormatSymbols(activeLocale).apply {
            groupingSeparator = '\u00A0'
            decimalSeparator = localeSymbols.decimalSeparator
        }
        val df = DecimalFormat("#,##0.##", symbols)
        val sign = when (type) {
            TransactionType.INCOME -> "+"
            TransactionType.EXPENSE -> "\u2212"
            null -> when {
                amount.signum() < 0 -> "\u2212"
                amount.signum() > 0 && includePositiveSign -> "+"
                else -> ""
            }
        }
        val formattedValue = df.format(amount.abs())
        return "$sign$formattedValue\u00A0$currencyCode"
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val currencyRepository: CurrencyRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _filter = MutableStateFlow(MonitoringFilter())
    val filter: StateFlow<MonitoringFilter> = _filter.asStateFlow()

    val currencies: StateFlow<List<CurrencyEntity>> = currencyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val defaultCurrency: StateFlow<CurrencyEntity?> = currencies
        .combine(MutableStateFlow(Unit)) { list, _ -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            defaultCurrency.collect { def ->
                if (def != null && _filter.value.displayCurrencyId == null) {
                    _filter.value = _filter.value.copy(displayCurrencyId = def.id)
                }
            }
        }
    }

    private val queryParams = combine(_filter, defaultCurrency) { f, def ->
        val fallbackId = def?.id ?: 1L
        val displayId = f.displayCurrencyId ?: fallbackId
        Triple(f, displayId, fallbackId)
    }

    val transactions: Flow<PagingData<TransactionUiItem>> = queryParams.flatMapLatest { (f, displayId, fallbackId) ->
        transactionRepository.paging(f, displayId, fallbackId).map { pagingData ->
            pagingData.map { row ->
                TransactionUiItem(
                    id = row.id,
                    title = row.categoryName,
                    subtitle = FormatUtils.formatDate(row.date),
                    amountText = FormatUtils.formatAmount(row.amount, row.currencyCode, row.type),
                    type = row.type,
                )
            }
        }
    }.cachedIn(viewModelScope)

    val totals: StateFlow<MonitoringTotals> = queryParams.flatMapLatest { (f, displayId, fallbackId) ->
        transactionRepository.totals(f, displayId, fallbackId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonitoringTotals())

    fun setCategory(categoryId: Long?) {
        _filter.value = _filter.value.copy(categoryId = categoryId)
    }

    fun setCurrencyFilter(currencyId: Long?) {
        _filter.value = _filter.value.copy(currencyId = currencyId)
    }

    fun setDisplayCurrency(displayCurrencyId: Long) {
        _filter.value = _filter.value.copy(displayCurrencyId = displayCurrencyId)
    }

    fun setType(type: TransactionType?) {
        _filter.value = _filter.value.copy(type = type)
    }

    fun setDateRange(from: LocalDate?, to: LocalDate?) {
        _filter.value = _filter.value.copy(from = from, to = to)
    }

    fun resetFilters() {
        val defId = defaultCurrency.value?.id
        _filter.value = MonitoringFilter(displayCurrencyId = defId)
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            transactionRepository.delete(id)
        }
    }
}
