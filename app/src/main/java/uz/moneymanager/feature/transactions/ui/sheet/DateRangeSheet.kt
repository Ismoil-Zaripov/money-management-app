package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.feature.transactions.presentation.DateRangePreset

/** M3 date range picker with quick presets. Single-day ranges are allowed (end defaults to start). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSheet(
    from: LocalDate?,
    to: LocalDate?,
    onApply: (from: LocalDate, to: LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = from?.toUtcMillis(),
        initialSelectedEndDateMillis = to?.toUtcMillis(),
        initialDisplayedMonthMillis = (from ?: LocalDate.now()).toUtcMillis(),
    )
    val startMillis = state.selectedStartDateMillis

    FilterSheetContainer(title = stringResource(R.string.filter_date_range), onDismiss = onDismiss) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(DateRangePreset.entries.toList(), key = { it.name }) { preset ->
                AssistChip(
                    onClick = {
                        val (f, t) = preset.range(LocalDate.now())
                        state.setSelection(f.toUtcMillis(), t.toUtcMillis())
                    },
                    label = { Text(stringResource(preset.labelRes), style = MaterialTheme.contentText) }
                )
            }
        }
        DateRangePicker(
            state = state,
            modifier = Modifier.height(460.dp),
            showModeToggle = false
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                enabled = startMillis != null,
                onClick = {
                    val start = startMillis ?: return@Button
                    val end = state.selectedEndDateMillis ?: start
                    onApply(start.toUtcLocalDate(), end.toUtcLocalDate())
                }
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toUtcLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
