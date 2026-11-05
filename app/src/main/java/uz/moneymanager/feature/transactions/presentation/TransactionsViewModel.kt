package uz.moneymanager.feature.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.PaymentMethodRepository
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.MonitoringTotals
import uz.moneymanager.domain.TransactionAmountCalculator
import uz.moneymanager.domain.model.TransactionType

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val currencyRepository: CurrencyRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : ViewModel() {

    private val _filter = MutableStateFlow(MonitoringFilter())
    val filter: StateFlow<MonitoringFilter> = _filter.asStateFlow()

    val currencies: StateFlow<List<CurrencyEntity>> = currencyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentMethods: StateFlow<List<PaymentMethodEntity>> = paymentMethodRepository.observeAll()
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

    val dailyTotals: StateFlow<Map<LocalDate, MonitoringTotals>> = queryParams.flatMapLatest { (f, displayId, fallbackId) ->
        transactionRepository.dailyTotals(f, displayId, fallbackId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val transactions: Flow<PagingData<TransactionListItemUiModel>> = queryParams.flatMapLatest { (f, displayId, fallbackId) ->
        val currencyCodesFlow = currencies
            .filter { it.isNotEmpty() }
            .map { list ->
                val fallbackCode = list.firstOrNull { it.id == fallbackId }?.code
                    ?: list.firstOrNull { it.isDefault }?.code
                    ?: list.firstOrNull()?.code
                    ?: "UZS"
                val displayCode = list.firstOrNull { it.id == displayId }?.code ?: fallbackCode
                displayCode to fallbackCode
            }.distinctUntilChanged()

        transactionRepository.paging(f, displayId, fallbackId)
            .cachedIn(viewModelScope)
            .combine(currencyCodesFlow) { pagingData, (dCode, fCode) ->
                pagingData
                    .map { row ->
                        val converted = TransactionAmountCalculator.calculate(
                            amount = row.amount,
                            currencyCode = row.currencyCode,
                            displayCurrencyCode = dCode,
                            fallbackCurrencyCode = fCode,
                            displayRate = row.displayRate,
                            fallbackRate = row.fallbackRate,
                        )
                        TransactionListItemUiModel.Item(
                            id = row.id,
                            title = row.categoryName,
                            paymentMethodName = row.paymentMethodName,
                            amountText = FormatUtils.formatAmount(converted.amount, converted.currencyCode, row.type),
                            originalAmountText = converted.originalAmount?.let { origAmount ->
                                FormatUtils.formatAmount(
                                    amount = origAmount.abs(),
                                    currencyCode = converted.originalCurrencyCode ?: row.currencyCode,
                                    type = null,
                                    includePositiveSign = false,
                                )
                            },
                            type = row.type,
                            date = row.date,
                        )
                    }
                    .insertSeparators { before: TransactionListItemUiModel.Item?, after: TransactionListItemUiModel.Item? ->
                        TransactionDateSeparator.createSeparator(before, after)
                    }
            }
    }

    val totals: StateFlow<MonitoringTotals> = queryParams.flatMapLatest { (f, displayId, fallbackId) ->
        transactionRepository.totals(f, displayId, fallbackId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonitoringTotals())

    val uiState: StateFlow<TransactionsUiState> = combine(
        combine(_filter, currencies, categories, paymentMethods, defaultCurrency) { f, cur, cat, pm, def ->
            TransactionsUiState(
                filter = f,
                currencies = cur,
                categories = cat,
                paymentMethods = pm,
                defaultCurrency = def,
            )
        },
        totals,
        dailyTotals,
    ) { base, t, d -> base.copy(totals = t, dailyTotals = d) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionsUiState())

    fun onEvent(event: TransactionsEvent) {
        when (event) {
            is TransactionsEvent.CategorySelected -> setCategory(event.id)
            is TransactionsEvent.CurrencyFilterSelected -> setCurrencyFilter(event.id)
            is TransactionsEvent.PaymentMethodSelected -> setPaymentMethodFilter(event.id)
            is TransactionsEvent.DisplayCurrencySelected -> setDisplayCurrency(event.id)
            is TransactionsEvent.TypeSelected -> setType(event.type)
            is TransactionsEvent.DateRangeSelected -> setDateRange(event.from, event.to)
            is TransactionsEvent.DeleteConfirmed -> deleteTransaction(event.id)
            TransactionsEvent.ResetFilters -> resetFilters()
        }
    }

    fun setCategory(categoryId: Long?) {
        _filter.value = _filter.value.copy(categoryId = categoryId)
    }

    fun setPaymentMethodFilter(paymentMethodId: Long?) {
        _filter.value = _filter.value.copy(paymentMethodId = paymentMethodId)
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
