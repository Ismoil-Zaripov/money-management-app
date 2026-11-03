package uz.moneymanager.data.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration2To3Test {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
    )

    @After
    fun tearDown() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(TEST_DB)
        context.deleteDatabase(FRESH_DB)
    }

    @Test
    fun migrate2To3_emptyDatabase_createsCashAndTransferDefaults() {
        helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO currencies (id, code, name, isDefault) VALUES (1, 'UZS', 'Uzbek sum', 1)")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3)

        db.query("SELECT id, name, nameKey, isDefault FROM payment_methods ORDER BY id ASC").use { cursor ->
            assertEquals(2, cursor.count)

            assertTrue(cursor.moveToFirst())
            assertEquals(1L, cursor.getLong(0))
            assertEquals("Cash", cursor.getString(1))
            assertEquals("cash", cursor.getString(2))
            assertEquals(1, cursor.getInt(3))

            assertTrue(cursor.moveToNext())
            assertEquals(2L, cursor.getLong(0))
            assertEquals("Transfer", cursor.getString(1))
            assertEquals("transfer", cursor.getString(2))
            assertEquals(0, cursor.getInt(3))
        }

        db.query("SELECT COUNT(*) FROM transactions").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(0, cursor.getInt(0))
        }

        db.close()
    }

    @Test
    fun migrate2To3_withExistingTransactions_preservesAllDataAndLinksToCash() {
        helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO currencies (id, code, name, isDefault) VALUES (1, 'UZS', 'Uzbek sum', 1)")
            execSQL("INSERT INTO currencies (id, code, name, isDefault) VALUES (2, 'USD', 'US Dollar', 0)")
            execSQL("INSERT INTO categories (id, name, nameKey) VALUES (10, 'Oziq-ovqat', 'oziq-ovqat')")
            execSQL("INSERT INTO categories (id, name, nameKey) VALUES (20, 'Maosh', 'maosh')")

            execSQL(
                """
                INSERT INTO transactions (id, type, amount, currencyId, categoryId, date, createdAt, description)
                VALUES (101, 'EXPENSE', '125000.50', 1, 10, 20000, 1700000001000, 'Bozordan xarid')
                """.trimIndent()
            )
            execSQL(
                """
                INSERT INTO transactions (id, type, amount, currencyId, categoryId, date, createdAt, description)
                VALUES (102, 'INCOME', '500.00', 2, 20, 20001, 1700000002000, '')
                """.trimIndent()
            )

            execSQL("INSERT INTO transaction_rates (transactionId, quoteCurrencyId, rate) VALUES (101, 1, '1')")
            execSQL("INSERT INTO transaction_rates (transactionId, quoteCurrencyId, rate) VALUES (102, 2, '1')")
            execSQL("INSERT INTO transaction_rates (transactionId, quoteCurrencyId, rate) VALUES (102, 1, '12800')")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3)

        db.query(
            """
            SELECT id, type, amount, currencyId, categoryId, paymentMethodId, date, createdAt, description
            FROM transactions ORDER BY id ASC
            """.trimIndent()
        ).use { cursor ->
            assertEquals(2, cursor.count)

            assertTrue(cursor.moveToFirst())
            assertEquals(101L, cursor.getLong(0))
            assertEquals("EXPENSE", cursor.getString(1))
            assertEquals("125000.50", cursor.getString(2))
            assertEquals(1L, cursor.getLong(3))
            assertEquals(10L, cursor.getLong(4))
            assertEquals(1L, cursor.getLong(5)) // Cash id=1
            assertEquals(20000L, cursor.getLong(6))
            assertEquals(1700000001000L, cursor.getLong(7))
            assertEquals("Bozordan xarid", cursor.getString(8))

            assertTrue(cursor.moveToNext())
            assertEquals(102L, cursor.getLong(0))
            assertEquals("INCOME", cursor.getString(1))
            assertEquals("500.00", cursor.getString(2))
            assertEquals(2L, cursor.getLong(3))
            assertEquals(20L, cursor.getLong(4))
            assertEquals(1L, cursor.getLong(5)) // Cash id=1
            assertEquals(20001L, cursor.getLong(6))
            assertEquals(1700000002000L, cursor.getLong(7))
            assertEquals("", cursor.getString(8))
        }

        // Verify snapshot rates in transaction_rates were not lost during table recreation
        db.query("SELECT COUNT(*) FROM transaction_rates").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(3, cursor.getInt(0))
        }

        db.close()
    }

    @Test
    fun migrate2To3_manyTransactions_allLinkedToCashId1() = runBlocking {
        val totalCount = 50
        helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO currencies (id, code, name, isDefault) VALUES (1, 'UZS', 'Uzbek sum', 1)")
            execSQL("INSERT INTO categories (id, name, nameKey) VALUES (1, 'Transport', 'transport')")
            for (i in 1..totalCount) {
                val type = if (i % 2 == 0) "INCOME" else "EXPENSE"
                val amount = "${i * 1000}.00"
                val date = 19000 + i
                val createdAt = 1700000000000L + i
                val desc = "Tx #$i"
                execSQL(
                    """
                    INSERT INTO transactions (id, type, amount, currencyId, categoryId, date, createdAt, description)
                    VALUES ($i, '$type', '$amount', 1, 1, $date, $createdAt, '$desc')
                    """.trimIndent()
                )
            }
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3).close()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val roomDb = Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB)
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()

        try {
            val paymentMethods = roomDb.paymentMethodDao().getAll()
            assertEquals(2, paymentMethods.size)
            val cash = paymentMethods.first { it.id == 1L }
            val transfer = paymentMethods.first { it.id == 2L }
            assertEquals("Cash", cash.name)
            assertTrue(cash.isDefault)
            assertEquals("Transfer", transfer.name)
            assertFalse(transfer.isDefault)

            assertEquals(totalCount, roomDb.paymentMethodDao().usageCount(1L))
            assertEquals(0, roomDb.paymentMethodDao().usageCount(2L))

            // Spot-check first and last transaction via DAO
            val firstTx = roomDb.transactionDao().getById(1L)
            assertNotNull(firstTx)
            assertEquals(1L, firstTx!!.paymentMethodId)
            assertEquals("Tx #1", firstTx.description)

            val lastTx = roomDb.transactionDao().getById(totalCount.toLong())
            assertNotNull(lastTx)
            assertEquals(1L, lastTx!!.paymentMethodId)
            assertEquals("Tx #$totalCount", lastTx.description)
        } finally {
            roomDb.close()
        }
    }

    @Test
    fun freshInstall_seedCallback_matchesMigratedDefaultPaymentMethods() = runBlocking {
        // 1. Migrated DB payment methods
        helper.createDatabase(TEST_DB, 2).close()
        helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3).close()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val migratedDb = Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB)
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()

        // 2. Fresh install DB with SEED_CALLBACK
        val freshDb = Room.databaseBuilder(context, AppDatabase::class.java, FRESH_DB)
            .addCallback(AppDatabase.SEED_CALLBACK)
            .build()

        try {
            val migratedMethods = migratedDb.paymentMethodDao().getAll()
            val freshMethods = freshDb.paymentMethodDao().getAll()

            assertEquals(migratedMethods, freshMethods)
            assertEquals(2, freshMethods.size)

            val defaultPm = freshDb.paymentMethodDao().getDefault()
            assertNotNull(defaultPm)
            assertEquals(1L, defaultPm!!.id)
            assertEquals("Cash", defaultPm.name)
            assertEquals("cash", defaultPm.nameKey)
            assertTrue(defaultPm.isDefault)

            val transferPm = freshDb.paymentMethodDao().getById(2L)
            assertNotNull(transferPm)
            assertEquals("Transfer", transferPm!!.name)
            assertEquals("transfer", transferPm.nameKey)
            assertFalse(transferPm.isDefault)

            val currencies = freshDb.currencyDao().getAll()
            assertEquals(1, currencies.size)
            assertEquals("UZS", currencies.first().code)
            assertTrue(currencies.first().isDefault)
        } finally {
            migratedDb.close()
            freshDb.close()
        }
    }

    companion object {
        private const val TEST_DB = "migration_2_3_test.db"
        private const val FRESH_DB = "fresh_install_3_test.db"
    }
}
