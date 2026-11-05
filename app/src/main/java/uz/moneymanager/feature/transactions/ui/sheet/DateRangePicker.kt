package uz.moneymanager.feature.transactions.ui.sheet

import android.app.DatePickerDialog
import android.content.Context
import java.time.LocalDate

/**
 * Hozircha tizim DatePickerDialog (2 bosqich: from, keyin to). T04 (B) da DateRangeSheet shu joyni egallaydi.
 */
fun showDateRangePicker(
    context: Context,
    initialFrom: LocalDate?,
    onSelected: (from: LocalDate, to: LocalDate) -> Unit,
) {
    val currentFrom = initialFrom ?: LocalDate.now()
    DatePickerDialog(
        context,
        { _, y1, m1, d1 ->
            val fromDate = LocalDate.of(y1, m1 + 1, d1)
            DatePickerDialog(
                context,
                { _, y2, m2, d2 ->
                    onSelected(fromDate, LocalDate.of(y2, m2 + 1, d2))
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
}
