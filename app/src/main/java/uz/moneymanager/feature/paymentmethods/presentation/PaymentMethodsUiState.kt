package uz.moneymanager.feature.paymentmethods.presentation

import androidx.compose.runtime.Immutable
import uz.moneymanager.data.local.PaymentMethodWithUsage

@Immutable
data class PaymentMethodsUiState(
    val paymentMethods: List<PaymentMethodWithUsage> = emptyList(),
    /** False until the first emission from the data layer; the screen shows its skeleton meanwhile. */
    val loaded: Boolean = false,
)
