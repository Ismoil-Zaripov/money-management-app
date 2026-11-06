package uz.moneymanager.feature.transactions.ui.filter.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.icon.categoryIcon
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.CategoryEntity

/** Single-choice category list with search; the picked row is tinted, tapping it again clears it. */
@Composable
fun CategorySection(
    categories: List<CategoryEntity>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val visible = remember(categories, query) {
        categories.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }
    val fieldColor = MaterialTheme.colorScheme.surfaceContainerHighest
    TextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        textStyle = MaterialTheme.contentText,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        placeholder = { Text(stringResource(R.string.sheet_search_categories), style = MaterialTheme.contentText) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = fieldColor,
            unfocusedContainerColor = fieldColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
    if (visible.isEmpty()) {
        Text(
            text = stringResource(R.string.sheet_no_results),
            style = MaterialTheme.contentText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.heightIn(max = 280.dp)) {
            items(visible, key = { it.id }) { category ->
                CategoryRow(
                    category = category,
                    selected = selectedId == category.id,
                    onClick = { onSelect(if (selectedId == category.id) null else category.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(category: CategoryEntity, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val icon = remember(category.nameKey) { categoryIcon(category.nameKey) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(if (selected) colors.primaryContainer else Color.Transparent)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (selected) colors.primary else colors.surfaceContainerHighest, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) colors.onPrimary else colors.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = category.name,
            style = MaterialTheme.contentText.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal),
            color = if (selected) colors.onPrimaryContainer else colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = colors.primary)
        }
    }
}
