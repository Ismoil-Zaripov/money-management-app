package uz.moneymanager.feature.paymentmethods.presentation

sealed interface PaymentMethodEvent {
    data object Success : PaymentMethodEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : PaymentMethodEvent
}
