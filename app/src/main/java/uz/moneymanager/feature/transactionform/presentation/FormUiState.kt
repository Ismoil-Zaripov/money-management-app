package uz.moneymanager.feature.transactionform.presentation

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.domain.model.TransactionType

@Immutable
data class FormUiState(
    val isNew: Boolean = true,
    val type: TransactionType = TransactionType.EXPENSE,
    val amountText: String = "",
    val currencyId: Long? = null,
    val currency: CurrencyEntity? = null,
    val categoryText: String = "",
    val paymentMethodId: Long? = null,
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val currencies: List<CurrencyEntity> = emptyList(),
    val allCategories: List<CategoryEntity> = emptyList(),
    val categorySuggestions: List<String> = emptyList(),
    val paymentMethods: List<PaymentMethodEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val showNoInternetDialog: Boolean = false,
    val amountErrorRes: Int? = null,
    val categoryErrorRes: Int? = null,
    /** Shown only when the trimmed description exceeds the limit; empty is always fine. */
    val descriptionErrorRes: Int? = null,
    val canSave: Boolean = false,
    val isDirty: Boolean = false,
)
