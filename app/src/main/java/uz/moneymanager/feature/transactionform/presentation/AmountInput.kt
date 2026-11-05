package uz.moneymanager.feature.transactionform.presentation

import uz.moneymanager.domain.AmountValidator

/** Summa maydoni uchun kiritishni tozalash. Ichki ko'rinish: faqat raqamlar va bitta "." (masalan "1234.5"). */
internal object AmountInput {
    const val MAX_INTEGER_DIGITS = 15

    /**
     * - "." va "," ikkalasi kasr ajratgichi sifatida qabul qilinadi va "." ga normallashadi.
     * - Kasr ko'pi bilan 2 xona, butun qism ko'pi bilan [MAX_INTEGER_DIGITS] xona, bosh nollar olib tashlanadi.
     * - [isPaste] bo'lsa va matnda ikkala ajratgich uchrasa (masalan "1,234.56"), oxirgisi kasr ajratgichi
     *   hisoblanadi, qolganlari mingliklar ajratgichi sifatida tashlab yuboriladi.
     */
    fun sanitize(input: String, isPaste: Boolean = false): String {
        val prepared = if (isPaste && input.contains('.') && input.contains(',')) {
            val decimalAt = maxOf(input.lastIndexOf('.'), input.lastIndexOf(','))
            input.filterIndexed { i, c -> i == decimalAt || (c != '.' && c != ',') }
        } else {
            input
        }
        val cleaned = AmountValidator.sanitize(prepared)
        val dot = cleaned.indexOf('.')
        val rawInt = if (dot >= 0) cleaned.substring(0, dot) else cleaned
        val fraction = if (dot >= 0) cleaned.substring(dot) else ""
        val integer = rawInt.trimStart('0')
            .ifEmpty { if (rawInt.isNotEmpty() || dot >= 0) "0" else "" }
            .take(MAX_INTEGER_DIGITS)
        return integer + fraction
    }
}
