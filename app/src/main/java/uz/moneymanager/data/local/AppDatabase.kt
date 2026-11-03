package uz.moneymanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CurrencyEntity::class,
        CategoryEntity::class,
        ExchangeRateEntity::class,
        PaymentMethodEntity::class,
        TransactionEntity::class,
        TransactionRateEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun categoryDao(): CategoryDao
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val NAME = "money_manager.db"

        /** v2: optional transaction description; existing rows get '' (no placeholder text). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }

        /** v3: dynamic payment methods with default 'Cash' and 'Transfer'. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create payment_methods table and default rows
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `payment_methods` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `nameKey` TEXT NOT NULL,
                        `isDefault` INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_payment_methods_nameKey` ON `payment_methods` (`nameKey`)")
                db.execSQL("INSERT OR IGNORE INTO `payment_methods` (`id`, `name`, `nameKey`, `isDefault`) VALUES (1, 'Cash', 'cash', 1)")
                db.execSQL("INSERT OR IGNORE INTO `payment_methods` (`id`, `name`, `nameKey`, `isDefault`) VALUES (2, 'Transfer', 'transfer', 0)")

                // 2. Create transactions_new table with all foreign keys
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `transactions_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `type` TEXT NOT NULL,
                        `amount` TEXT NOT NULL,
                        `currencyId` INTEGER NOT NULL,
                        `categoryId` INTEGER NOT NULL,
                        `paymentMethodId` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `description` TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(`currencyId`) REFERENCES `currencies`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT,
                        FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT,
                        FOREIGN KEY(`paymentMethodId`) REFERENCES `payment_methods`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )

                // 3. Copy data from transactions into transactions_new
                val cursor = db.query("PRAGMA table_info(`transactions`)")
                var hasPaymentMethodId = false
                try {
                    val nameIdx = cursor.getColumnIndex("name")
                    while (cursor.moveToNext()) {
                        if (cursor.getString(nameIdx).equals("paymentMethodId", ignoreCase = true)) {
                            hasPaymentMethodId = true
                            break
                        }
                    }
                } finally {
                    cursor.close()
                }

                val paymentMethodExpr = if (hasPaymentMethodId) "`paymentMethodId`" else "1"
                db.execSQL(
                    """
                    INSERT INTO `transactions_new` (
                        `id`, `type`, `amount`, `currencyId`, `categoryId`, `paymentMethodId`, `date`, `createdAt`, `description`
                    )
                    SELECT `id`, `type`, `amount`, `currencyId`, `categoryId`, $paymentMethodExpr, `date`, `createdAt`, `description`
                    FROM `transactions`
                    """.trimIndent()
                )

                // 4. Drop old table and rename new table
                db.execSQL("DROP TABLE `transactions`")
                db.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")

                // 5. Recreate indices on transactions
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_currencyId` ON `transactions` (`currencyId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_categoryId` ON `transactions` (`categoryId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_paymentMethodId` ON `transactions` (`paymentMethodId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_date` ON `transactions` (`date`)")
            }
        }

        /** Seeds the national default currency and default payment methods on first launch. */
        val SEED_CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "INSERT INTO currencies (code, name, isDefault) VALUES ('UZS', 'Uzbek sum', 1)"
                )
                db.execSQL(
                    "INSERT INTO payment_methods (id, name, nameKey, isDefault) VALUES (1, 'Cash', 'cash', 1)"
                )
                db.execSQL(
                    "INSERT INTO payment_methods (id, name, nameKey, isDefault) VALUES (2, 'Transfer', 'transfer', 0)"
                )
            }
        }
    }
}
