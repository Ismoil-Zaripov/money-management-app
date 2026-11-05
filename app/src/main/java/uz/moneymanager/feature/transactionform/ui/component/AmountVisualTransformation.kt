package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

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
