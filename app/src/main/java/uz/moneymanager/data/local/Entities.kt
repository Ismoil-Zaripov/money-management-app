package uz.moneymanager.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

@Entity(
    tableName = "currencies",
    indices = [Index(value = ["code"], unique = true)],
)
data class CurrencyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    /** National currency (UZS). Exactly one row, can never be deleted. */
    @ColumnInfo(defaultValue = "0") val isDefault: Boolean = false,
)

@Entity(
    tableName = "categories",
    indices = [Index(value = ["nameKey"], unique = true)],
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** Normalized lower-case name used for uniqueness. */
    val nameKey: String,
)

/** 1 [baseCurrencyId] = [rate] [quoteCurrencyId] on [date]. Always stored together with its reverse. */
@Entity(
    tableName = "exchange_rates",
    foreignKeys = [
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["baseCurrencyId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["quoteCurrencyId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["date", "baseCurrencyId", "quoteCurrencyId"], unique = true),
        Index("baseCurrencyId"),
        Index("quoteCurrencyId"),
    ],
)
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val baseCurrencyId: Long,
    val quoteCurrencyId: Long,
    val rate: BigDecimal,
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["currencyId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("currencyId"), Index("categoryId"), Index("date")],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amount: BigDecimal,
    val currencyId: Long,
    val categoryId: Long,
    val date: LocalDate,
    val createdAt: Long,
    /** Optional free text; empty string when not provided (column is NOT NULL DEFAULT ''). */
    @ColumnInfo(defaultValue = "") val description: String = "",
)

/**
 * Snapshot of the rates valid when the transaction was saved:
 * 1 transaction currency = [rate] [quoteCurrencyId] (includes itself with rate 1).
 */
@Entity(
    tableName = "transaction_rates",
    primaryKeys = ["transactionId", "quoteCurrencyId"],
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["id"],
            childColumns = ["quoteCurrencyId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("quoteCurrencyId")],
)
data class TransactionRateEntity(
    val transactionId: Long,
    val quoteCurrencyId: Long,
    val rate: BigDecimal,
)

// ---------- Query projections ----------

data class TransactionListRow(
    val id: Long,
    val type: TransactionType,
    val amount: BigDecimal,
    val date: LocalDate,
    val currencyCode: String,
    val categoryName: String,
    val description: String,
    /** Snapshot rate to the display currency, null if the currency did not exist yet. */
    val displayRate: BigDecimal?,
    /** Snapshot rate to the national currency (UZS). */
    val fallbackRate: BigDecimal?,
)

data class TotalsRow(
    val type: TransactionType,
    val amount: BigDecimal,
    val displayRate: BigDecimal?,
    val fallbackRate: BigDecimal?,
)

data class DailyTotalsRow(
    val date: LocalDate,
    val type: TransactionType,
    val amount: BigDecimal,
    val displayRate: BigDecimal?,
    val fallbackRate: BigDecimal?,
)

data class CurrencyWithUsage(
    @Embedded val currency: CurrencyEntity,
    val transactionCount: Int,
    val rateCount: Int,
) {
    val isUsed: Boolean get() = transactionCount > 0 || rateCount > 0
}

data class CategoryWithUsage(
    @Embedded val category: CategoryEntity,
    val transactionCount: Int,
)

data class ExchangeRateRow(
    val id: Long,
    val date: LocalDate,
    val baseCurrencyId: Long,
    val quoteCurrencyId: Long,
    val rate: BigDecimal,
    val baseCode: String,
    val quoteCode: String,
    /** True when a transaction snapshot already uses this pair – then it is read-only. */
    val locked: Boolean,
)
