package uz.moneymanager.data.local

import android.database.sqlite.SQLiteConstraintException
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uz.moneymanager.R
import uz.moneymanager.data.repository.PaymentMethodRepository
import uz.moneymanager.domain.DomainException
import uz.moneymanager.domain.PaymentMethodNames
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class PaymentMethodDaoAndRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var paymentMethodDao: PaymentMethodDao
    private lateinit var transactionDao: TransactionDao
    private lateinit var currencyDao: CurrencyDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var repository: PaymentMethodRepository

    private var uzsId: Long = 0L
    private var usdId: Long = 0L
    private var foodCatId: Long = 0L
    private var salaryCatId: Long = 0L
    private val cashId: Long = 1L
    private val transferId: Long = 2L

    @Before
    fun setUp() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    AppDatabase.SEED_CALLBACK.onCreate(db)
                }
            })
            .build()

        paymentMethodDao = db.paymentMethodDao()
        transactionDao = db.transactionDao()
        currencyDao = db.currencyDao()
        categoryDao = db.categoryDao()
        repository = PaymentMethodRepository(db, paymentMethodDao)

        // Trigger SEED_CALLBACK and load seeded UZS
        val allCurrencies = currencyDao.getAll()
        uzsId = allCurrencies.first { it.code == "UZS" }.id
        usdId = currencyDao.insert(CurrencyEntity(code = "USD", name = "US Dollar", isDefault = false))

        foodCatId = categoryDao.insert(CategoryEntity(name = "Ovqat", nameKey = "ovqat"))
        salaryCatId = categoryDao.insert(CategoryEntity(name = "Maosh", nameKey = "maosh"))
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addPaymentMethod_normalizesNameAndOrdersDefaultFirst() = runBlocking {
        val added = repository.add("  Humo   Card  ")
        assertEquals("Humo Card", added.name)
        assertEquals("humo card", added.nameKey)
        assertFalse(added.isDefault)
        assertTrue(added.id > 0)

        val another = repository.add("Anorbank")
        assertEquals("Anorbank", another.name)

        // Ordering: isDefault DESC, name COLLATE NOCASE -> Cash (default), Anorbank, Humo Card, Transfer
        val list = repository.observeAll().first()
        assertEquals(listOf("Cash", "Anorbank", "Humo Card", "Transfer"), list.map { it.name })
    }

    @Test
    fun addPaymentMethod_duplicateWithDifferentCaseOrWhitespace_throwsDomainException() = runBlocking {
        // Seeded "Cash" already exists
        val duplicatesOfCash = listOf("cash", "CASH", "  cAsH  ")
        for (dup in duplicatesOfCash) {
            try {
                repository.add(dup)
                fail("Expected DomainException for duplicate '$dup'")
            } catch (e: DomainException) {
                assertEquals(R.string.err_payment_method_duplicate, e.messageRes)
                assertEquals("Cash", e.args.firstOrNull())
            }
        }

        // Multi-word & Cyrillic duplicate checks
        repository.add("Bank Transfer")
        try {
            repository.add("  bank    TRANSFER ")
            fail("Expected DomainException for multi-space duplicate")
        } catch (e: DomainException) {
            assertEquals(R.string.err_payment_method_duplicate, e.messageRes)
            assertEquals("Bank Transfer", e.args.firstOrNull())
        }

        repository.add("Наличные")
        try {
            repository.add("  наличные  ")
            fail("Expected DomainException for Cyrillic case-insensitive duplicate")
        } catch (e: DomainException) {
            assertEquals(R.string.err_payment_method_duplicate, e.messageRes)
            assertEquals("Наличные", e.args.firstOrNull())
        }
    }

    @Test
    fun addPaymentMethod_maxLengthBoundary_50Allowed_51Rejected() = runBlocking {
        val exact50 = "A".repeat(PaymentMethodNames.MAX_LENGTH)
        val created50 = repository.add(exact50)
        assertEquals(50, created50.name.length)

        val padded50 = "   " + "B".repeat(PaymentMethodNames.MAX_LENGTH) + "   "
        val createdPadded = repository.add(padded50)
        assertEquals(50, createdPadded.name.length)

        val tooLong51 = "C".repeat(PaymentMethodNames.MAX_LENGTH + 1)
        try {
            repository.add(tooLong51)
            fail("Expected DomainException for length 51")
        } catch (e: DomainException) {
            assertEquals(R.string.err_payment_method_too_long, e.messageRes)
        }
    }

    @Test
    fun addAndRename_blankName_throwsRequiredException() = runBlocking {
        for (blank in listOf("", "   ", "\t\n ")) {
            try {
                repository.add(blank)
                fail("Expected err_payment_method_required on add('$blank')")
            } catch (e: DomainException) {
                assertEquals(R.string.err_payment_method_required, e.messageRes)
            }

            try {
                repository.rename(transferId, blank)
                fail("Expected err_payment_method_required on rename('$blank')")
            } catch (e: DomainException) {
                assertEquals(R.string.err_payment_method_required, e.messageRes)
            }
        }
    }

    @Test
    fun renamePaymentMethod_updatesNameAndRejectsDuplicatesOrMissingId() = runBlocking {
        // Valid rename with normalization
        repository.rename(transferId, "  Uzcard   Pay  ")
        val updated = repository.getById(transferId)
        assertNotNull(updated)
        assertEquals("Uzcard Pay", updated!!.name)
        assertEquals("uzcard pay", updated.nameKey)

        // Rename to same name with different case should succeed
        repository.rename(transferId, "UZCARD PAY")
        assertEquals("UZCARD PAY", repository.getById(transferId)!!.name)

        // Rename to another existing method's name ("cash") should fail
        try {
            repository.rename(transferId, "  cash ")
            fail("Expected duplicate exception when renaming to Cash")
        } catch (e: DomainException) {
            assertEquals(R.string.err_payment_method_duplicate, e.messageRes)
        }

        // Rename non-existent ID should fail with err_payment_method_not_found
        try {
            repository.rename(9999L, "Click")
            fail("Expected not_found exception for non-existent id")
        } catch (e: DomainException) {
            assertEquals(R.string.err_payment_method_not_found, e.messageRes)
        }
    }

    @Test
    fun deletePaymentMethod_usedInTransaction_isRejectedByRepositoryAndForeignKey() = runBlocking {
        val txId = transactionDao.insert(
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = BigDecimal("50000"),
                currencyId = uzsId,
                categoryId = foodCatId,
                paymentMethodId = transferId,
                date = LocalDate.of(2026, 10, 25),
                createdAt = 1000L,
                description = "Taksi",
            )
        )

        // 1. Repository blocks deletion with DomainException(err_payment_method_in_use)
        try {
            repository.delete(transferId)
            fail("Expected err_payment_method_in_use when deleting used payment method")
        } catch (e: DomainException) {
            assertEquals(R.string.err_payment_method_in_use, e.messageRes)
            assertEquals("Transfer", e.args.firstOrNull())
        }
        assertNotNull(repository.getById(transferId))

        // 2. Direct DAO delete is also blocked by ForeignKey.RESTRICT
        try {
            paymentMethodDao.deleteById(transferId)
            fail("Expected SQLiteConstraintException from ForeignKey.RESTRICT")
        } catch (_: SQLiteConstraintException) {
            // Expected
        }

        // 3. After deleting the transaction, the unused non-default payment method can be deleted
        transactionDao.deleteById(txId)
        repository.delete(transferId)
        assertNull(repository.getById(transferId))
    }

    @Test
    fun deletePaymentMethod_defaultCashWhenUnused_shouldRemainProtected() = runBlocking {
        // 1. Default payment method (Cash, id=1, isDefault=true) when unused (transactionCount == 0)
        // must throw err_payment_method_default_locked (not err_payment_method_in_use) and remain in DB
        try {
            repository.delete(cashId)
            fail("Expected err_payment_method_default_locked when deleting default payment method")
        } catch (e: DomainException) {
            assertEquals(R.string.err_payment_method_default_locked, e.messageRes)
        }
        assertNotNull(
            "Standart (isDefault=true) 'Cash' to'lov usuli tranzaksiyasi bo'lmasa ham o'chib ketmasligi kerak",
            repository.getDefault()
        )

        // 2. Even when Cash has transactions, default lock takes precedence over in-use error
        transactionDao.insert(
            TransactionEntity(
                type = TransactionType.EXPENSE,
                amount = BigDecimal("10000"),
                currencyId = uzsId,
                categoryId = foodCatId,
                paymentMethodId = cashId,
                date = LocalDate.of(2026, 10, 25),
                createdAt = 1000L,
            )
        )
        try {
            repository.delete(cashId)
            fail("Expected err_payment_method_default_locked when deleting default payment method")
        } catch (e: DomainException) {
            assertEquals(R.string.err_payment_method_default_locked, e.messageRes)
        }
        assertNotNull(repository.getDefault())
    }

    @Test
    fun filterByPaymentMethod_andCombinedFilters_inPagingTotalsAndDailyTotals() = runBlocking {
        val click = repository.add("Click")
        val date1 = LocalDate.of(2026, 10, 20)
        val date2 = LocalDate.of(2026, 10, 25)

        // Tx 1: Cash, UZS, Food, EXPENSE, 100_000 on date1
        insertTxWithRate(
            type = TransactionType.EXPENSE,
            amount = BigDecimal("100000"),
            currencyId = uzsId,
            categoryId = foodCatId,
            paymentMethodId = cashId,
            date = date1,
            createdAt = 100L,
        )
        // Tx 2: Transfer, UZS, Food, EXPENSE, 250_000 on date1
        insertTxWithRate(
            type = TransactionType.EXPENSE,
            amount = BigDecimal("250000"),
            currencyId = uzsId,
            categoryId = foodCatId,
            paymentMethodId = transferId,
            date = date1,
            createdAt = 200L,
        )
        // Tx 3: Transfer, UZS, Salary, INCOME, 1_000_000 on date2
        insertTxWithRate(
            type = TransactionType.INCOME,
            amount = BigDecimal("1000000"),
            currencyId = uzsId,
            categoryId = salaryCatId,
            paymentMethodId = transferId,
            date = date2,
            createdAt = 300L,
        )
        // Tx 4: Click, USD, Food, EXPENSE, 20 USD (rate 12500 -> 250_000 UZS) on date2
        insertTxWithRate(
            type = TransactionType.EXPENSE,
            amount = BigDecimal("20"),
            currencyId = usdId,
            categoryId = foodCatId,
            paymentMethodId = click.id,
            date = date2,
            createdAt = 400L,
            uzsRate = BigDecimal("12500"),
        )

        // 1. Filter only by paymentMethodId = transferId -> should return Tx 2 and Tx 3
        val transferRows = loadPagingRows(paymentMethodId = transferId)
        assertEquals(2, transferRows.size)
        assertTrue(transferRows.all { it.paymentMethodId == transferId && it.paymentMethodName == "Transfer" })

        val transferTotals = transactionDao.totals(
            categoryId = null,
            currencyId = null,
            paymentMethodId = transferId,
            type = null,
            fromDate = null,
            toDate = null,
            displayCurrencyId = uzsId,
            fallbackCurrencyId = uzsId,
        ).first()
        assertEquals(2, transferTotals.size)

        val transferDaily = transactionDao.dailyTotals(
            categoryId = null,
            currencyId = null,
            paymentMethodId = transferId,
            type = null,
            fromDate = null,
            toDate = null,
            displayCurrencyId = uzsId,
            fallbackCurrencyId = uzsId,
        ).first()
        assertEquals(2, transferDaily.size)

        // 2. Combined filter: paymentMethodId = transferId + type = EXPENSE + categoryId = foodCatId + date range = date1..date1
        val combinedRows = loadPagingRows(
            categoryId = foodCatId,
            currencyId = uzsId,
            paymentMethodId = transferId,
            type = TransactionType.EXPENSE,
            fromDate = date1,
            toDate = date1,
        )
        assertEquals(1, combinedRows.size)
        assertEquals(0, BigDecimal("250000").compareTo(combinedRows.first().amount))
        assertEquals("Transfer", combinedRows.first().paymentMethodName)
    }

    @Test
    fun lastUsedPaymentMethodId_emptyAndPopulatedDatabase() = runBlocking {
        // 1. Empty database -> null
        assertNull(transactionDao.lastUsedPaymentMethodId())

        val click = repository.add("Click")
        val today = LocalDate.of(2026, 10, 27)

        // 2. Insert first transaction with Cash (createdAt = 1000)
        insertTxWithRate(
            type = TransactionType.EXPENSE,
            amount = BigDecimal("10000"),
            currencyId = uzsId,
            categoryId = foodCatId,
            paymentMethodId = cashId,
            date = today,
            createdAt = 1000L,
        )
        assertEquals(cashId, transactionDao.lastUsedPaymentMethodId())

        // 3. Insert second transaction with Click (createdAt = 2000)
        val clickTxId = insertTxWithRate(
            type = TransactionType.EXPENSE,
            amount = BigDecimal("20000"),
            currencyId = uzsId,
            categoryId = foodCatId,
            paymentMethodId = click.id,
            date = today.minusDays(5), // Even with an older transaction date, createdAt is newer!
            createdAt = 2000L,
        )
        assertEquals(click.id, transactionDao.lastUsedPaymentMethodId())

        // 4. Delete latest transaction -> falls back to previous (Cash)
        transactionDao.deleteById(clickTxId)
        assertEquals(cashId, transactionDao.lastUsedPaymentMethodId())
    }

    @Test
    fun observeWithUsage_accuratelyTracksTransactionCounts() = runBlocking {
        val click = repository.add("Click")
        val today = LocalDate.of(2026, 10, 27)

        // Initially all have 0 transactions
        val initialUsage = repository.observeWithUsage().first().associateBy { it.paymentMethod.id }
        assertEquals(0, initialUsage[cashId]?.transactionCount)
        assertFalse(initialUsage[cashId]!!.isUsed)
        assertEquals(0, initialUsage[transferId]?.transactionCount)
        assertEquals(0, initialUsage[click.id]?.transactionCount)

        // Add 2 transactions to Cash and 1 to Transfer
        val tx1 = insertTxWithRate(TransactionType.EXPENSE, BigDecimal("100"), uzsId, foodCatId, cashId, today, 100L)
        val tx2 = insertTxWithRate(TransactionType.EXPENSE, BigDecimal("200"), uzsId, foodCatId, cashId, today, 200L)
        insertTxWithRate(TransactionType.INCOME, BigDecimal("300"), uzsId, salaryCatId, transferId, today, 300L)

        val afterInsert = repository.observeWithUsage().first().associateBy { it.paymentMethod.id }
        assertEquals(2, afterInsert[cashId]?.transactionCount)
        assertTrue(afterInsert[cashId]!!.isUsed)
        assertEquals(1, afterInsert[transferId]?.transactionCount)
        assertTrue(afterInsert[transferId]!!.isUsed)
        assertEquals(0, afterInsert[click.id]?.transactionCount)
        assertFalse(afterInsert[click.id]!!.isUsed)

        // Move tx2 from Cash to Click
        val tx2Entity = transactionDao.getById(tx2)!!
        transactionDao.update(tx2Entity.copy(paymentMethodId = click.id))

        val afterUpdate = repository.observeWithUsage().first().associateBy { it.paymentMethod.id }
        assertEquals(1, afterUpdate[cashId]?.transactionCount)
        assertEquals(1, afterUpdate[transferId]?.transactionCount)
        assertEquals(1, afterUpdate[click.id]?.transactionCount)

        // Delete tx1 -> Cash count becomes 0
        transactionDao.deleteById(tx1)
        val afterDelete = repository.observeWithUsage().first().associateBy { it.paymentMethod.id }
        assertEquals(0, afterDelete[cashId]?.transactionCount)
        assertFalse(afterDelete[cashId]!!.isUsed)
    }

    private suspend fun insertTxWithRate(
        type: TransactionType,
        amount: BigDecimal,
        currencyId: Long,
        categoryId: Long,
        paymentMethodId: Long,
        date: LocalDate,
        createdAt: Long,
        uzsRate: BigDecimal = BigDecimal.ONE,
    ): Long {
        val id = transactionDao.insert(
            TransactionEntity(
                type = type,
                amount = amount,
                currencyId = currencyId,
                categoryId = categoryId,
                paymentMethodId = paymentMethodId,
                date = date,
                createdAt = createdAt,
                description = "",
            )
        )
        val rates = mutableListOf(TransactionRateEntity(id, currencyId, BigDecimal.ONE))
        if (currencyId != uzsId) {
            rates.add(TransactionRateEntity(id, uzsId, uzsRate))
        }
        transactionDao.insertSnapshot(rates)
        return id
    }

    private suspend fun loadPagingRows(
        categoryId: Long? = null,
        currencyId: Long? = null,
        paymentMethodId: Long? = null,
        type: TransactionType? = null,
        fromDate: LocalDate? = null,
        toDate: LocalDate? = null,
    ): List<TransactionListRow> {
        val source = transactionDao.paging(
            categoryId = categoryId,
            currencyId = currencyId,
            paymentMethodId = paymentMethodId,
            type = type,
            fromDate = fromDate,
            toDate = toDate,
            displayCurrencyId = uzsId,
            fallbackCurrencyId = uzsId,
        )
        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 50,
                placeholdersEnabled = false,
            )
        )
        return (result as PagingSource.LoadResult.Page).data
    }
}
