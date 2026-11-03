package uz.moneymanager.ui

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.internal.MainDispatcherFactory
import uz.moneymanager.data.local.AppDatabase
import uz.moneymanager.data.local.CategoryDao
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CategoryWithUsage
import uz.moneymanager.data.local.CurrencyDao
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.CurrencyWithUsage
import uz.moneymanager.data.local.DailyTotalsRow
import uz.moneymanager.data.local.ExchangeRateDao
import uz.moneymanager.data.local.ExchangeRateEntity
import uz.moneymanager.data.local.ExchangeRateRow
import uz.moneymanager.data.local.PaymentMethodDao
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.data.local.PaymentMethodWithUsage
import uz.moneymanager.data.local.TotalsRow
import uz.moneymanager.data.local.TransactionDao
import uz.moneymanager.data.local.TransactionEntity
import uz.moneymanager.data.local.TransactionListRow
import uz.moneymanager.data.local.TransactionRateEntity
import uz.moneymanager.data.remote.CbuApi
import uz.moneymanager.data.remote.CbuRateDto
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.ExchangeRateRepository
import uz.moneymanager.data.repository.PaymentMethodRepository
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.model.TransactionType
import java.time.LocalDate
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

@OptIn(InternalCoroutinesApi::class)
class TestMainDispatcherFactory : MainDispatcherFactory {
    override val loadPriority: Int = Int.MAX_VALUE
    override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher =
        ImmediateTestMainDispatcher
}

object ImmediateTestMainDispatcher : MainCoroutineDispatcher() {
    override val immediate: MainCoroutineDispatcher get() = this

    override fun isDispatchNeeded(context: CoroutineContext): Boolean = false

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        block.run()
    }
}

class FakeAppDatabase(
    private val fakeCurrencyDao: CurrencyDao,
    private val fakeCategoryDao: CategoryDao,
    private val fakeExchangeRateDao: ExchangeRateDao,
    private val fakePaymentMethodDao: PaymentMethodDao,
    private val fakeTransactionDao: TransactionDao,
) : AppDatabase() {
    private val directExecutor = Executor { it.run() }

    override val transactionExecutor: Executor = directExecutor
    override val queryExecutor: Executor = directExecutor

    @Suppress("DEPRECATION")
    override fun beginTransaction() {}

    @Suppress("DEPRECATION")
    override fun endTransaction() {}

    @Suppress("DEPRECATION")
    override fun setTransactionSuccessful() {}

    override fun currencyDao(): CurrencyDao = fakeCurrencyDao
    override fun categoryDao(): CategoryDao = fakeCategoryDao
    override fun exchangeRateDao(): ExchangeRateDao = fakeExchangeRateDao
    override fun paymentMethodDao(): PaymentMethodDao = fakePaymentMethodDao
    override fun transactionDao(): TransactionDao = fakeTransactionDao

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        throw UnsupportedOperationException("Not used in fake unit tests")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return InvalidationTracker(this, emptyMap(), emptyMap(), "transactions")
    }

    override fun clearAllTables() {}
}

class FakeCurrencyDao(
    initial: List<CurrencyEntity> = listOf(
        CurrencyEntity(id = 1L, code = "UZS", name = "Uzbek sum", isDefault = true)
    ),
) : CurrencyDao {
    private val items = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

    override fun observeWithUsage(): Flow<List<CurrencyWithUsage>> =
        items.map { list -> list.map { CurrencyWithUsage(it, 0, 0) } }

    override fun observeAll(): Flow<List<CurrencyEntity>> = items

    override suspend fun getAll(): List<CurrencyEntity> = items.value

    override suspend fun getById(id: Long): CurrencyEntity? = items.value.firstOrNull { it.id == id }

    override suspend fun findByCode(code: String): CurrencyEntity? = items.value.firstOrNull { it.code == code }

    override suspend fun insert(entity: CurrencyEntity): Long {
        val id = if (entity.id == 0L) nextId++ else entity.id
        items.value = items.value + entity.copy(id = id)
        return id
    }

    override suspend fun update(entity: CurrencyEntity) {
        items.value = items.value.map { if (it.id == entity.id) entity else it }
    }

    override suspend fun deleteById(id: Long) {
        items.value = items.value.filterNot { it.id == id }
    }

    override suspend fun usageCount(id: Long): Int = 0
}

