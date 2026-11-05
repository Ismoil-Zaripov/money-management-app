package uz.moneymanager.feature.transactions.presentation

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateRangePresetTest {

    private val today = LocalDate.of(2026, 11, 15)

    @Test
    fun thisMonth_startsOnFirstAndEndsToday() {
        assertEquals(LocalDate.of(2026, 11, 1) to today, DateRangePreset.ThisMonth.range(today))
    }

    @Test
    fun lastMonth_coversWholePreviousMonth() {
        assertEquals(
            LocalDate.of(2026, 10, 1) to LocalDate.of(2026, 10, 31),
            DateRangePreset.LastMonth.range(today)
        )
    }

    @Test
    fun lastMonth_inJanuary_goesToDecemberOfPreviousYear() {
        assertEquals(
            LocalDate.of(2025, 12, 1) to LocalDate.of(2025, 12, 31),
            DateRangePreset.LastMonth.range(LocalDate.of(2026, 1, 20))
        )
    }

    @Test
    fun lastMonth_handlesLeapFebruary() {
        assertEquals(
            LocalDate.of(2024, 2, 1) to LocalDate.of(2024, 2, 29),
            DateRangePreset.LastMonth.range(LocalDate.of(2024, 3, 10))
        )
    }

    @Test
    fun last30Days_is30DaysInclusive() {
        assertEquals(LocalDate.of(2026, 10, 17) to today, DateRangePreset.Last30Days.range(today))
    }

    @Test
    fun thisYear_startsOnJanuaryFirst() {
        assertEquals(LocalDate.of(2026, 1, 1) to today, DateRangePreset.ThisYear.range(today))
    }
}
