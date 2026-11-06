package uz.moneymanager.feature.categories.presentation

sealed interface CategoryEvent {
    data object Success : CategoryEvent
    data class Error(val messageRes: Int, val args: List<Any> = emptyList()) : CategoryEvent
}
