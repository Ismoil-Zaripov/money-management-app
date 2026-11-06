package uz.moneymanager.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import uz.moneymanager.R

/** Filter chip; when [selected] and [onClear] is set it shows a clear (x) icon. */
@Composable
fun MmFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = { Text(label) },
        trailingIcon = if (selected && onClear != null) {
            {
                // 48dp touch target around the 18dp icon (accessibility minimum).
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(role = Role.Button, onClick = onClear),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_cancel),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        } else {
            null
        },
    )
}

@MmPreviews
@Composable
private fun MmFilterChipPreview() = MmPreviewHost {
    Row {
        MmFilterChip("Nov 2026", selected = true, onClick = {}, onClear = {})
        MmFilterChip("Category", selected = false, onClick = {})
    }
}
