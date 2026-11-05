package uz.moneymanager.feature.transactions.ui.sheet.summary

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class SummarySharesTest {

    @Test
    fun sharesSumToHundred() {
        val (inc, exp) = summaryShares(BigDecimal("4850"), BigDecimal("2230"))
        assertEquals(BigDecimal("68.5"), inc)
        assertEquals(BigDecimal("31.5"), exp)
        assertEquals(0, BigDecimal("100").compareTo(inc + exp))
    }

    @Test
    fun zeroVolumeGivesZeroShares() {
        val (inc, exp) = summaryShares(BigDecimal.ZERO, BigDecimal.ZERO)
        assertEquals(0, BigDecimal.ZERO.compareTo(inc))
        assertEquals(0, BigDecimal.ZERO.compareTo(exp))
    }

    @Test
    fun onlyIncomeIsHundredPercent() {
        val (inc, exp) = summaryShares(BigDecimal("10"), BigDecimal.ZERO)
        assertEquals(0, BigDecimal("100").compareTo(inc))
        assertEquals(0, BigDecimal.ZERO.compareTo(exp))
    }
}
