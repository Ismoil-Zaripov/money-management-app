package uz.moneymanager.ui.rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.R
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.ExchangeRateRow
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.ExchangeRateRepository
import uz.moneymanager.domain.DomainException
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

sealed interface RateEvent {
    data class CbuResult(val added: Int, val existing: Int, val notFound: List<String>) : RateEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : RateEvent
    data object Success : RateEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RatesViewModel @Inject constructor(
    private val rateRepository: ExchangeRateRepository,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val currencies: StateFlow<List<CurrencyEntity>> = currencyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rates: StateFlow<List<ExchangeRateRow>> = _selectedDate.flatMapLatest { date ->
        rateRepository.observeForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _events = MutableSharedFlow<RateEvent>()
    val events: SharedFlow<RateEvent> = _events.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun setDate(date: LocalDate) {
        if (!date.isAfter(LocalDate.now())) {
            _selectedDate.value = date
        }
    }

    fun importFromCbu() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = rateRepository.importFromCbu(_selectedDate.value)
                _events.emit(RateEvent.CbuResult(res.added, res.existing, res.notFound))
            } catch (e: Exception) {
                _events.emit(RateEvent.Error(R.string.err_cbu_fetch_failed))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveRate(baseId: Long, quoteId: Long, rate: BigDecimal) {
        viewModelScope.launch {
            try {
                rateRepository.savePair(_selectedDate.value, baseId, quoteId, rate)
                _events.emit(RateEvent.Success)
            } catch (e: DomainException) {
                _events.emit(RateEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }

    fun deleteRate(baseId: Long, quoteId: Long) {
        viewModelScope.launch {
            try {
                rateRepository.deletePair(_selectedDate.value, baseId, quoteId)
                _events.emit(RateEvent.Success)
            } catch (e: DomainException) {
                _events.emit(RateEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }
}
