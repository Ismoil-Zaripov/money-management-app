package uz.moneymanager.feature.categories.presentation

import androidx.compose.runtime.Immutable
import uz.moneymanager.data.local.CategoryWithUsage

@Immutable
data class CategoriesUiState(
    val categories: List<CategoryWithUsage> = emptyList(),
    /** False until the first emission from the data layer; the screen shows its skeleton meanwhile. */
    val loaded: Boolean = false,
)
