package uz.moneymanager.data.local

import androidx.room.TypeConverter
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

/** Money is stored as TEXT (exact BigDecimal), dates as epoch-day INTEGER (sortable/filterable). */
class Converters {
    @TypeConverter fun fromDate(value: LocalDate?): Long? = value?.toEpochDay()
    @TypeConverter fun toDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter fun fromDecimal(value: BigDecimal?): String? = value?.toPlainString()
    @TypeConverter fun toDecimal(value: String?): BigDecimal? = value?.let(::BigDecimal)

    @TypeConverter fun fromType(value: TransactionType?): String? = value?.name
    @TypeConverter fun toType(value: String?): TransactionType? = value?.let(TransactionType::valueOf)
}
