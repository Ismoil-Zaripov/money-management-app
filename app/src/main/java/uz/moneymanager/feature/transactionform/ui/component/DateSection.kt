package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
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
    val dateLabel = stringResource(R.string.label_date)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Butun maydon bosiladigan: readOnly field ustida shaffof clickable qatlam
        Box {
            OutlinedTextField(
                value = FormatUtils.formatDate(date),
                onValueChange = {},
                readOnly = true,
                label = { Text(dateLabel) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = dateLabel
                    )
                },
                singleLine = true,
                shape = FormFieldShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 8.dp)
                    .heightIn(min = 48.dp)
                    .clickable(role = Role.Button, onClickLabel = dateLabel) { showPicker = true }
            )
        }
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
