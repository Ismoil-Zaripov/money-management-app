package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uz.moneymanager.core.designsystem.theme.contentText

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
            label = { Text(text = label, style = MaterialTheme.contentText) }
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
