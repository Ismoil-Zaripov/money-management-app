package uz.moneymanager.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.TransactionListRow
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.MonitoringTotals
import uz.moneymanager.domain.model.TransactionType
import java.time.LocalDate
import javax.inject.Inject

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
        // Set display currency default to UZS
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

    val transactions: Flow<PagingData<TransactionListRow>> = queryParams.flatMapLatest { (f, displayId, fallbackId) ->
        transactionRepository.paging(f, displayId, fallbackId)
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
