package uz.moneymanager.feature.transactions.ui.filter.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.feature.transactions.presentation.DateRangePreset

/**
 * Range headline, quick presets and an M3 date range picker. A single picked day reports (day, day).
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

    val activePreset = remember(from, to) {
        val today = LocalDate.now()
        DateRangePreset.entries.firstOrNull { it.range(today) == Pair(from, to) }
    }

    DateRangeHeadline(from = from, to = to)
    Spacer(modifier = Modifier.height(12.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(DateRangePreset.entries.toList(), key = { it.name }) { preset ->
            FilterChip(
                selected = preset == activePreset,
                onClick = {
                    val (f, t) = preset.range(LocalDate.now())
                    state.setSelection(f.toUtcMillis(), t.toUtcMillis())
                },
                label = { Text(stringResource(preset.labelRes), style = MaterialTheme.contentText) }
            )
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
    FilterCalendarTheme {
        DateRangePicker(
            state = state,
            modifier = Modifier.height(420.dp),
            title = null,
            headline = null,
            showModeToggle = false,
            colors = filterDatePickerColors(),
        )
    }
}

private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toUtcLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
