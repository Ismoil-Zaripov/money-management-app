package uz.moneymanager.feature.rates.presentation

sealed interface RateEvent {
    data class CbuResult(val added: Int, val existing: Int, val notFound: List<String>) : RateEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : RateEvent
    data object Success : RateEvent
}
