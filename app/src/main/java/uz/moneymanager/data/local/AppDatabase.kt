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
        TransactionEntity::class,
        TransactionRateEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun categoryDao(): CategoryDao
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val NAME = "money_manager.db"

        /** v2: optional transaction description; existing rows get '' (no placeholder text). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }

        /** Seeds the national default currency on first launch. */
        val SEED_CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "INSERT INTO currencies (code, name, isDefault) VALUES ('UZS', 'Uzbek sum', 1)"
                )
            }
        }
    }
}
