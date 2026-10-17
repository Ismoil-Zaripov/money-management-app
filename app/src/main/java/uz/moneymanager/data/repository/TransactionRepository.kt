package uz.moneymanager.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import uz.moneymanager.R
import uz.moneymanager.data.local.AppDatabase
import uz.moneymanager.data.local.CurrencyDao
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.TransactionDao
import uz.moneymanager.data.local.TransactionEntity
import uz.moneymanager.data.local.TransactionListRow
import uz.moneymanager.data.local.TransactionRateEntity
import uz.moneymanager.domain.DomainException
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.MonitoringTotals
import uz.moneymanager.domain.TotalsCalculator
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class TransactionInput(
    val id: Long?,
    val type: TransactionType,
    val amount: BigDecimal,
    val currencyId: Long,
    val categoryName: String,
    val date: LocalDate,
)

data class TransactionDetails(
    val id: Long,
    val type: TransactionType,
    val amount: BigDecimal,
    val currencyId: Long,
    val categoryName: String,
    val date: LocalDate,
)

sealed interface SaveResult {
    data object Saved : SaveResult

    /** Rates `1 base = ? quote` are required on [date] before a new transaction can be created. */
    data class MissingRates(
        val base: CurrencyEntity,
        val date: LocalDate,
        val missing: List<CurrencyEntity>,
    ) : SaveResult
}

@Singleton
class TransactionRepository @Inject constructor(
    private val db: AppDatabase,
    private val dao: TransactionDao,
    private val currencyDao: CurrencyDao,
    private val categoryRepository: CategoryRepository,
    private val rateRepository: ExchangeRateRepository,
) {
    fun paging(filter: MonitoringFilter, displayCurrencyId: Long, fallbackCurrencyId: Long): Flow<PagingData<TransactionListRow>> =
        Pager(PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)) {
            dao.paging(
                filter.categoryId, filter.currencyId, filter.type, filter.from, filter.to,
                displayCurrencyId, fallbackCurrencyId,
            )
        }.flow

    fun totals(filter: MonitoringFilter, displayCurrencyId: Long, fallbackCurrencyId: Long): Flow<MonitoringTotals> =
        dao.totals(
            filter.categoryId, filter.currencyId, filter.type, filter.from, filter.to,
            displayCurrencyId, fallbackCurrencyId,
        ).map(TotalsCalculator::calculate).flowOn(Dispatchers.Default)

    suspend fun getDetails(id: Long): TransactionDetails? {
        val t = dao.getById(id) ?: return null
        val category = categoryRepository.getById(t.categoryId) ?: return null
        return TransactionDetails(t.id, t.type, t.amount, t.currencyId, category.name, t.date)
    }

    /**
     * Creates or updates a transaction.
     * - New: if rates for (currency, date) are missing, returns [SaveResult.MissingRates]
     *   unless they are supplied in [newRates] (saved together with their reverse).
     * - Edit: changing currency/date is only allowed when the rates for the new
     *   (currency, date) already exist; otherwise [DomainException].
     */
    suspend fun save(input: TransactionInput, newRates: Map<Long, BigDecimal> = emptyMap()): SaveResult =
        db.withTransaction {
            if (input.amount.signum() <= 0) throw DomainException(R.string.err_amount_positive)
            if (input.date.isAfter(LocalDate.now())) throw DomainException(R.string.err_future_date)
            val currency = currencyDao.getById(input.currencyId)
                ?: throw DomainException(R.string.err_currency_not_found)

            if (input.id == null) {
                if (newRates.isNotEmpty()) rateRepository.saveRatesForBase(input.date, currency.id, newRates)
                val missing = rateRepository.missingRates(currency.id, input.date)
                if (missing.isNotEmpty()) return@withTransaction SaveResult.MissingRates(currency, input.date, missing)

                val category = categoryRepository.findOrCreate(input.categoryName)
                val id = dao.insert(
                    TransactionEntity(
                        type = input.type,
                        amount = input.amount,
                        currencyId = currency.id,
                        categoryId = category.id,
                        date = input.date,
                        createdAt = System.currentTimeMillis(),
                    )
                )
                writeSnapshot(id, currency.id, input.date)
            } else {
                val existing = dao.getById(input.id) ?: throw DomainException(R.string.err_transaction_not_found)
                val rateKeyChanged = existing.currencyId != currency.id || existing.date != input.date
                if (rateKeyChanged && rateRepository.missingRates(currency.id, input.date).isNotEmpty()) {
                    throw DomainException(R.string.err_edit_rates_missing, currency.code, input.date.toString())
                }
                val category = categoryRepository.findOrCreate(input.categoryName)
                dao.update(
                    existing.copy(
                        type = input.type,
                        amount = input.amount,
                        currencyId = currency.id,
                        categoryId = category.id,
                        date = input.date,
                    )
                )
                if (rateKeyChanged) {
                    dao.deleteSnapshot(existing.id)
                    writeSnapshot(existing.id, currency.id, input.date)
                }
            }
            SaveResult.Saved
        }

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun lastUsedCurrencyId(): Long? = dao.lastUsedCurrencyId()

    fun recentCategoryNames(limit: Int): Flow<List<String>> = dao.observeRecentCategoryNames(limit)

    private suspend fun writeSnapshot(transactionId: Long, currencyId: Long, date: LocalDate) {
        val rates = rateRepository.ratesForBase(currencyId, date)
            .filter { it.quoteCurrencyId != currencyId }
            .map { TransactionRateEntity(transactionId, it.quoteCurrencyId, it.rate) }
        dao.insertSnapshot(rates + TransactionRateEntity(transactionId, currencyId, BigDecimal.ONE))
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
