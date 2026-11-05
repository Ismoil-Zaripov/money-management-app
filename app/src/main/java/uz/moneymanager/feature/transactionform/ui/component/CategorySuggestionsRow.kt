package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun CategorySuggestionsRow(
    suggestions: List<String>,
    selectedText: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (suggestions.isEmpty()) return
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(suggestions, key = { it }) { name ->
            FormFilterChip(
                selected = name.equals(selectedText.trim(), ignoreCase = true),
                onClick = { onSelect(name) },
                label = name
            )
        }
    }
}
