package uz.moneymanager.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import uz.moneymanager.R
import uz.moneymanager.data.local.AppDatabase
import uz.moneymanager.data.local.CategoryDao
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CategoryWithUsage
import uz.moneymanager.domain.CategoryNames
import uz.moneymanager.domain.DomainException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val db: AppDatabase,
    private val dao: CategoryDao,
) {
    fun observeAll(): Flow<List<CategoryEntity>> = dao.observeAll()

    fun observeWithUsage(): Flow<List<CategoryWithUsage>> = dao.observeWithUsage()

    suspend fun getById(id: Long): CategoryEntity? = dao.getById(id)

    /** Returns the existing category with the same (case-insensitive) name, or creates it. */
    suspend fun findOrCreate(name: String): CategoryEntity = db.withTransaction {
        val normalized = validate(name)
        val key = CategoryNames.key(normalized)
        dao.findByKey(key) ?: CategoryEntity(name = normalized, nameKey = key).let {
            it.copy(id = dao.insert(it))
        }
    }

    suspend fun rename(id: Long, newName: String) = db.withTransaction {
        val existing = dao.getById(id) ?: return@withTransaction
        val normalized = validate(newName)
        val key = CategoryNames.key(normalized)
        val other = dao.findByKey(key)
        if (other != null && other.id != id) throw DomainException(R.string.err_category_duplicate, other.name)
        dao.update(existing.copy(name = normalized, nameKey = key))
    }

    suspend fun delete(id: Long) = db.withTransaction {
        val existing = dao.getById(id) ?: return@withTransaction
        if (dao.usageCount(id) > 0) throw DomainException(R.string.err_category_in_use, existing.name)
        dao.deleteById(id)
    }

    private fun validate(name: String): String {
        val normalized = CategoryNames.normalize(name)
        if (normalized.isEmpty()) throw DomainException(R.string.err_category_required)
        if (normalized.length > CategoryNames.MAX_LENGTH) throw DomainException(R.string.err_category_too_long)
        return normalized
    }
}
