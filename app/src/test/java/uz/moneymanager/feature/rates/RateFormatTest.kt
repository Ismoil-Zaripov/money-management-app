package uz.moneymanager.feature.rates

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uz.moneymanager.domain.RateMath
import uz.moneymanager.feature.rates.ui.component.formatRate
import java.math.BigDecimal
import java.util.Locale

class RateFormatTest {

    private lateinit var previous: Locale

    @Before
    fun setUp() {
        previous = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() = Locale.setDefault(previous)

    @Test
    fun wholeRate_dropsTrailingZeros_andGroups() {
        assertEquals("12,650", formatRate(BigDecimal("12650.00")))
    }

    @Test
    fun fractionalRate_keepsUpToFourDecimals() {
        assertEquals("135.5", formatRate(BigDecimal("135.50")))
        assertEquals("26.1042", formatRate(BigDecimal("26.1042")))
    }

    @Test
    fun smallRate_usesSixDecimals() {
        assertEquals("0.000079", formatRate(BigDecimal("0.0000790000")))
    }

    @Test
    fun reverseLine_isInverseOfRate() {
        val reverse = RateMath.inverse(BigDecimal("12650"))
        assertEquals("0.000079", formatRate(reverse))
    }
}
