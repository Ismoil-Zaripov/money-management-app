package uz.moneymanager.feature.transactionform.ui

import uz.moneymanager.domain.CategoryNames
import uz.moneymanager.feature.transactionform.ui.component.TransactionInfoCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
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
        if (!state.isNew) {
            TransactionInfoCard(
                id = state.transactionId,
                createdAt = state.createdAt,
                onDelete = callbacks.onDeleteClick
            )
        }

        TransactionTypeSelector(type = state.type, onSelect = callbacks.onTypeChange)

        // Summa + valyuta chip'lari (faqat bitta joyda)
        AmountCard(
            label = stringResource(if (state.type == TransactionType.INCOME) R.string.form_amount_income else R.string.form_amount_expense),
            amount = state.amountText,
            onAmountChange = callbacks.onAmountChange,
            currencies = state.currencies,
            selectedCurrencyId = state.currencyId,
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
            fieldModifier = categoryModifier,
            linkedCount = if (state.isNew) null else state.categoryUsage[CategoryNames.key(state.categoryText)]?.takeIf { it > 0 }
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
