package uz.moneymanager.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.moneymanager.data.local.DailyTotalsRow
import uz.moneymanager.data.local.TotalsRow
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.ui.transactions.FormatUtils
import uz.moneymanager.ui.transactions.TransactionDateSeparator
import uz.moneymanager.ui.transactions.TransactionListItemUiModel
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Locale

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

    @Test
    fun testTransactionDateSeparator_groupingRules() {
        val date1 = LocalDate.of(2026, 10, 27)
        val date2 = LocalDate.of(2026, 10, 26)

        val item1 = TransactionListItemUiModel.Item(
            id = 1,
            title = "Ovqat",
            amountText = "−50 000 UZS",
            originalAmountText = null,
            type = TransactionType.EXPENSE,
            date = date1,
        )
        val item2 = TransactionListItemUiModel.Item(
            id = 2,
            title = "Kiyim",
            amountText = "−100 000 UZS",
            originalAmountText = null,
            type = TransactionType.EXPENSE,
            date = date1,
        )
        val item3 = TransactionListItemUiModel.Item(
            id = 3,
            title = "Maosh",
            amountText = "+5 000 000 UZS",
            originalAmountText = null,
            type = TransactionType.INCOME,
            date = date2,
        )

        // 1. Ro'yxat boshi (before == null, after != null) -> sarlavha chiqishi shart
        val header1 = TransactionDateSeparator.createSeparator(null, item1)
        assertNotNull(header1)
        assertEquals(date1, header1?.date)

        // 2. Bir xil sana (before.date == after.date) -> sarlavha chiqmasligi kerak (null)
        val header2 = TransactionDateSeparator.createSeparator(item1, item2)
        assertNull(header2)

        // 3. Yangi sana boshlanganda (before.date != after.date) -> yangi sarlavha chiqishi shart
        val header3 = TransactionDateSeparator.createSeparator(item2, item3)
        assertNotNull(header3)
        assertEquals(date2, header3?.date)

        // 4. Ro'yxat oxiri (after == null) -> ortiqcha sarlavha chiqmasligi kerak (null)
        val headerEnd = TransactionDateSeparator.createSeparator(item3, null)
        assertNull(headerEnd)
    }

    @Test
    fun testDailyTotalsCalculator_multiDateMultiRate() {
        val date1 = LocalDate.of(2026, 10, 27)
        val date2 = LocalDate.of(2026, 10, 20)

        val rows = listOf(
            // Sana 1: Display kursi bor (12500)
            DailyTotalsRow(
                date = date1,
                type = TransactionType.INCOME,
                amount = BigDecimal("100"),
                displayRate = BigDecimal("12500"),
                fallbackRate = BigDecimal("12500"),
            ),
            DailyTotalsRow(
                date = date1,
                type = TransactionType.EXPENSE,
                amount = BigDecimal("30"),
                displayRate = BigDecimal("12500"),
                fallbackRate = BigDecimal("12500"),
            ),
            // Sana 2: Display kursi yo'q, fallback kursi bor (13500)
            DailyTotalsRow(
                date = date2,
                type = TransactionType.INCOME,
                amount = BigDecimal("50"),
                displayRate = null,
                fallbackRate = BigDecimal("13500"),
            ),
        )

        val totalsMap = DailyTotalsCalculator.calculate(rows)

        // Sana 1 tekshiruvi: display.income = 1,250,000, display.expense = 375,000, display.balance = 875,000
        val day1Totals = totalsMap[date1]
        assertNotNull(day1Totals)
        assertEquals(0, BigDecimal("1250000").compareTo(day1Totals!!.display.income))
        assertEquals(0, BigDecimal("375000").compareTo(day1Totals.display.expense))
        assertEquals(0, BigDecimal("875000").compareTo(day1Totals.display.balance))
        assertEquals(2, day1Totals.display.count)
        assertEquals(0, day1Totals.fallback.count)

        // Sana 2 tekshiruvi: fallback.income = 675,000, fallback.balance = 675,000
        val day2Totals = totalsMap[date2]
        assertNotNull(day2Totals)
        assertEquals(0, day2Totals!!.display.count)
        assertEquals(0, BigDecimal("675000").compareTo(day2Totals.fallback.income))
        assertEquals(0, BigDecimal("675000").compareTo(day2Totals.fallback.balance))
        assertEquals(1, day2Totals.fallback.count)
    }

    @Test
    fun testFormatHeaderDate_currentYearWithoutYear_otherYearWithYear() {
        val now = LocalDate.of(2026, 10, 27)

        val sameYear = LocalDate.of(2026, 5, 1)
        val formattedSame = FormatUtils.formatHeaderDate(sameYear, Locale.ENGLISH, now)
        assertEquals("1 May", formattedSame)

        val otherYear = LocalDate.of(2025, 10, 27)
        val formattedOther = FormatUtils.formatHeaderDate(otherYear, Locale.ENGLISH, now)
        assertEquals("27 October 2025", formattedOther)
    }
}
