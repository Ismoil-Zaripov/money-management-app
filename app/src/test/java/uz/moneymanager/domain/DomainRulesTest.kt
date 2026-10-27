package uz.moneymanager.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    @Test
    fun description_empty_isValidAndStaysEmpty() {
        val normalized = TransactionDescription.normalize("")
        assertEquals("", normalized)
        assertTrue(TransactionDescription.isValid(normalized))
    }

    @Test
    fun description_onlyWhitespace_becomesEmptyAndIsValid() {
        val normalized = TransactionDescription.normalize("   \t\n  ")
        assertEquals("", normalized)
        assertTrue(TransactionDescription.isValid(normalized))
    }

    @Test
    fun description_normalText_isTrimmedAndValid() {
        val normalized = TransactionDescription.normalize("  Bozordan meva  ")
        assertEquals("Bozordan meva", normalized)
        assertTrue(TransactionDescription.isValid(normalized))
    }

    @Test
    fun description_exactlyMaxLength_isValid() {
        val text = "a".repeat(TransactionDescription.MAX_LENGTH)
        assertTrue(TransactionDescription.isValid(TransactionDescription.normalize(text)))
    }

    @Test
    fun description_maxLengthWithSurroundingSpaces_isValidAfterTrim() {
        val text = "  " + "a".repeat(TransactionDescription.MAX_LENGTH) + "  "
        val normalized = TransactionDescription.normalize(text)
        assertEquals(TransactionDescription.MAX_LENGTH, normalized.length)
        assertTrue(TransactionDescription.isValid(normalized))
    }

    @Test
    fun description_overMaxLength_isInvalid() {
        val text = "a".repeat(TransactionDescription.MAX_LENGTH + 1)
        assertFalse(TransactionDescription.isValid(TransactionDescription.normalize(text)))
    }

    @Test
    fun testTransactionAmountCalculator_sameCurrency() {
        // Asl valyutasi display currency bilan bir xil (snapshot rate = 1)
        val result = TransactionAmountCalculator.calculate(
            amount = BigDecimal("100"),
            currencyCode = "USD",
            displayCurrencyCode = "USD",
            fallbackCurrencyCode = "UZS",
            displayRate = BigDecimal.ONE,
            fallbackRate = BigDecimal("12500"),
        )
        assertEquals(0, BigDecimal("100").compareTo(result.amount))
        assertEquals("USD", result.currencyCode)
        assertNull(result.originalAmount)
        assertNull(result.originalCurrencyCode)
    }

    @Test
    fun testTransactionAmountCalculator_displayRatePresent() {
        // Display kursi mavjud bo'lganda (masalan USD -> UZS, rate = 12,500)
        val result = TransactionAmountCalculator.calculate(
            amount = BigDecimal("50"),
            currencyCode = "USD",
            displayCurrencyCode = "UZS",
            fallbackCurrencyCode = "UZS",
            displayRate = BigDecimal("12500"),
            fallbackRate = BigDecimal("12500"),
        )
        assertEquals(0, BigDecimal("625000").compareTo(result.amount))
        assertEquals("UZS", result.currencyCode)
        assertEquals(0, BigDecimal("50").compareTo(result.originalAmount))
        assertEquals("USD", result.originalCurrencyCode)
    }

    @Test
    fun testTransactionAmountCalculator_onlyFallbackRate() {
        // Display kursi yo'q (masalan KZT valyutasi o'sha sanada mavjud bo'lmagan), fallback kursi bor
        val result = TransactionAmountCalculator.calculate(
            amount = BigDecimal("20"),
            currencyCode = "USD",
            displayCurrencyCode = "KZT",
            fallbackCurrencyCode = "UZS",
            displayRate = null,
            fallbackRate = BigDecimal("12000"),
        )
        assertEquals(0, BigDecimal("240000").compareTo(result.amount))
        assertEquals("UZS", result.currencyCode)
        assertEquals(0, BigDecimal("20").compareTo(result.originalAmount))
        assertEquals("USD", result.originalCurrencyCode)
    }

    @Test
    fun testTransactionAmountCalculator_noRatesAtAll() {
        // Ikkala kurs ham yo'q holat
        val result = TransactionAmountCalculator.calculate(
            amount = BigDecimal("75"),
            currencyCode = "EUR",
            displayCurrencyCode = "KZT",
            fallbackCurrencyCode = "UZS",
            displayRate = null,
            fallbackRate = null,
        )
        assertEquals(0, BigDecimal("75").compareTo(result.amount))
        assertEquals("EUR", result.currencyCode)
        assertNull(result.originalAmount)
        assertNull(result.originalCurrencyCode)
    }

    @Test
    fun testTransactionAmountCalculator_differentDatesDifferentSnapshotRates() {
        // Turli sanadagi turli snapshot kurslar:
        // Sana 1: 1 USD = 12,000 UZS
        val txDate1 = TransactionAmountCalculator.calculate(
            amount = BigDecimal("10"),
            currencyCode = "USD",
            displayCurrencyCode = "UZS",
            fallbackCurrencyCode = "UZS",
            displayRate = BigDecimal("12000"),
            fallbackRate = BigDecimal("12000"),
        )
        // Sana 2: 1 USD = 12,800 UZS
        val txDate2 = TransactionAmountCalculator.calculate(
            amount = BigDecimal("10"),
            currencyCode = "USD",
            displayCurrencyCode = "UZS",
            fallbackCurrencyCode = "UZS",
            displayRate = BigDecimal("12800"),
            fallbackRate = BigDecimal("12800"),
        )

        assertEquals(0, BigDecimal("120000").compareTo(txDate1.amount))
        assertEquals(0, BigDecimal("128000").compareTo(txDate2.amount))
        assertEquals("UZS", txDate1.currencyCode)
        assertEquals("UZS", txDate2.currencyCode)
        assertEquals("USD", txDate1.originalCurrencyCode)
        assertEquals("USD", txDate2.originalCurrencyCode)
    }

    @Test
    fun testOriginalAmountFormatting_noPlusMinusSign() {
        // Asl summa formatlanganda + yoki - belgisi bo'lmasligi, faqat raqam va valyuta bo'lishi
        val formatted = uz.moneymanager.ui.transactions.FormatUtils.formatAmount(
            amount = BigDecimal("100"),
            currencyCode = "USD",
            type = null,
            includePositiveSign = false,
        )
        assertFalse(formatted.contains("+"))
        assertFalse(formatted.contains("\u2212"))
        assertFalse(formatted.contains("-"))
        assertTrue(formatted.contains("100"))
        assertTrue(formatted.contains("USD"))
    }
}
