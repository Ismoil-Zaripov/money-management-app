package uz.moneymanager.core.format

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import uz.moneymanager.domain.model.TransactionType

object FormatUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun formatDate(date: LocalDate): String = date.format(dateFormatter)

    fun formatHeaderDate(
        date: LocalDate,
        locale: Locale = Locale.getDefault(),
        now: LocalDate = LocalDate.now(),
    ): String {
        val pattern = if (date.year == now.year) "d MMMM" else "d MMMM yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return date.format(formatter)
    }

    /** Local time of day (e.g. "10:30 AM"), empty when [epochMillis] is not set. */
    fun formatTime(
        epochMillis: Long,
        locale: Locale = Locale.getDefault(),
        zone: ZoneId = ZoneId.systemDefault(),
    ): String {
        if (epochMillis <= 0L) return ""
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
            .format(Instant.ofEpochMilli(epochMillis).atZone(zone))
    }

    fun formatAmount(
        amount: BigDecimal,
        currencyCode: String,
        type: TransactionType? = null,
        includePositiveSign: Boolean = false,
    ): String {
        val activeLocale = Locale.getDefault()
        val localeSymbols = DecimalFormatSymbols.getInstance(activeLocale)
        val symbols = DecimalFormatSymbols(activeLocale).apply {
            groupingSeparator = '\u00A0'
            decimalSeparator = localeSymbols.decimalSeparator
        }
        val df = DecimalFormat("#,##0.##", symbols)
        val sign = when (type) {
            TransactionType.INCOME -> "+"
            TransactionType.EXPENSE -> "\u2212"
            null -> when {
                amount.signum() < 0 -> "\u2212"
                amount.signum() > 0 && includePositiveSign -> "+"
                else -> ""
            }
        }
        val formattedValue = df.format(amount.abs())
        return "$sign$formattedValue\u00A0$currencyCode"
    }
}
