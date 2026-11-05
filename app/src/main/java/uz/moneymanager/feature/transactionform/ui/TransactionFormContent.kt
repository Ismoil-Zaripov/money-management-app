package uz.moneymanager.feature.transactionform.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.feature.transactionform.presentation.FormUiState
import uz.moneymanager.feature.transactionform.ui.component.AmountCard
import uz.moneymanager.feature.transactionform.ui.component.CategoryPicker
import uz.moneymanager.feature.transactionform.ui.component.DateSection
import uz.moneymanager.feature.transactionform.ui.component.DescriptionField
import uz.moneymanager.feature.transactionform.ui.component.PaymentMethodChips
import uz.moneymanager.feature.transactionform.ui.component.TransactionTypeSelector

@Composable
internal fun TransactionFormContent(
    state: FormUiState,
    callbacks: TransactionFormCallbacks,
    amountModifier: Modifier,
    categoryModifier: Modifier,
    onAmountNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typeColor = if (state.type == TransactionType.INCOME) IncomeColor else ExpenseColor

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TransactionTypeSelector(type = state.type, onSelect = callbacks.onTypeChange)

        // Summa + biriktirilgan valyuta tanlagichi (faqat bitta joyda)
        AmountCard(
            amount = state.amountText,
            onAmountChange = callbacks.onAmountChange,
            currencyCode = state.currency?.code.orEmpty(),
            currencies = state.currencies,
            onSelectCurrency = callbacks.onCurrencySelect,
            color = typeColor,
            errorText = state.amountErrorRes?.let { stringResource(it) },
            modifier = amountModifier,
            onNext = onAmountNext
        )

        CategoryPicker(
            categoryText = state.categoryText,
            allCategories = state.allCategories,
            suggestions = state.categorySuggestions,
            errorRes = state.categoryErrorRes,
            onCategoryChange = callbacks.onCategoryChange,
            fieldModifier = categoryModifier
        )

        PaymentMethodChips(
            paymentMethods = state.paymentMethods,
            selectedId = state.paymentMethodId,
            onSelect = callbacks.onPaymentMethodSelect,
            onAddClick = callbacks.onAddPaymentMethod
        )

        DateSection(date = state.date, onDateChange = callbacks.onDateChange)

        DescriptionField(
            description = state.description,
            errorRes = state.descriptionErrorRes,
            onChange = callbacks.onDescriptionChange
        )
    }
}
