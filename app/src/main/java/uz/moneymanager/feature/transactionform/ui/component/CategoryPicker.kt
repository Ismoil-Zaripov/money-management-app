package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.domain.CategoryNames

/** Kategoriya: yozish mumkin bo'lgan maydon (dropdown) + takliflar kartasi + "Create" qatori. */
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
    val focusManager = LocalFocusManager.current
    var menuExpanded by remember { mutableStateOf(false) }
    val filteredCategories = remember(categoryText, allCategories) {
        if (categoryText.isBlank()) allCategories
        else allCategories.filter { it.name.contains(categoryText.trim(), ignoreCase = true) }
    }
    // Yozilgan nom mavjud emas bo'lsa, saqlashda yangi kategoriya yaratiladi (avvalgi xulq)
    val createName = remember(categoryText, allCategories) {
        val key = CategoryNames.key(categoryText)
        if (key.isEmpty() || allCategories.any { it.nameKey == key }) null else CategoryNames.normalize(categoryText)
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FormSectionLabel(title = stringResource(R.string.label_info))
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
        CategorySuggestionsCard(
            suggestions = suggestions,
            selectedText = categoryText,
            createName = createName,
            onSelect = {
                onCategoryChange(it)
                menuExpanded = false
            },
            onCreate = {
                menuExpanded = false
                focusManager.clearFocus()
            }
        )
    }
}
