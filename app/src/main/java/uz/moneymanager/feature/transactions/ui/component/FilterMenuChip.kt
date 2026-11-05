package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText

/** Chip + uning dropdown menyusi. [menu] ga menyuni yopuvchi `dismiss` beriladi. */
@Composable
internal fun FilterMenuChip(
    label: String,
    selected: Boolean,
    onClear: (() -> Unit)? = null,
    menu: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilterChip(
            selected = selected,
            onClick = { expanded = true },
            label = { Text(text = label, style = MaterialTheme.contentText) },
            trailingIcon = if (selected && onClear != null) {
                {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_cancel),
                        modifier = Modifier.size(18.dp).clickable(onClick = onClear)
                    )
                }
            } else {
                null
            }
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
