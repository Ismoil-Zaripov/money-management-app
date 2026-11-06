package uz.moneymanager.feature.transactionform.presentation

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.ExchangeRateRepository
import uz.moneymanager.data.repository.PaymentMethodRepository
import uz.moneymanager.data.repository.SaveResult
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.DomainException
import uz.moneymanager.domain.model.TransactionType

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    currencyRepository: CurrencyRepository,
    categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val rateRepository: ExchangeRateRepository,
) : ViewModel() {

    val transactionId: Long? = savedStateHandle.get<String>("id")?.toLongOrNull()
    private val isNew: Boolean = transactionId == null

    private var saveAttempted: Boolean
        get() = savedStateHandle[KEY_SAVE_ATTEMPTED] ?: false
        set(value) { savedStateHandle[KEY_SAVE_ATTEMPTED] = value }

    private var amountTouched: Boolean
        get() = savedStateHandle[KEY_AMOUNT_TOUCHED] ?: false
        set(value) { savedStateHandle[KEY_AMOUNT_TOUCHED] = value }

    private var categoryTouched: Boolean
        get() = savedStateHandle[KEY_CATEGORY_TOUCHED] ?: false
        set(value) { savedStateHandle[KEY_CATEGORY_TOUCHED] = value }

    private var showNoInternet: Boolean
        get() = savedStateHandle[KEY_SHOW_NO_INTERNET] ?: false
        set(value) { savedStateHandle[KEY_SHOW_NO_INTERNET] = value }

    private val currenciesFlow: StateFlow<List<CurrencyEntity>> = currencyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val categoriesFlow: StateFlow<List<CategoryEntity>> = categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val suggestionsFlow: StateFlow<List<String>> = transactionRepository.recentCategoryNames(RECENT_CATEGORY_LIMIT)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val paymentMethodsFlow: StateFlow<List<PaymentMethodEntity>> = paymentMethodRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val categoryUsageFlow: StateFlow<Map<String, Int>> =
        (if (isNew) flowOf(emptyMap())
        else categoryRepository.observeWithUsage().map { list -> list.associate { it.category.nameKey to it.transactionCount } })
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FormEvent>()
    val events: SharedFlow<FormEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            if (isNew) {
                val defaults = resolveLastUsedDefaults(savedStateHandle, transactionRepository)
                defaults.currencyId?.let { id -> _uiState.update { it.copy(currencyId = id).recompute() } }
                defaults.paymentMethodId?.let { id -> _uiState.update { it.copy(paymentMethodId = id).recompute() } }
            } else if (savedStateHandle.get<Boolean>(KEY_ORIG_LOADED) != true) {
                _uiState.update { it.copy(isLoading = true).recompute() }
                val tx = transactionRepository.getDetails(transactionId!!)
                if (tx != null) {
                    val plainAmount = storeLoadedTransaction(savedStateHandle, tx)

                    _uiState.update {
                        it.copy(
                            type = tx.type,
                            amountText = plainAmount,
                            currencyId = tx.currencyId,
                            categoryText = tx.categoryName,
                            paymentMethodId = tx.paymentMethodId,
                            description = tx.description,
                            date = tx.date,
                            createdAt = tx.createdAt,
                            isLoading = false,
                        ).recompute()
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false).recompute() }
                }
            }

            // Fonda valyuta, to'lov usuli va kategoriya ro'yxatlarini yuklash
            combine(currenciesFlow, categoriesFlow, suggestionsFlow, paymentMethodsFlow, categoryUsageFlow) { currList, catList, recentList, pmList, usage ->
                FormDependencies(currList, catList, recentList, pmList, usage)
            }.collect { deps ->
                _uiState.update { it.withDependencies(savedStateHandle, deps).recompute() }
            }
        }
    }

    private fun buildInitialState(): FormUiState =
        buildInitialFormState(savedStateHandle, isNew, transactionId, showNoInternet).recompute()

    private fun FormUiState.recompute(): FormUiState =
        recomputed(savedStateHandle, isNew, saveAttempted, amountTouched, categoryTouched)

    fun setType(t: TransactionType) {
        savedStateHandle[KEY_TYPE] = t.name
        _uiState.update { it.copy(type = t).recompute() }
    }

    fun setAmount(raw: String) {
        val sanitized = AmountInput.sanitize(raw)
        savedStateHandle[KEY_AMOUNT] = sanitized
        _uiState.update { it.copy(amountText = sanitized).recompute() }
    }

    fun setCurrencyId(id: Long) {
        savedStateHandle[KEY_CURRENCY_ID] = id
        _uiState.update { it.copy(currencyId = id).recompute() }
    }

    fun setCategoryText(c: String) {
        savedStateHandle[KEY_CATEGORY] = c
        _uiState.update { it.copy(categoryText = c).recompute() }
    }

    fun setPaymentMethodId(id: Long) {
        savedStateHandle[KEY_PAYMENT_METHOD_ID] = id
        _uiState.update { it.copy(paymentMethodId = id).recompute() }
    }

    fun addQuickPaymentMethod(name: String) {
        viewModelScope.launch {
            try {
                val created = paymentMethodRepository.add(name)
                setPaymentMethodId(created.id)
            } catch (e: DomainException) {
                _events.emit(FormEvent.Error(e.messageRes, e.args.toList()))
            }
        }
    }

    fun setDescription(d: String) {
        savedStateHandle[KEY_DESCRIPTION] = d
        _uiState.update { it.copy(description = d).recompute() }
    }

    fun setDate(d: LocalDate) {
        if (!d.isAfter(LocalDate.now())) {
            savedStateHandle[KEY_DATE] = d.toEpochDay()
            _uiState.update { it.copy(date = d).recompute() }
        }
    }

    fun onAmountBlur() {
        if (!amountTouched) {
            amountTouched = true
            _uiState.update { it.recompute() }
        }
    }

    fun onCategoryBlur() {
        if (!categoryTouched) {
            categoryTouched = true
            _uiState.update { it.recompute() }
        }
    }

    fun dismissNoInternetDialog() {
        showNoInternet = false
        _uiState.update { it.copy(showNoInternetDialog = false) }
    }

    private fun presentNoInternetDialog() {
        showNoInternet = true
        _uiState.update { it.copy(showNoInternetDialog = true) }
    }

    fun save(additionalRates: Map<Long, BigDecimal> = emptyMap()) {
        val current = _uiState.value
        if (current.isSaving || current.isLoading) return

        saveAttempted = true
        val validated = current.recompute()
        _uiState.value = validated

        val input = when (val check = validated.checkForSave(transactionId)) {
            is SaveCheck.Rejected -> {
                check.errorRes?.let(::emitError)
                return
            }
            is SaveCheck.Ready -> check.input
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                when (val result = transactionRepository.save(input, additionalRates)) {
                    is SaveResult.Saved -> {
                        _events.emit(FormEvent.Saved)
                    }
                    is SaveResult.MissingRates -> {
                        val suggestions = rateRepository.suggestOrNull(result) ?: run {
                            presentNoInternetDialog()
                            return@launch
                        }

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
            } catch (e: CancellationException) {
                throw e
            } catch (e: DomainException) {
                _events.emit(FormEvent.Error(e.messageRes, e.args.toList()))
            } catch (_: IOException) {
                presentNoInternetDialog()
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    /** Tahrirlash rejimida tranzaksiyani o'chiradi va [FormEvent.Deleted] yuboradi. */
    fun delete() {
        val id = transactionId ?: return
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                transactionRepository.delete(id)
                _events.emit(FormEvent.Deleted)
            } catch (e: CancellationException) {
                throw e
            } catch (e: DomainException) {
                _events.emit(FormEvent.Error(e.messageRes, e.args.toList()))
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun emitError(res: Int) {
        viewModelScope.launch {
            _events.emit(FormEvent.Error(res))
        }
    }

    private companion object {
        const val RECENT_CATEGORY_LIMIT = 6
    }
}
