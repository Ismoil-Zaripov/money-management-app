package uz.moneymanager.feature.rates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RatesViewModel @Inject constructor(
    private val rateRepository: ExchangeRateRepository,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _isLoading = MutableStateFlow(false)

    private val currencies: Flow<List<CurrencyEntity>> = currencyRepository.observeAll()

    private val rates: Flow<List<ExchangeRateRow>> = _selectedDate.flatMapLatest { date ->
        rateRepository.observeForDate(date)
    }

    val uiState: StateFlow<RatesUiState> = combine(
        _selectedDate, currencies, rates, _isLoading
    ) { date, currencyList, rateList, loading ->
        RatesUiState(date = date, currencies = currencyList, rates = rateList, isLoading = loading, loaded = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RatesUiState())

    private val _events = MutableSharedFlow<RateEvent>()
    val events: SharedFlow<RateEvent> = _events.asSharedFlow()

    fun setDate(date: LocalDate) {
        if (date.isAfter(LocalDate.now())) {
            viewModelScope.launch {
                _events.emit(RateEvent.Error(R.string.err_future_date))
            }
            return
        }
        _selectedDate.value = date
    }

    fun importFromCbu() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = rateRepository.importFromCbu(_selectedDate.value)
                _events.emit(RateEvent.CbuResult(res.added, res.existing, res.notFound))
            } catch (e: DomainException) {
                _events.emit(RateEvent.Error(e.messageRes, e.args.toList()))
            } catch (e: Exception) {
                _events.emit(RateEvent.Error(R.string.err_cbu_fetch_failed))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveRate(
        baseId: Long,
        quoteId: Long,
        rate: BigDecimal,
        date: LocalDate = _selectedDate.value,
        originalDate: LocalDate? = null,
    ) {
        viewModelScope.launch {
            try {
                if (date.isAfter(LocalDate.now())) throw DomainException(R.string.err_future_date)
                if (rate.signum() <= 0) throw DomainException(R.string.err_rate_invalid)
                rateRepository.savePair(
                    date = date,
                    baseId = baseId,
                    quoteId = quoteId,
                    rate = rate,
                    originalDate = originalDate,
                )
                _selectedDate.value = date
                _events.emit(RateEvent.Success)
            } catch (e: DomainException) {
                _events.emit(RateEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }

    fun deleteRate(baseId: Long, quoteId: Long, date: LocalDate = _selectedDate.value) {
        viewModelScope.launch {
            try {
                rateRepository.deletePair(date, baseId, quoteId)
                _events.emit(RateEvent.Success)
            } catch (e: DomainException) {
                _events.emit(RateEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }
}
