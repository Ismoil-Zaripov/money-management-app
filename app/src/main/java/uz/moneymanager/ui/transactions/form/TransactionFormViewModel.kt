package uz.moneymanager.ui.transactions.form

import androidx.compose.runtime.Immutable
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
        val suggestedRates: Map<Long, String>,
    ) : FormEvent
}

@Immutable
data class FormUiState(
    val isNew: Boolean = true,
    val type: TransactionType = TransactionType.EXPENSE,
    val amountText: String = "",
    val currencyId: Long? = null,
    val currency: CurrencyEntity? = null,
    val categoryText: String = "",
    val date: LocalDate = LocalDate.now(),
    val currencies: List<CurrencyEntity> = emptyList(),
    val allCategories: List<CategoryEntity> = emptyList(),
    val categorySuggestions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val amountErrorRes: Int? = null,
    val categoryErrorRes: Int? = null,
    val canSave: Boolean = false,
    val isDirty: Boolean = false,
)

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    currencyRepository: CurrencyRepository,
    categoryRepository: CategoryRepository,
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

    private val currenciesFlow: StateFlow<List<CurrencyEntity>> = currencyRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val categoriesFlow: StateFlow<List<CategoryEntity>> = categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val suggestionsFlow: StateFlow<List<String>> = transactionRepository.recentCategoryNames(RECENT_CATEGORY_LIMIT)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FormEvent>()
    val events: SharedFlow<FormEvent> = _events.asSharedFlow()

    init {
        // Fonda valyuta va kategoriya ro'yxatlarini yuklash (ekran ochilishini kutdirmaydi)
        viewModelScope.launch {
            combine(currenciesFlow, categoriesFlow, suggestionsFlow) { currList, catList, recentList ->
                Triple(currList, catList, recentList)
            }.collect { (currList, catList, recentList) ->
                _uiState.update { current ->
                    val resolvedCurrencyId = current.currencyId ?: resolveInitialCurrencyId(currList)
                    if (current.currencyId == null && resolvedCurrencyId != null) {
                        savedStateHandle[KEY_CURRENCY_ID] = resolvedCurrencyId
                        if (savedStateHandle.get<Long>(KEY_ORIG_CURRENCY_ID) == null) {
                            savedStateHandle[KEY_ORIG_CURRENCY_ID] = resolvedCurrencyId
                        }
                    }
                    current.copy(
                        currencyId = resolvedCurrencyId,
                        currencies = currList,
                        allCategories = catList,
                        categorySuggestions = recentList,
                    ).recompute()
                }
            }
        }

        if (isNew) {
            viewModelScope.launch {
                val lastUsedId = transactionRepository.lastUsedCurrencyId()
                if (lastUsedId != null && savedStateHandle.get<Long>(KEY_ORIG_CURRENCY_ID) == null) {
                    savedStateHandle[KEY_ORIG_CURRENCY_ID] = lastUsedId
                    if (savedStateHandle.get<Long>(KEY_CURRENCY_ID) == null) {
                        savedStateHandle[KEY_CURRENCY_ID] = lastUsedId
                        _uiState.update { it.copy(currencyId = lastUsedId).recompute() }
                    }
                }
            }
        } else if (savedStateHandle.get<Boolean>(KEY_ORIG_LOADED) != true) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true).recompute() }
                val tx = transactionRepository.getDetails(transactionId!!)
                if (tx != null) {
                    val plainAmount = tx.amount.stripTrailingZeros().toPlainString()
                    savedStateHandle[KEY_ORIG_LOADED] = true
                    savedStateHandle[KEY_ORIG_TYPE] = tx.type.name
                    savedStateHandle[KEY_ORIG_AMOUNT] = plainAmount
                    savedStateHandle[KEY_ORIG_CURRENCY_ID] = tx.currencyId
                    savedStateHandle[KEY_ORIG_CATEGORY] = tx.categoryName
                    savedStateHandle[KEY_ORIG_DATE] = tx.date.toEpochDay()

                    savedStateHandle[KEY_TYPE] = tx.type.name
                    savedStateHandle[KEY_AMOUNT] = plainAmount
                    savedStateHandle[KEY_CURRENCY_ID] = tx.currencyId
                    savedStateHandle[KEY_CATEGORY] = tx.categoryName
                    savedStateHandle[KEY_DATE] = tx.date.toEpochDay()

                    _uiState.update {
                        it.copy(
                            type = tx.type,
                            amountText = plainAmount,
                            currencyId = tx.currencyId,
                            categoryText = tx.categoryName,
                            date = tx.date,
                            isLoading = false,
                        ).recompute()
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false).recompute() }
                }
            }
        }
    }

    private fun buildInitialState(): FormUiState {
        val savedType = savedStateHandle.get<String>(KEY_TYPE)?.let {
            runCatching { TransactionType.valueOf(it) }.getOrNull()
        } ?: TransactionType.EXPENSE
        val savedAmount = savedStateHandle.get<String>(KEY_AMOUNT) ?: ""
        val savedCurrencyId = savedStateHandle.get<Long>(KEY_CURRENCY_ID)
        val savedCategory = savedStateHandle.get<String>(KEY_CATEGORY) ?: ""
        val savedDate = savedStateHandle.get<Long>(KEY_DATE)?.let(LocalDate::ofEpochDay) ?: LocalDate.now()
        val origLoaded = savedStateHandle.get<Boolean>(KEY_ORIG_LOADED) == true

        if (isNew && savedStateHandle.get<Long>(KEY_ORIG_DATE) == null) {
            savedStateHandle[KEY_ORIG_TYPE] = TransactionType.EXPENSE.name
            savedStateHandle[KEY_ORIG_AMOUNT] = ""
            savedStateHandle[KEY_ORIG_CATEGORY] = ""
            savedStateHandle[KEY_ORIG_DATE] = savedDate.toEpochDay()
        }

        return FormUiState(
            isNew = isNew,
            type = savedType,
            amountText = savedAmount,
            currencyId = savedCurrencyId,
            categoryText = savedCategory,
            date = savedDate,
            isLoading = !isNew && !origLoaded,
        ).recompute()
    }

    private fun resolveInitialCurrencyId(currList: List<CurrencyEntity>): Long? {
        if (currList.isEmpty()) return null
        val preferredId = savedStateHandle.get<Long>(KEY_ORIG_CURRENCY_ID)
        return (currList.firstOrNull { it.id == preferredId }
            ?: currList.firstOrNull { it.isDefault }
            ?: currList.first()).id
    }

    private fun FormUiState.recompute(): FormUiState {
        val rawAmountError = when (val res = AmountValidator.validate(amountText)) {
            is AmountResult.Invalid -> res.messageRes
            is AmountResult.Valid -> null
        }
        val rawCategoryError = if (categoryText.isBlank()) R.string.err_category_required else null
        val selectedCurrency = currencies.firstOrNull { it.id == currencyId }

        val origType = savedStateHandle.get<String>(KEY_ORIG_TYPE)?.let {
            runCatching { TransactionType.valueOf(it) }.getOrNull()
        } ?: TransactionType.EXPENSE
        val origAmount = savedStateHandle.get<String>(KEY_ORIG_AMOUNT) ?: ""
        val origCurrencyId = savedStateHandle.get<Long>(KEY_ORIG_CURRENCY_ID)
        val origCategory = savedStateHandle.get<String>(KEY_ORIG_CATEGORY) ?: ""
        val origDate = savedStateHandle.get<Long>(KEY_ORIG_DATE)?.let(LocalDate::ofEpochDay) ?: LocalDate.now()

        val dirty = if (isNew) {
            amountText.isNotBlank() ||
                categoryText.isNotBlank() ||
                type != origType ||
                date != origDate ||
                (origCurrencyId != null && currencyId != null && currencyId != origCurrencyId)
        } else {
            val loaded = savedStateHandle.get<Boolean>(KEY_ORIG_LOADED) == true
            loaded && (
                type != origType ||
                    amountText != origAmount ||
                    currencyId != origCurrencyId ||
                    categoryText.trim() != origCategory.trim() ||
                    date != origDate
                )
        }

        return copy(
            currency = selectedCurrency,
            amountErrorRes = if (saveAttempted || amountTouched) rawAmountError else null,
            categoryErrorRes = if (saveAttempted || categoryTouched) rawCategoryError else null,
            canSave = !isLoading && rawAmountError == null && rawCategoryError == null,
            isDirty = dirty,
        )
    }

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

    fun save(additionalRates: Map<Long, BigDecimal> = emptyMap()) {
        val current = _uiState.value
        if (current.isSaving || current.isLoading) return

        saveAttempted = true
        val validated = current.recompute()
        _uiState.value = validated

        val currId = validated.currencyId ?: run {
            emitError(R.string.err_currency_required)
            return
        }

        val amountResult = AmountValidator.validate(validated.amountText)
        if (amountResult is AmountResult.Invalid) return
        val validAmount = (amountResult as AmountResult.Valid).value

        if (validated.categoryText.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val input = TransactionInput(
                    id = transactionId,
                    type = validated.type,
                    amount = validAmount,
                    currencyId = currId,
                    categoryName = validated.categoryText,
                    date = validated.date,
                )

                when (val result = transactionRepository.save(input, additionalRates)) {
                    is SaveResult.Saved -> {
                        _events.emit(FormEvent.Saved)
                    }
                    is SaveResult.MissingRates -> {
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
            } catch (_: Exception) {
                _events.emit(FormEvent.Error(R.string.err_amount_invalid))
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
        const val KEY_TYPE = "form_type"
        const val KEY_AMOUNT = "form_amount"
        const val KEY_CURRENCY_ID = "form_currency_id"
        const val KEY_CATEGORY = "form_category"
        const val KEY_DATE = "form_date"
        const val KEY_SAVE_ATTEMPTED = "form_save_attempted"
        const val KEY_AMOUNT_TOUCHED = "form_amount_touched"
        const val KEY_CATEGORY_TOUCHED = "form_category_touched"
        const val KEY_ORIG_LOADED = "form_orig_loaded"
        const val KEY_ORIG_TYPE = "form_orig_type"
        const val KEY_ORIG_AMOUNT = "form_orig_amount"
        const val KEY_ORIG_CURRENCY_ID = "form_orig_currency_id"
        const val KEY_ORIG_CATEGORY = "form_orig_category"
        const val KEY_ORIG_DATE = "form_orig_date"
    }
}
