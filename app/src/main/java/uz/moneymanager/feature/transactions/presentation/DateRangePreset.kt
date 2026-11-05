package uz.moneymanager.feature.transactions.presentation

import androidx.annotation.StringRes
import uz.moneymanager.R
import java.time.LocalDate

/** Quick ranges offered in the date range sheet. Pure date math, unit-testable. */
enum class DateRangePreset(@StringRes val labelRes: Int) {
    ThisMonth(R.string.preset_this_month),
    LastMonth(R.string.preset_last_month),
    Last30Days(R.string.preset_last_30_days),
    ThisYear(R.string.preset_this_year);

    /** Inclusive (from, to) for the given [today]. */
    fun range(today: LocalDate): Pair<LocalDate, LocalDate> = when (this) {
        ThisMonth -> today.withDayOfMonth(1) to today
        LastMonth -> {
            val first = today.minusMonths(1).withDayOfMonth(1)
            first to first.withDayOfMonth(first.lengthOfMonth())
        }
        Last30Days -> today.minusDays(29) to today
        ThisYear -> today.withDayOfYear(1) to today
    }
}