class FakeCategoryDao(
    initial: List<CategoryEntity> = listOf(
        CategoryEntity(id = 1L, name = "Ovqat", nameKey = "ovqat"),
        CategoryEntity(id = 2L, name = "Transport", nameKey = "transport"),
    ),
) : CategoryDao {
    private val items = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

    override fun observeAll(): Flow<List<CategoryEntity>> = items

    override fun observeWithUsage(): Flow<List<CategoryWithUsage>> =
        items.map { list -> list.map { CategoryWithUsage(it, 0) } }

    override suspend fun getById(id: Long): CategoryEntity? = items.value.firstOrNull { it.id == id }

    override suspend fun findByKey(key: String): CategoryEntity? = items.value.firstOrNull { it.nameKey == key }

    override suspend fun insert(entity: CategoryEntity): Long {
        val id = if (entity.id == 0L) nextId++ else entity.id
        items.value = items.value + entity.copy(id = id)
        return id
    }

    override suspend fun update(entity: CategoryEntity) {
        items.value = items.value.map { if (it.id == entity.id) entity else it }
    }

    override suspend fun deleteById(id: Long) {
        items.value = items.value.filterNot { it.id == id }
    }

    override suspend fun usageCount(id: Long): Int = 0
}

class FakePaymentMethodDao(
    private val transactionFlow: MutableStateFlow<List<TransactionEntity>>,
    initial: List<PaymentMethodEntity> = listOf(
        PaymentMethodEntity(id = 1L, name = "Cash", nameKey = "cash", isDefault = true),
        PaymentMethodEntity(id = 2L, name = "Transfer", nameKey = "transfer", isDefault = false),
    ),
) : PaymentMethodDao {
    val items = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

    private fun sortMethods(list: List<PaymentMethodEntity>): List<PaymentMethodEntity> =
        list.sortedWith(compareByDescending<PaymentMethodEntity> { it.isDefault }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })

    override fun observeAll(): Flow<List<PaymentMethodEntity>> = items.map(::sortMethods)

    override fun observeWithUsage(): Flow<List<PaymentMethodWithUsage>> =
        combine(items, transactionFlow) { methods, txs ->
            sortMethods(methods).map { pm ->
                val count = txs.count { it.paymentMethodId == pm.id }
                PaymentMethodWithUsage(pm, count)
            }
        }

    override suspend fun getAll(): List<PaymentMethodEntity> = sortMethods(items.value)

    override suspend fun getById(id: Long): PaymentMethodEntity? = items.value.firstOrNull { it.id == id }

    override suspend fun findByKey(key: String): PaymentMethodEntity? = items.value.firstOrNull { it.nameKey == key }

    override suspend fun getDefault(): PaymentMethodEntity? = items.value.firstOrNull { it.isDefault }

    override suspend fun insert(entity: PaymentMethodEntity): Long {
        val id = if (entity.id == 0L) nextId++ else entity.id
        items.value = items.value + entity.copy(id = id)
        return id
    }

    override suspend fun update(entity: PaymentMethodEntity) {
        items.value = items.value.map { if (it.id == entity.id) entity else it }
    }

    override suspend fun deleteById(id: Long) {
        items.value = items.value.filterNot { it.id == id }
    }

    override suspend fun usageCount(id: Long): Int =
        transactionFlow.value.count { it.paymentMethodId == id }
}

class FakeExchangeRateDao : ExchangeRateDao {
    private val rates = MutableStateFlow<List<ExchangeRateEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun find(date: LocalDate, baseId: Long, quoteId: Long): ExchangeRateEntity? =
        rates.value.firstOrNull { it.date == date && it.baseCurrencyId == baseId && it.quoteCurrencyId == quoteId }

    override suspend fun getForBase(date: LocalDate, baseId: Long): List<ExchangeRateEntity> =
        rates.value.filter { it.date == date && it.baseCurrencyId == baseId }

