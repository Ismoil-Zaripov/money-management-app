package uz.moneymanager.ui.transactions.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.moneymanager.R
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.ExchangeRateRepository
import uz.moneymanager.data.repository.SaveResult
import uz.moneymanager.data.repository.TransactionInput
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.AmountResult
import uz.moneymanager.domain.AmountValidator
import uz.moneymanager.domain.DomainException
import uz.moneymanager.domain.RateValidator
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

sealed interface FormEvent {
    data object Saved : FormEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : FormEvent
    data class ShowMissingRates(
        val base: CurrencyEntity,
        val date: LocalDate,
        val missing: List<CurrencyEntity>,
        val suggestedRates: Map<Long, String>, // quoteId to prefilled rate string
    ) : FormEvent
}

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val currencyRepository: CurrencyRepository,
    private val categoryRepository: CategoryRepository,
    private val rateRepository: ExchangeRateRepository,
) : ViewModel() {

    val transactionId: Long? = savedStateHandle.get<String>("id")?.toLongOrNull()

    private val _type = MutableStateFlow(TransactionType.EXPENSE)
    val type: StateFlow<TransactionType> = _type.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _currencyId = MutableStateFlow<Long?>(null)
    val currencyId: StateFlow<Long?> = _currencyId.asStateFlow()

    private val _categoryText = MutableStateFlow("")
    val categoryText: StateFlow<String> = _categoryText.asStateFlow()

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    val currencies: StateFlow<List<CurrencyEntity>> = currencyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _events = MutableSharedFlow<FormEvent>()
    val events: SharedFlow<FormEvent> = _events.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            // Set default currency if creating new
            if (transactionId == null) {
                currencies.collect { list ->
                    if (_currencyId.value == null && list.isNotEmpty()) {
                        val def = list.firstOrNull { it.isDefault } ?: list.first()
                        _currencyId.value = def.id
                    }
                }
            }
        }

        if (transactionId != null) {
            viewModelScope.launch {
                _isLoading.value = true
                val tx = transactionRepository.getDetails(transactionId)
                if (tx != null) {
                    _type.value = tx.type
                    _amount.value = tx.amount.toPlainString()
                    _currencyId.value = tx.currencyId
                    _categoryText.value = tx.categoryName
                    _date.value = tx.date
                }
                _isLoading.value = false
            }
        }
    }

    fun setType(t: TransactionType) { _type.value = t }
    fun setAmount(a: String) { _amount.value = AmountValidator.sanitize(a) }
    fun setCurrencyId(id: Long) { _currencyId.value = id }
    fun setCategoryText(c: String) { _categoryText.value = c }
    fun setDate(d: LocalDate) {
        if (!d.isAfter(LocalDate.now())) {
            _date.value = d
        }
    }

    fun save(additionalRates: Map<Long, BigDecimal> = emptyMap()) {
        val currId = _currencyId.value ?: run {
            emitError(R.string.err_currency_required)
            return
        }

        val amountResult = AmountValidator.validate(_amount.value)
        if (amountResult is AmountResult.Invalid) {
            emitError(amountResult.messageRes)
            return
        }
        val validAmount = (amountResult as AmountResult.Valid).value

        if (_categoryText.value.isBlank()) {
            emitError(R.string.err_category_required)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val input = TransactionInput(
                    id = transactionId,
                    type = _type.value,
                    amount = validAmount,
                    currencyId = currId,
                    categoryName = _categoryText.value,
                    date = _date.value,
                )

                val result = transactionRepository.save(input, additionalRates)
                when (result) {
                    is SaveResult.Saved -> {
                        _events.emit(FormEvent.Saved)
                    }
                    is SaveResult.MissingRates -> {
                        // Prefill rates from CBU
                        val suggestions = rateRepository.suggestFromCbu(
                            base = result.base,
                            quotes = result.missing,
                            date = result.date,
                        ).mapValues { it.value.toPlainString() }

                        _events.emit(
                            FormEvent.ShowMissingRates(
                                base = result.base,
                                date = result.date,
                                missing = result.missing,
                                suggestedRates = suggestions,
                            )
                        )
                    }
                }
            } catch (e: DomainException) {
                _events.emit(FormEvent.Error(e.messageRes, e.args.toList()))
            } catch (e: Exception) {
                _events.emit(FormEvent.Error(R.string.err_amount_invalid))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun emitError(res: Int) {
        viewModelScope.launch {
            _events.emit(FormEvent.Error(res))
        }
    }
}
