package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import uz.moneymanager.R
import uz.moneymanager.feature.transactions.ui.filter.section.DateRangeSection

/** Date range picker sheet with Cancel / Save. Single-day ranges are allowed (end defaults to start). */
@Composable
fun DateRangeSheet(
    from: LocalDate?,
    to: LocalDate?,
    onApply: (from: LocalDate, to: LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    var pendingFrom by remember { mutableStateOf(from) }
    var pendingTo by remember { mutableStateOf(to) }

    FilterSheetContainer(title = stringResource(R.string.filter_date_range), onDismiss = onDismiss) {
        DateRangeSection(
            from = pendingFrom,
            to = pendingTo,
            onRangeChange = { f, t ->
                pendingFrom = f
                pendingTo = t
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                enabled = pendingFrom != null,
                onClick = {
                    val start = pendingFrom ?: return@Button
                    onApply(start, pendingTo ?: start)
                }
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}
