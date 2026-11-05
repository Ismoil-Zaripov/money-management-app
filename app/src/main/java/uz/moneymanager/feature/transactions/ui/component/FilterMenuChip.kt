package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uz.moneymanager.core.designsystem.theme.contentText

@Composable
internal fun transactionChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColor = MaterialTheme.colorScheme.surface,
    labelColor = MaterialTheme.colorScheme.onSurface,
)

/** Chip + uning dropdown menyusi. [menu] ga menyuni yopuvchi `dismiss` beriladi. */
@Composable
internal fun FilterMenuChip(
    label: String,
    selected: Boolean,
    menu: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilterChip(
            selected = selected,
            onClick = { expanded = true },
            label = { Text(text = label, style = MaterialTheme.contentText) },
            colors = transactionChipColors()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menu { expanded = false }
        }
    }
}

@Composable
internal fun FilterMenuItem(text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text, style = MaterialTheme.contentText) },
        onClick = onClick
    )
}
