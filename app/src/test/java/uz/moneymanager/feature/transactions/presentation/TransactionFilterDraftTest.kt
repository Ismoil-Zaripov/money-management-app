package uz.moneymanager.feature.transactions.presentation

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.model.TransactionType

class TransactionFilterDraftTest {

    private val full = TransactionFilterDraft(
        type = TransactionType.EXPENSE,
        categoryId = 3,
        paymentMethodId = 2,
        currencyId = 5,
        from = LocalDate.of(2026, 11, 1),
        to = LocalDate.of(2026, 11, 30),
    )

    @Test
    fun activeCount_emptyDraftIsZero() {
        assertEquals(0, TransactionFilterDraft().activeCount)
    }

    @Test
    fun activeCount_dateRangeCountsAsOne() {
        assertEquals(5, full.activeCount)
        assertEquals(1, TransactionFilterDraft(from = LocalDate.of(2026, 11, 1)).activeCount)
    }

    @Test
    fun cleared_resetsEverything() {
        assertEquals(TransactionFilterDraft(), full.cleared())
        assertEquals(0, full.cleared().activeCount)
    }

    @Test
    fun toFilter_keepsDisplayCurrency() {
        val base = MonitoringFilter(displayCurrencyId = 7)
        val filter = full.toFilter(base)
        assertEquals(7L, filter.displayCurrencyId)
        assertEquals(TransactionType.EXPENSE, filter.type)
        assertEquals(3L, filter.categoryId)
        assertEquals(2L, filter.paymentMethodId)
        assertEquals(5L, filter.currencyId)
        assertEquals(LocalDate.of(2026, 11, 30), filter.to)
    }

    @Test
    fun clearedDraft_toFilter_clearsFiltersButKeepsDisplayCurrency() {
        val base = full.toFilter(MonitoringFilter(displayCurrencyId = 7))
        assertEquals(MonitoringFilter(displayCurrencyId = 7), full.cleared().toFilter(base))
    }

    @Test
    fun roundTrip_filterToDraftAndBack() {
        val filter = full.toFilter(MonitoringFilter(displayCurrencyId = 7))
        assertEquals(full, TransactionFilterDraft.of(filter))
        assertEquals(filter, TransactionFilterDraft.of(filter).toFilter(filter))
    }
}
