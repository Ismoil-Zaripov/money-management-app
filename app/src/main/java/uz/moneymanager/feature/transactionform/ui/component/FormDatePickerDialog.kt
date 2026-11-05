package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import uz.moneymanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FormDatePickerDialog(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val today = LocalDate.now()
    val todayUtcMillis = today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        selectableDates = remember(todayUtcMillis) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayUtcMillis
                override fun isSelectableYear(year: Int) = year <= today.year
            }
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                pickerState.selectedDateMillis?.let {
                    onDateChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                }
                onDismiss()
            }) { Text(stringResource(R.string.action_apply)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    ) {
        DatePicker(state = pickerState)
    }
}
