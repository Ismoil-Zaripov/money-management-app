package uz.moneymanager.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import uz.moneymanager.R
import uz.moneymanager.data.local.AppDatabase
import uz.moneymanager.data.local.CurrencyDao
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.ExchangeRateDao
import uz.moneymanager.data.local.ExchangeRateEntity
import uz.moneymanager.data.local.ExchangeRateRow
import uz.moneymanager.data.remote.CbuApi
import uz.moneymanager.domain.DomainException
import uz.moneymanager.domain.RateMath
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class CbuImportResult(val added: Int, val existing: Int, val notFound: List<String>)

@Singleton
class ExchangeRateRepository @Inject constructor(
    private val db: AppDatabase,
    private val rateDao: ExchangeRateDao,
    private val currencyDao: CurrencyDao,
    private val api: CbuApi,
) {
    fun observeForDate(date: LocalDate): Flow<List<ExchangeRateRow>> = rateDao.observeForDate(date)

    /** Currencies for which `1 base = ? quote` is not stored on [date]. */
    suspend fun missingRates(baseId: Long, date: LocalDate): List<CurrencyEntity> {
        val existing = rateDao.getForBase(date, baseId).map { it.quoteCurrencyId }.toSet()
        return currencyDao.getAll().filter { it.id != baseId && it.id !in existing }
    }

    suspend fun ratesForBase(baseId: Long, date: LocalDate): List<ExchangeRateEntity> =
        rateDao.getForBase(date, baseId)

    /** CBU prices of 1 unit of each currency in UZS, keyed by ISO code. */
    private suspend fun fetchUzsPrices(date: LocalDate): Map<String, BigDecimal> =
        api.ratesForDate(date.toString()).mapNotNull { dto ->
            val rate = dto.rate.toBigDecimalOrNull() ?: return@mapNotNull null
            val nominal = dto.nominal.toBigDecimalOrNull()?.takeIf { it.signum() > 0 } ?: BigDecimal.ONE
            dto.ccy.uppercase() to rate.divide(nominal, RateMath.SCALE, RoundingMode.HALF_UP)
        }.toMap()

    private fun uzsPrice(currency: CurrencyEntity, prices: Map<String, BigDecimal>): BigDecimal? =
        if (currency.isDefault) BigDecimal.ONE else prices[currency.code]

    /** Suggested `1 base = x quote` for each quote, computed from CBU. Missing entries = unknown to CBU. */
    suspend fun suggestFromCbu(
        base: CurrencyEntity,
        quotes: List<CurrencyEntity>,
        date: LocalDate,
    ): Map<Long, BigDecimal> {
        val prices = fetchUzsPrices(date)
        val basePrice = uzsPrice(base, prices)
        return buildMap {
            quotes.forEach { q -> RateMath.cross(basePrice, uzsPrice(q, prices))?.let { put(q.id, it) } }
        }
    }

    suspend fun suggestPairFromCbu(baseId: Long, quoteId: Long, date: LocalDate): BigDecimal? {
        val base = currencyDao.getById(baseId) ?: return null
        val quote = currencyDao.getById(quoteId) ?: return null
        return suggestFromCbu(base, listOf(quote), date)[quoteId]
    }

    /** Fills every missing pair for [date] from CBU. Existing pairs are never overwritten. */
    suspend fun importFromCbu(date: LocalDate): CbuImportResult {
        ensureNotFuture(date)
        val prices = fetchUzsPrices(date)
        val currencies = currencyDao.getAll()
        val notFound = currencies.filter { uzsPrice(it, prices) == null }.map { it.code }
        var added = 0
        var existing = 0
        db.withTransaction {
            for (i in currencies.indices) {
                for (j in i + 1 until currencies.size) {
                    val a = currencies[i]
                    val b = currencies[j]
                    val rate = RateMath.cross(uzsPrice(a, prices), uzsPrice(b, prices)) ?: continue
                    if (rateDao.find(date, a.id, b.id) != null || rateDao.find(date, b.id, a.id) != null) {
                        existing++
                    } else {
                        upsertPair(date, a.id, b.id, rate)
                        added++
                    }
                }
            }
        }
        return CbuImportResult(added, existing, notFound)
    }

    /** Manual add/edit from the rates screen. Saves the reverse rate as well. */
    suspend fun savePair(date: LocalDate, baseId: Long, quoteId: Long, rate: BigDecimal) =
        db.withTransaction { savePairChecked(date, baseId, quoteId, rate) }

    /** Used inside the transaction-save DB transaction. */
    suspend fun saveRatesForBase(date: LocalDate, baseId: Long, rates: Map<Long, BigDecimal>) =
        db.withTransaction { rates.forEach { (quoteId, rate) -> savePairChecked(date, baseId, quoteId, rate) } }

    suspend fun deletePair(date: LocalDate, a: Long, b: Long) = db.withTransaction {
        if (rateDao.isPairUsed(date, a, b)) throw DomainException(R.string.err_rate_locked)
        rateDao.deletePair(date, a, b)
    }

    private suspend fun savePairChecked(date: LocalDate, baseId: Long, quoteId: Long, rate: BigDecimal) {
        ensureNotFuture(date)
        if (baseId == quoteId) throw DomainException(R.string.err_rate_same_currency)
        if (rate.signum() <= 0) throw DomainException(R.string.err_rate_invalid)
        val exists = rateDao.find(date, baseId, quoteId) != null || rateDao.find(date, quoteId, baseId) != null
        if (exists && rateDao.isPairUsed(date, baseId, quoteId)) throw DomainException(R.string.err_rate_locked)
        upsertPair(date, baseId, quoteId, rate)
    }

    private suspend fun upsertPair(date: LocalDate, baseId: Long, quoteId: Long, rate: BigDecimal) {
        upsert(date, baseId, quoteId, rate.stripTrailingZeros())
        upsert(date, quoteId, baseId, RateMath.inverse(rate))
    }

    private suspend fun upsert(date: LocalDate, baseId: Long, quoteId: Long, rate: BigDecimal) {
        val current = rateDao.find(date, baseId, quoteId)
        if (current == null) {
            rateDao.insert(ExchangeRateEntity(date = date, baseCurrencyId = baseId, quoteCurrencyId = quoteId, rate = rate))
        } else {
            rateDao.update(current.copy(rate = rate))
        }
    }

    private fun ensureNotFuture(date: LocalDate) {
        if (date.isAfter(LocalDate.now())) throw DomainException(R.string.err_future_date)
    }
}
