package uz.moneymanager.feature.transactions.ui.component

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText

@Composable
internal fun DateRangeFilterChip(
    from: LocalDate?,
    to: LocalDate?,
    onDateRangeSelect: (LocalDate?, LocalDate?) -> Unit,
) {
    val context = LocalContext.current
    val dateLabel = if (from != null || to != null) {
        "${from?.toString() ?: "..."} - ${to?.toString() ?: "..."}"
    } else {
        stringResource(R.string.filter_date_range)
    }

    FilterChip(
        selected = from != null || to != null,
        onClick = {
            val now = LocalDate.now()
            val currentFrom = from ?: now
            DatePickerDialog(
                context,
                { _, y1, m1, d1 ->
                    val fromDate = LocalDate.of(y1, m1 + 1, d1)
                    DatePickerDialog(
                        context,
                        { _, y2, m2, d2 ->
                            val toDate = LocalDate.of(y2, m2 + 1, d2)
                            onDateRangeSelect(fromDate, toDate)
                        },
                        fromDate.year,
                        fromDate.monthValue - 1,
                        fromDate.dayOfMonth
                    ).show()
                },
                currentFrom.year,
                currentFrom.monthValue - 1,
                currentFrom.dayOfMonth
            ).show()
        },
        label = { Text(text = dateLabel, style = MaterialTheme.contentText) },
        trailingIcon = {
            if (from != null || to != null) {
                IconButton(
                    onClick = { onDateRangeSelect(null, null) },
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                }
            }
        }
    )
}
