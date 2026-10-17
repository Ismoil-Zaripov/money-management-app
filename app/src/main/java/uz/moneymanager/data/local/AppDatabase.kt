package uz.moneymanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        CurrencyEntity::class,
        CategoryEntity::class,
        ExchangeRateEntity::class,
        TransactionEntity::class,
        TransactionRateEntity::class,
    ],
    version = 1,
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
