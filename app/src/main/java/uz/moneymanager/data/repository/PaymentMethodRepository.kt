package uz.moneymanager.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import uz.moneymanager.R
import uz.moneymanager.data.local.AppDatabase
import uz.moneymanager.data.local.PaymentMethodDao
import uz.moneymanager.data.local.PaymentMethodEntity
import uz.moneymanager.data.local.PaymentMethodWithUsage
import uz.moneymanager.domain.DomainException
import uz.moneymanager.domain.PaymentMethodNames
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentMethodRepository @Inject constructor(
    private val db: AppDatabase,
    private val dao: PaymentMethodDao,
) {
    fun observeAll(): Flow<List<PaymentMethodEntity>> = dao.observeAll()

    fun observeWithUsage(): Flow<List<PaymentMethodWithUsage>> = dao.observeWithUsage()

    suspend fun getById(id: Long): PaymentMethodEntity? = dao.getById(id)

    suspend fun getDefault(): PaymentMethodEntity? = dao.getDefault()

    suspend fun add(name: String): PaymentMethodEntity = db.withTransaction {
        val normalized = validate(name)
        val key = PaymentMethodNames.key(normalized)
        val existing = dao.findByKey(key)
        if (existing != null) {
            throw DomainException(R.string.err_payment_method_duplicate, existing.name)
        }
        val entity = PaymentMethodEntity(name = normalized, nameKey = key, isDefault = false)
        val id = dao.insert(entity)
        entity.copy(id = id)
    }

    suspend fun findOrCreate(name: String): PaymentMethodEntity = db.withTransaction {
        val normalized = validate(name)
        val key = PaymentMethodNames.key(normalized)
        dao.findByKey(key) ?: PaymentMethodEntity(name = normalized, nameKey = key).let {
            it.copy(id = dao.insert(it))
        }
    }

    suspend fun rename(id: Long, newName: String) = db.withTransaction {
        val existing = dao.getById(id) ?: throw DomainException(R.string.err_payment_method_not_found)
        val normalized = validate(newName)
        val key = PaymentMethodNames.key(normalized)
        val other = dao.findByKey(key)
        if (other != null && other.id != id) {
            throw DomainException(R.string.err_payment_method_duplicate, other.name)
        }
        dao.update(existing.copy(name = normalized, nameKey = key))
    }

    suspend fun delete(id: Long) = db.withTransaction {
        val existing = dao.getById(id) ?: return@withTransaction
        if (existing.isDefault) {
            throw DomainException(R.string.err_payment_method_default_locked)
        }
        if (dao.usageCount(id) > 0) {
            throw DomainException(R.string.err_payment_method_in_use, existing.name)
        }
        dao.deleteById(id)
    }

    private fun validate(name: String): String {
        val normalized = PaymentMethodNames.normalize(name)
        if (normalized.isEmpty()) throw DomainException(R.string.err_payment_method_required)
        if (normalized.length > PaymentMethodNames.MAX_LENGTH) {
            throw DomainException(R.string.err_payment_method_too_long)
        }
        return normalized
    }
}
