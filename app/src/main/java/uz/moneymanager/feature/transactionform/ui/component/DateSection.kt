package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import uz.moneymanager.R
import uz.moneymanager.core.format.FormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DateSection(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FormSectionLabel(
            title = stringResource(R.string.label_transaction_date),
            trailing = stringResource(R.string.date_future_locked)
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FormFilterChip(
                selected = date == today,
                onClick = { onDateChange(today) },
                label = stringResource(R.string.date_today)
            )
            FormFilterChip(
                selected = date == yesterday,
                onClick = { onDateChange(yesterday) },
                label = stringResource(R.string.date_yesterday)
            )
            FormFilterChip(
                selected = date != today && date != yesterday,
                onClick = { showPicker = true },
                label = FormatUtils.formatDate(date),
                leadingIcon = Icons.Default.CalendarToday
            )
        }
    }

    if (showPicker) {
        FormDatePickerDialog(
            date = date,
            onDateChange = onDateChange,
            onDismiss = { showPicker = false }
        )
    }
}
