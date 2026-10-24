package uz.moneymanager.domain

import androidx.annotation.StringRes
import uz.moneymanager.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

/** Business-rule violation; [messageRes] is shown to the user. */
class DomainException(@StringRes val messageRes: Int, vararg val args: Any) : Exception()

sealed interface AmountResult {
    data class Valid(val value: BigDecimal) : AmountResult
    data class Invalid(@StringRes val messageRes: Int) : AmountResult
}

object AmountValidator {
    const val MAX_DECIMALS = 2

    fun validate(input: String): AmountResult {
        val normalized = input.trim().replace(" ", "").replace(',', '.')
        if (normalized.isEmpty()) return AmountResult.Invalid(R.string.err_amount_required)
        val value = normalized.toBigDecimalOrNull()
            ?: return AmountResult.Invalid(R.string.err_amount_invalid)
        if (value.signum() <= 0) return AmountResult.Invalid(R.string.err_amount_positive)
        if (value.stripTrailingZeros().scale() > MAX_DECIMALS) {
            return AmountResult.Invalid(R.string.err_amount_decimals)
        }
        return AmountResult.Valid(value.setScale(MAX_DECIMALS, RoundingMode.UNNECESSARY))
    }

    /** Keeps only digits and a single decimal separator; a minus sign can never be typed. */
    fun sanitize(input: String): String = sanitizeDecimal(input, MAX_DECIMALS)
}

object RateValidator {
    const val MAX_DECIMALS = 10

    fun parse(input: String): BigDecimal? {
        val normalized = input.trim().replace(" ", "").replace(',', '.')
        val value = normalized.toBigDecimalOrNull() ?: return null
        if (value.signum() <= 0) return null
        if (value.stripTrailingZeros().scale() > MAX_DECIMALS) return null
        return value
    }

    fun sanitize(input: String): String = sanitizeDecimal(input, MAX_DECIMALS)
}

private fun sanitizeDecimal(input: String, maxDecimals: Int): String {
    val sb = StringBuilder()
    var separatorSeen = false
    var decimals = 0
    for (ch in input) {
        when {
            ch.isDigit() -> {
                if (separatorSeen) {
                    if (decimals >= maxDecimals) continue
                    decimals++
                }
                sb.append(ch)
            }
            (ch == '.' || ch == ',') && !separatorSeen -> {
                separatorSeen = true
                sb.append('.')
            }
        }
    }
    return sb.toString()
}

object CategoryNames {
    const val MAX_LENGTH = 100
    private val spaces = Regex("\\s+")

    fun normalize(name: String): String = name.trim().replace(spaces, " ")

    /** Case-insensitive uniqueness key (works for Latin and Cyrillic). */
    fun key(name: String): String = normalize(name).lowercase(Locale.ROOT)
}

/** Optional transaction description: empty is valid, length is checked after trimming. */
object TransactionDescription {
    const val MAX_LENGTH = 200

    fun normalize(input: String): String = input.trim()

    /** True when the already [normalize]d text fits into [MAX_LENGTH]. */
    fun isValid(normalized: String): Boolean = normalized.length <= MAX_LENGTH
}

object CurrencyCodes {
    private val pattern = Regex("^[A-Z]{3,5}$")
    fun normalize(code: String): String = code.trim().uppercase(Locale.ROOT)
    fun isValid(code: String): Boolean = pattern.matches(code)
}

object RateMath {
    const val SCALE = 10

    fun inverse(rate: BigDecimal): BigDecimal =
        BigDecimal.ONE.divide(rate, SCALE, RoundingMode.HALF_UP).stripTrailingZeros()

    /**
     * Cross rate base→quote from their prices in the national currency (UZS):
     * 1 base = (baseUzs / quoteUzs) quote.
     */
    fun cross(baseUzs: BigDecimal?, quoteUzs: BigDecimal?): BigDecimal? {
        if (baseUzs == null || quoteUzs == null) return null
        if (baseUzs.signum() <= 0 || quoteUzs.signum() <= 0) return null
        return baseUzs.divide(quoteUzs, SCALE, RoundingMode.HALF_UP).stripTrailingZeros()
    }
}
