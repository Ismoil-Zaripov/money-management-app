package uz.moneymanager.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.TransactionListRow
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.MonitoringFilter
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    val currencies: StateFlow<List<CurrencyEntity>> = currencyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val defaultCurrency: StateFlow<CurrencyEntity?> = currencies
        .combine(MutableStateFlow(Unit)) { list, _ -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: Flow<PagingData<TransactionListRow>> = defaultCurrency.flatMapLatest { defaultCurr ->
        val defaultId = defaultCurr?.id ?: 1L
        transactionRepository.paging(
            filter = MonitoringFilter(),
            displayCurrencyId = defaultId,
            fallbackCurrencyId = defaultId,
        )
    }.cachedIn(viewModelScope)

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            transactionRepository.delete(id)
        }
    }
}
