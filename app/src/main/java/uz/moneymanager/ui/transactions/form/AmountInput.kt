package uz.moneymanager.ui.transactions.form

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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

/**
 * Ichki "1234567.5" matnini "1 234 567,5" ko'rinishida chizadi (NBSP + Locale kasr ajratgichi).
 * Faqat ko'rinish o'zgaradi, shuning uchun kursor pozitsiyasi OffsetMapping orqali aniq saqlanadi.
 */
internal class AmountVisualTransformation(
    private val decimalSeparator: Char,
    private val groupingSeparator: Char = '\u00A0',
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val dot = raw.indexOf('.')
        val intLength = if (dot >= 0) dot else raw.length

        val out = StringBuilder(raw.length + raw.length / 3 + 1)
        val originalToTransformed = IntArray(raw.length + 1)
        for (i in raw.indices) {
            if (i < intLength && i > 0 && (intLength - i) % 3 == 0) out.append(groupingSeparator)
            originalToTransformed[i] = out.length
            out.append(if (i == dot) decimalSeparator else raw[i])
        }
        originalToTransformed[raw.length] = out.length

        val transformedToOriginal = IntArray(out.length + 1)
        var original = 0
        for (t in 0..out.length) {
            while (original < raw.length && originalToTransformed[original] < t) original++
            transformedToOriginal[t] = original
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                originalToTransformed[offset.coerceIn(0, raw.length)]

            override fun transformedToOriginal(offset: Int): Int =
                transformedToOriginal[offset.coerceIn(0, out.length)]
        }
        return TransformedText(AnnotatedString(out.toString()), mapping)
    }
}
