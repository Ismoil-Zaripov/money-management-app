package uz.moneymanager.feature.transactionform.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import uz.moneymanager.domain.model.TransactionType

/** Forma ekranining barcha harakatlari: Screen ViewModel'ni bilmaydi, faqat shu lambdalarni chaqiradi. */
@Immutable
internal class TransactionFormCallbacks(
    val onBack: () -> Unit,
    val onSave: () -> Unit,
    val onDeleteClick: () -> Unit,
    val onTypeChange: (TransactionType) -> Unit,
    val onAmountChange: (String) -> Unit,
    val onAmountBlur: () -> Unit,
    val onCurrencySelect: (Long) -> Unit,
    val onCategoryChange: (String) -> Unit,
    val onCategoryBlur: () -> Unit,
    val onPaymentMethodSelect: (Long) -> Unit,
    val onAddPaymentMethod: () -> Unit,
    val onDateChange: (LocalDate) -> Unit,
    val onDescriptionChange: (String) -> Unit,
)
