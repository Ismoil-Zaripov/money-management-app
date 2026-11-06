package uz.moneymanager.feature.paymentmethods.presentation

import androidx.compose.runtime.Immutable
import uz.moneymanager.data.local.PaymentMethodWithUsage

@Immutable
data class PaymentMethodsUiState(
    val paymentMethods: List<PaymentMethodWithUsage> = emptyList(),
)
