package uz.moneymanager.feature.paymentmethods.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.data.repository.PaymentMethodRepository
import uz.moneymanager.domain.DomainException
import javax.inject.Inject

@HiltViewModel
class PaymentMethodsViewModel @Inject constructor(
    private val paymentMethodRepository: PaymentMethodRepository,
) : ViewModel() {

    val uiState: StateFlow<PaymentMethodsUiState> = paymentMethodRepository.observeWithUsage()
        .map { PaymentMethodsUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PaymentMethodsUiState())

    private val _events = MutableSharedFlow<PaymentMethodEvent>()
    val events: SharedFlow<PaymentMethodEvent> = _events.asSharedFlow()

    fun addPaymentMethod(name: String) {
        viewModelScope.launch {
            try {
                paymentMethodRepository.add(name)
                _events.emit(PaymentMethodEvent.Success)
            } catch (e: DomainException) {
                _events.emit(PaymentMethodEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }

    fun renamePaymentMethod(id: Long, newName: String) {
        viewModelScope.launch {
            try {
                paymentMethodRepository.rename(id, newName)
                _events.emit(PaymentMethodEvent.Success)
            } catch (e: DomainException) {
                _events.emit(PaymentMethodEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }

    fun deletePaymentMethod(id: Long) {
        viewModelScope.launch {
            try {
                paymentMethodRepository.delete(id)
                _events.emit(PaymentMethodEvent.Success)
            } catch (e: DomainException) {
                _events.emit(PaymentMethodEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }
}
