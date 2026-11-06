package uz.moneymanager.feature.transactions.ui.filter.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.feature.transactions.presentation.DateRangePreset

/**
 * M3 date range picker with quick presets. A single picked day reports (day, day).
 * [from]/[to] are the source of truth: changing them from outside (e.g. "Clear all") resets the picker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSection(
    from: LocalDate?,
    to: LocalDate?,
    onRangeChange: (from: LocalDate?, to: LocalDate?) -> Unit,
) {
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = from?.toUtcMillis(),
        initialSelectedEndDateMillis = to?.toUtcMillis(),
        initialDisplayedMonthMillis = (from ?: LocalDate.now()).toUtcMillis(),
    )

    // picker -> caller
    LaunchedEffect(state.selectedStartDateMillis, state.selectedEndDateMillis) {
        val start = state.selectedStartDateMillis?.toUtcLocalDate()
        val end = state.selectedEndDateMillis?.toUtcLocalDate() ?: start
        if (start != from || end != to) onRangeChange(start, end)
    }
    // caller -> picker
    LaunchedEffect(from, to) {
        val start = state.selectedStartDateMillis?.toUtcLocalDate()
        val end = state.selectedEndDateMillis?.toUtcLocalDate() ?: start
        if (start != from || end != to) state.setSelection(from?.toUtcMillis(), to?.toUtcMillis())
    }

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
}

private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toUtcLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
