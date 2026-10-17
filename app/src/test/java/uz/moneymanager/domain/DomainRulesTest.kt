package uz.moneymanager.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.moneymanager.data.local.TotalsRow
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal

class DomainRulesTest {

    @Test
    fun testAmountValidation() {
        assertTrue(AmountValidator.validate("").let { it is AmountResult.Invalid })
        assertTrue(AmountValidator.validate("0").let { it is AmountResult.Invalid })
        assertTrue(AmountValidator.validate("-10").let { it is AmountResult.Invalid })
        assertTrue(AmountValidator.validate("10.555").let { it is AmountResult.Invalid })

        val valid = AmountValidator.validate("123.45")
        assertTrue(valid is AmountResult.Valid)
        assertEquals(BigDecimal("123.45"), (valid as AmountResult.Valid).value)

        // Sanitize check
        assertEquals("123.45", AmountValidator.sanitize("-123.456"))
    }

    @Test
    fun testRateMathInverseAndCross() {
        // 1 USD = 10 EUR -> 1 EUR = 0.1 USD
        val rate = BigDecimal("10")
        val inv = RateMath.inverse(rate)
        assertEquals(BigDecimal("0.1"), inv)

        // Cross rate: 1 USD = 12,500 UZS, 1 EUR = 13,500 UZS
        // 1 USD = 12500 / 13500 EUR
        val cross = RateMath.cross(BigDecimal("12500"), BigDecimal("13500"))
        assertNotNull(cross)
    }

    @Test
    fun testTotalsCalculatorSplit() {
        val rows = listOf(
            TotalsRow(
                type = TransactionType.INCOME,
                amount = BigDecimal("100"),
                displayRate = BigDecimal("1.5"),
                fallbackRate = null
            ),
            TotalsRow(
                type = TransactionType.EXPENSE,
                amount = BigDecimal("50"),
                displayRate = BigDecimal("1.5"),
                fallbackRate = null
            ),
            // Fallback transaction (no display rate, but fallback UZS rate exists)
            TotalsRow(
                type = TransactionType.INCOME,
                amount = BigDecimal("200"),
                displayRate = null,
                fallbackRate = BigDecimal("1000")
            ),
        )

        val totals = TotalsCalculator.calculate(rows)

        // Display totals: Income: 150, Expense: 75, Balance: 75, Count: 2
        assertEquals(0, BigDecimal("150").compareTo(totals.display.income))
        assertEquals(0, BigDecimal("75").compareTo(totals.display.expense))
        assertEquals(0, BigDecimal("75").compareTo(totals.display.balance))
        assertEquals(2, totals.display.count)

        // Fallback totals: Income: 200,000, Expense: 0, Balance: 200,000, Count: 1
        assertEquals(0, BigDecimal("200000").compareTo(totals.fallback.income))
        assertEquals(0, BigDecimal.ZERO.compareTo(totals.fallback.expense))
        assertEquals(1, totals.fallback.count)
    }

    @Test
    fun testAmountInputSanitizeAndTransformation() {
        // Comma and dot normalized, leading zeros stripped, max 2 decimals, max 15 integer digits
        assertEquals("0", uz.moneymanager.ui.transactions.form.AmountInput.sanitize("000"))
        assertEquals("5", uz.moneymanager.ui.transactions.form.AmountInput.sanitize("005"))
        assertEquals("0.25", uz.moneymanager.ui.transactions.form.AmountInput.sanitize(",25"))
        assertEquals("1234.56", uz.moneymanager.ui.transactions.form.AmountInput.sanitize("1,234.567", isPaste = true))
        assertEquals(15, uz.moneymanager.ui.transactions.form.AmountInput.sanitize("1234567890123456789").length)

        val vt = uz.moneymanager.ui.transactions.form.AmountVisualTransformation(decimalSeparator = ',', groupingSeparator = '\u00A0')
        val transformed = vt.filter(androidx.compose.ui.text.AnnotatedString("1234567.89"))
        assertEquals("1\u00A0234\u00A0567,89", transformed.text.text)
        assertEquals(transformed.text.length, transformed.offsetMapping.originalToTransformed(10))
        assertEquals(10, transformed.offsetMapping.transformedToOriginal(transformed.text.length))
    }
}
