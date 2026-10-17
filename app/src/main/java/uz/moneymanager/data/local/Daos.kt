package uz.moneymanager.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import uz.moneymanager.domain.model.TransactionType
import java.time.LocalDate

@Dao
interface CurrencyDao {
    @Query(
        """
        SELECT c.*,
            (SELECT COUNT(*) FROM transactions t WHERE t.currencyId = c.id) AS transactionCount,
            (SELECT COUNT(*) FROM exchange_rates r
                WHERE r.baseCurrencyId = c.id OR r.quoteCurrencyId = c.id) AS rateCount
        FROM currencies c
        ORDER BY c.isDefault DESC, c.code
        """
    )
    fun observeWithUsage(): Flow<List<CurrencyWithUsage>>

    @Query("SELECT * FROM currencies ORDER BY isDefault DESC, code")
    fun observeAll(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies ORDER BY isDefault DESC, code")
    suspend fun getAll(): List<CurrencyEntity>

    @Query("SELECT * FROM currencies WHERE id = :id")
    suspend fun getById(id: Long): CurrencyEntity?

    @Query("SELECT * FROM currencies WHERE code = :code LIMIT 1")
    suspend fun findByCode(code: String): CurrencyEntity?

    @Insert
    suspend fun insert(entity: CurrencyEntity): Long

    @Update
    suspend fun update(entity: CurrencyEntity)

    @Query("DELETE FROM currencies WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT (SELECT COUNT(*) FROM transactions WHERE currencyId = :id)
             + (SELECT COUNT(*) FROM exchange_rates WHERE baseCurrencyId = :id OR quoteCurrencyId = :id)
             + (SELECT COUNT(*) FROM transaction_rates WHERE quoteCurrencyId = :id)
        """
    )
    suspend fun usageCount(id: Long): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query(
        """
        SELECT c.*, (SELECT COUNT(*) FROM transactions t WHERE t.categoryId = c.id) AS transactionCount
        FROM categories c ORDER BY c.name COLLATE NOCASE
        """
    )
    fun observeWithUsage(): Flow<List<CategoryWithUsage>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE nameKey = :key LIMIT 1")
    suspend fun findByKey(key: String): CategoryEntity?

    @Insert
    suspend fun insert(entity: CategoryEntity): Long

    @Update
    suspend fun update(entity: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM transactions WHERE categoryId = :id")
    suspend fun usageCount(id: Long): Int
}

@Dao
interface ExchangeRateDao {
    @Query(
        """
        SELECT * FROM exchange_rates
        WHERE date = :date AND baseCurrencyId = :baseId AND quoteCurrencyId = :quoteId LIMIT 1
        """
    )
    suspend fun find(date: LocalDate, baseId: Long, quoteId: Long): ExchangeRateEntity?

    @Query("SELECT * FROM exchange_rates WHERE date = :date AND baseCurrencyId = :baseId")
    suspend fun getForBase(date: LocalDate, baseId: Long): List<ExchangeRateEntity>

    @Insert
    suspend fun insert(entity: ExchangeRateEntity): Long

    @Update
    suspend fun update(entity: ExchangeRateEntity)

    @Query(
        """
        DELETE FROM exchange_rates WHERE date = :date AND (
            (baseCurrencyId = :a AND quoteCurrencyId = :b) OR (baseCurrencyId = :b AND quoteCurrencyId = :a))
        """
    )
    suspend fun deletePair(date: LocalDate, a: Long, b: Long)

    /** A pair is "used" when a transaction snapshot on that date took this rate (either direction). */
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM transactions t
            INNER JOIN transaction_rates tr ON tr.transactionId = t.id
            WHERE t.date = :date AND (
                (t.currencyId = :a AND tr.quoteCurrencyId = :b) OR
                (t.currencyId = :b AND tr.quoteCurrencyId = :a)))
        """
    )
    suspend fun isPairUsed(date: LocalDate, a: Long, b: Long): Boolean

    @Query(
        """
        SELECT r.id AS id, r.date AS date, r.baseCurrencyId AS baseCurrencyId,
               r.quoteCurrencyId AS quoteCurrencyId, r.rate AS rate,
               b.code AS baseCode, q.code AS quoteCode,
               EXISTS(
                   SELECT 1 FROM transactions t
                   INNER JOIN transaction_rates tr ON tr.transactionId = t.id
                   WHERE t.date = r.date AND (
                       (t.currencyId = r.baseCurrencyId AND tr.quoteCurrencyId = r.quoteCurrencyId) OR
                       (t.currencyId = r.quoteCurrencyId AND tr.quoteCurrencyId = r.baseCurrencyId))
               ) AS locked
        FROM exchange_rates r
        INNER JOIN currencies b ON b.id = r.baseCurrencyId
        INNER JOIN currencies q ON q.id = r.quoteCurrencyId
        WHERE r.date = :date
        ORDER BY b.isDefault DESC, b.code, q.isDefault DESC, q.code
        """
    )
    fun observeForDate(date: LocalDate): Flow<List<ExchangeRateRow>>
}

private const val FILTER_WHERE = """
    WHERE (:categoryId IS NULL OR t.categoryId = :categoryId)
      AND (:currencyId IS NULL OR t.currencyId = :currencyId)
      AND (:type IS NULL OR t.type = :type)
      AND (:fromDate IS NULL OR t.date >= :fromDate)
      AND (:toDate IS NULL OR t.date <= :toDate)
"""

private const val SNAPSHOT_JOINS = """
    LEFT JOIN transaction_rates trD
        ON trD.transactionId = t.id AND trD.quoteCurrencyId = :displayCurrencyId
    LEFT JOIN transaction_rates trF
        ON trF.transactionId = t.id AND trF.quoteCurrencyId = :fallbackCurrencyId
"""

private const val SELECT_ROWS = """
    SELECT t.id AS id, t.type AS type, t.amount AS amount, t.date AS date,
           c.code AS currencyCode, cat.name AS categoryName,
           trD.rate AS displayRate, trF.rate AS fallbackRate
    FROM transactions t
    INNER JOIN currencies c ON c.id = t.currencyId
    INNER JOIN categories cat ON cat.id = t.categoryId
""" + SNAPSHOT_JOINS

private const val SELECT_TOTALS = """
    SELECT t.type AS type, t.amount AS amount, trD.rate AS displayRate, trF.rate AS fallbackRate
    FROM transactions t
""" + SNAPSHOT_JOINS

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(entity: TransactionEntity): Long

    @Update
    suspend fun update(entity: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Insert
    suspend fun insertSnapshot(rates: List<TransactionRateEntity>)

    @Query("DELETE FROM transaction_rates WHERE transactionId = :transactionId")
    suspend fun deleteSnapshot(transactionId: Long)

    @Query(SELECT_ROWS + FILTER_WHERE + " ORDER BY t.date DESC, t.createdAt DESC, t.id DESC")
    fun paging(
        categoryId: Long?,
        currencyId: Long?,
        type: TransactionType?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        displayCurrencyId: Long,
        fallbackCurrencyId: Long,
    ): PagingSource<Int, TransactionListRow>

    @Query(SELECT_TOTALS + FILTER_WHERE)
    fun totals(
        categoryId: Long?,
        currencyId: Long?,
        type: TransactionType?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        displayCurrencyId: Long,
        fallbackCurrencyId: Long,
    ): Flow<List<TotalsRow>>
}