    override suspend fun insert(entity: ExchangeRateEntity): Long {
        val id = if (entity.id == 0L) nextId++ else entity.id
        rates.value = rates.value + entity.copy(id = id)
        return id
    }

    override suspend fun update(entity: ExchangeRateEntity) {
        rates.value = rates.value.map { if (it.id == entity.id) entity else it }
    }

    override suspend fun deletePair(date: LocalDate, a: Long, b: Long) {
        rates.value = rates.value.filterNot {
            it.date == date && ((it.baseCurrencyId == a && it.quoteCurrencyId == b) || (it.baseCurrencyId == b && it.quoteCurrencyId == a))
        }
    }

    override suspend fun isPairUsed(date: LocalDate, a: Long, b: Long): Boolean = false

    override fun observeForDate(date: LocalDate): Flow<List<ExchangeRateRow>> = MutableStateFlow(emptyList())
}

class FakeTransactionDao(
    private val currencyDao: FakeCurrencyDao,
    private val categoryDao: FakeCategoryDao,
    private val paymentMethodDaoProvider: () -> FakePaymentMethodDao,
) : TransactionDao {
    val transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private val snapshots = MutableStateFlow<List<TransactionRateEntity>>(emptyList())
    private var nextId = 1L

    override suspend fun insert(entity: TransactionEntity): Long {
        val id = if (entity.id == 0L) nextId++ else entity.id
        transactions.value = transactions.value + entity.copy(id = id)
        return id
    }

    override suspend fun update(entity: TransactionEntity) {
        transactions.value = transactions.value.map { if (it.id == entity.id) entity else it }
    }

    override suspend fun deleteById(id: Long) {
        transactions.value = transactions.value.filterNot { it.id == id }
        snapshots.value = snapshots.value.filterNot { it.transactionId == id }
    }

    override suspend fun getById(id: Long): TransactionEntity? =
        transactions.value.firstOrNull { it.id == id }

    override suspend fun insertSnapshot(rates: List<TransactionRateEntity>) {
        snapshots.value = snapshots.value + rates
    }

    override suspend fun deleteSnapshot(transactionId: Long) {
        snapshots.value = snapshots.value.filterNot { it.transactionId == transactionId }
    }

    private fun filterTransactions(
        list: List<TransactionEntity>,
        categoryId: Long?,
        currencyId: Long?,
        paymentMethodId: Long?,
        type: TransactionType?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
    ): List<TransactionEntity> = list.filter { t ->
        (categoryId == null || t.categoryId == categoryId) &&
            (currencyId == null || t.currencyId == currencyId) &&
            (paymentMethodId == null || t.paymentMethodId == paymentMethodId) &&
            (type == null || t.type == type) &&
            (fromDate == null || t.date >= fromDate) &&
            (toDate == null || t.date <= toDate)
    }

    override fun paging(
        categoryId: Long?,
        currencyId: Long?,
        paymentMethodId: Long?,
        type: TransactionType?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        displayCurrencyId: Long,
        fallbackCurrencyId: Long,
    ): PagingSource<Int, TransactionListRow> = object : PagingSource<Int, TransactionListRow>() {
        override fun getRefreshKey(state: PagingState<Int, TransactionListRow>): Int? = null
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionListRow> {
            val filtered = filterTransactions(
                transactions.value, categoryId, currencyId, paymentMethodId, type, fromDate, toDate
            ).sortedWith(compareByDescending<TransactionEntity> { it.date }.thenByDescending { it.createdAt }.thenByDescending { it.id })

            val pmDao = paymentMethodDaoProvider()
            val rows = filtered.mapNotNull { t ->
                val curr = currencyDao.getById(t.currencyId) ?: return@mapNotNull null
                val cat = categoryDao.getById(t.categoryId) ?: return@mapNotNull null
                val pm = pmDao.getById(t.paymentMethodId) ?: return@mapNotNull null
                val dRate = snapshots.value.firstOrNull { it.transactionId == t.id && it.quoteCurrencyId == displayCurrencyId }?.rate
                val fRate = snapshots.value.firstOrNull { it.transactionId == t.id && it.quoteCurrencyId == fallbackCurrencyId }?.rate
                TransactionListRow(
                    id = t.id,
                    type = t.type,
                    amount = t.amount,
                    date = t.date,
                    currencyCode = curr.code,
                    categoryName = cat.name,
                    paymentMethodId = pm.id,
                    paymentMethodName = pm.name,
                    description = t.description,
                    displayRate = dRate,
                    fallbackRate = fRate,
                )
            }
            return LoadResult.Page(rows, prevKey = null, nextKey = null)
        }
    }

    override fun totals(
        categoryId: Long?,
        currencyId: Long?,
        paymentMethodId: Long?,
        type: TransactionType?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        displayCurrencyId: Long,
        fallbackCurrencyId: Long,
    ): Flow<List<TotalsRow>> = combine(transactions, snapshots) { txs, snaps ->
        filterTransactions(txs, categoryId, currencyId, paymentMethodId, type, fromDate, toDate).map { t ->
            val dRate = snaps.firstOrNull { it.transactionId == t.id && it.quoteCurrencyId == displayCurrencyId }?.rate
            val fRate = snaps.firstOrNull { it.transactionId == t.id && it.quoteCurrencyId == fallbackCurrencyId }?.rate
            TotalsRow(t.type, t.amount, dRate, fRate)
        }
    }

    override fun dailyTotals(
        categoryId: Long?,
        currencyId: Long?,
        paymentMethodId: Long?,
        type: TransactionType?,
        fromDate: LocalDate?,
        toDate: LocalDate?,
        displayCurrencyId: Long,
        fallbackCurrencyId: Long,
    ): Flow<List<DailyTotalsRow>> = combine(transactions, snapshots) { txs, snaps ->
        filterTransactions(txs, categoryId, currencyId, paymentMethodId, type, fromDate, toDate).map { t ->
            val dRate = snaps.firstOrNull { it.transactionId == t.id && it.quoteCurrencyId == displayCurrencyId }?.rate
            val fRate = snaps.firstOrNull { it.transactionId == t.id && it.quoteCurrencyId == fallbackCurrencyId }?.rate
            DailyTotalsRow(t.date, t.type, t.amount, dRate, fRate)
        }
    }

    override suspend fun lastUsedCurrencyId(): Long? =
        transactions.value
            .sortedWith(compareByDescending<TransactionEntity> { it.createdAt }.thenByDescending { it.id })
            .firstOrNull()?.currencyId

    override suspend fun lastUsedPaymentMethodId(): Long? =
        transactions.value
            .sortedWith(compareByDescending<TransactionEntity> { it.createdAt }.thenByDescending { it.id })
            .firstOrNull()?.paymentMethodId

    override fun observeRecentCategoryNames(limit: Int): Flow<List<String>> = MutableStateFlow(emptyList())
}

