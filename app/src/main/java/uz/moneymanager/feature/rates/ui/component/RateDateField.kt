package uz.moneymanager.feature.rates.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.feature.rates.ui.dialog.RateDatePickerDialog
import java.time.LocalDate

@Composable
fun RateDateField(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }
    val dateLabel = stringResource(R.string.label_date)

    Box {
        OutlinedTextField(
            value = FormatUtils.formatDate(date),
            onValueChange = {},
            readOnly = true,
            label = { Text(dateLabel) },
            trailingIcon = {
                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = dateLabel)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp)
                .heightIn(min = 48.dp)
                .clickable(role = Role.Button, onClickLabel = dateLabel) { showPicker = true }
        )
    }

    if (showPicker) {
        RateDatePickerDialog(
            initialDate = date,
            onDateSelected = {
                onDateChange(it)
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }
}
