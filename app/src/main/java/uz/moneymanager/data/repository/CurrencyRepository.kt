package uz.moneymanager.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import uz.moneymanager.R
import uz.moneymanager.data.local.AppDatabase
import uz.moneymanager.data.local.CurrencyDao
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.CurrencyWithUsage
import uz.moneymanager.domain.CurrencyCodes
import uz.moneymanager.domain.DomainException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val db: AppDatabase,
    private val dao: CurrencyDao,
) {
    fun observeAll(): Flow<List<CurrencyEntity>> = dao.observeAll()

    fun observeWithUsage(): Flow<List<CurrencyWithUsage>> = dao.observeWithUsage()

    suspend fun add(code: String, name: String) = db.withTransaction {
        val normalized = validate(code, name)
        if (dao.findByCode(normalized) != null) {
            throw DomainException(R.string.err_currency_duplicate, normalized)
        }
        dao.insert(CurrencyEntity(code = normalized, name = name.trim()))
    }

    suspend fun update(id: Long, code: String, name: String) = db.withTransaction {
        val existing = dao.getById(id) ?: return@withTransaction
        val normalized = validate(code, name)
        if (normalized != existing.code) {
            if (existing.isDefault) throw DomainException(R.string.err_currency_default_locked)
            if (dao.usageCount(id) > 0) throw DomainException(R.string.err_currency_code_in_use)
            val other = dao.findByCode(normalized)
            if (other != null && other.id != id) {
                throw DomainException(R.string.err_currency_duplicate, normalized)
            }
        }
        dao.update(existing.copy(code = normalized, name = name.trim()))
    }

    suspend fun delete(id: Long) = db.withTransaction {
        val existing = dao.getById(id) ?: return@withTransaction
        if (existing.isDefault) throw DomainException(R.string.err_currency_default_locked)
        if (dao.usageCount(id) > 0) throw DomainException(R.string.err_currency_in_use, existing.code)
        dao.deleteById(id)
    }

    private fun validate(code: String, name: String): String {
        val normalized = CurrencyCodes.normalize(code)
        if (!CurrencyCodes.isValid(normalized)) throw DomainException(R.string.err_currency_code_invalid)
        if (name.isBlank()) throw DomainException(R.string.err_currency_name_required)
        return normalized
    }
}
