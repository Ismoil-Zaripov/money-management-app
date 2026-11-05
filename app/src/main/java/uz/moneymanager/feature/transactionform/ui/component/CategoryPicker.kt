package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.moneymanager.data.local.CategoryEntity

/** Kategoriya: yozish mumkin bo'lgan dropdown + tezkor taklif chip'lari. */
@Composable
internal fun CategoryPicker(
    categoryText: String,
    allCategories: List<CategoryEntity>,
    suggestions: List<String>,
    errorRes: Int?,
    onCategoryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldModifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val filteredCategories = remember(categoryText, allCategories) {
        if (categoryText.isBlank()) allCategories
        else allCategories.filter { it.name.contains(categoryText.trim(), ignoreCase = true) }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CategoryDropdownField(
            text = categoryText,
            categories = filteredCategories,
            menuVisible = menuExpanded && filteredCategories.isNotEmpty(),
            errorRes = errorRes,
            onExpandedChange = { menuExpanded = it },
            onTextChange = {
                onCategoryChange(it)
                menuExpanded = true
            },
            onPick = {
                onCategoryChange(it)
                menuExpanded = false
            },
            onClear = {
                onCategoryChange("")
                menuExpanded = false
            },
            modifier = fieldModifier
        )
        CategorySuggestionsRow(
            suggestions = suggestions,
            selectedText = categoryText,
            onSelect = {
                onCategoryChange(it)
                menuExpanded = false
            }
        )
    }
}
