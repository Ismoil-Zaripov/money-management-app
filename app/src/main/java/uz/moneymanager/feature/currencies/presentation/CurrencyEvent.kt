package uz.moneymanager.feature.currencies.presentation



sealed interface CurrencyEvent {
    data object Success : CurrencyEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : CurrencyEvent
}