class FakeCbuApi : CbuApi {
    override suspend fun ratesForDate(date: String): List<CbuRateDto> = emptyList()
}

class FakeRepositoriesEnv {
    val currencyDao = FakeCurrencyDao()
    val categoryDao = FakeCategoryDao()
    val rateDao = FakeExchangeRateDao()
    lateinit var paymentMethodDao: FakePaymentMethodDao
    val transactionDao = FakeTransactionDao(currencyDao, categoryDao) { paymentMethodDao }

    init {
        paymentMethodDao = FakePaymentMethodDao(transactionDao.transactions)
    }

    val db = FakeAppDatabase(currencyDao, categoryDao, rateDao, paymentMethodDao, transactionDao)
    val cbuApi = FakeCbuApi()

    val currencyRepository = CurrencyRepository(db, currencyDao)
    val categoryRepository = CategoryRepository(db, categoryDao)
    val paymentMethodRepository = PaymentMethodRepository(db, paymentMethodDao)
    val rateRepository = ExchangeRateRepository(db, rateDao, currencyDao, cbuApi)
    val transactionRepository = TransactionRepository(
        db = db,
        dao = transactionDao,
        currencyDao = currencyDao,
        categoryRepository = categoryRepository,
        paymentMethodDao = paymentMethodDao,
        rateRepository = rateRepository,
    )
}
