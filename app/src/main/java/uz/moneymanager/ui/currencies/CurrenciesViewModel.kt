package uz.moneymanager.ui.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.R
import uz.moneymanager.data.local.CurrencyWithUsage
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.domain.DomainException
import javax.inject.Inject

sealed interface CurrencyEvent {
    data object Success : CurrencyEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : CurrencyEvent
}

@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    val currenciesWithUsage: StateFlow<List<CurrencyWithUsage>> = currencyRepository.observeWithUsage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _events = MutableSharedFlow<CurrencyEvent>()
    val events: SharedFlow<CurrencyEvent> = _events.asSharedFlow()

    fun addCurrency(code: String, name: String) {
        viewModelScope.launch {
            try {
                currencyRepository.add(code, name)
                _events.emit(CurrencyEvent.Success)
            } catch (e: DomainException) {
                _events.emit(CurrencyEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }

    fun updateCurrency(id: Long, code: String, name: String) {
        viewModelScope.launch {
            try {
                currencyRepository.update(id, code, name)
                _events.emit(CurrencyEvent.Success)
            } catch (e: DomainException) {
                _events.emit(CurrencyEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }

    fun deleteCurrency(id: Long) {
        viewModelScope.launch {
            try {
                currencyRepository.delete(id)
                _events.emit(CurrencyEvent.Success)
            } catch (e: DomainException) {
                _events.emit(CurrencyEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }
}
