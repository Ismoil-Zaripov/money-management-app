package uz.moneymanager.feature.categories

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CategoryWithUsage
import uz.moneymanager.feature.categories.presentation.filterCategories

class CategoryFilterTest {

    private val items = listOf(
        item(1, "Groceries", 42),
        item(2, "Salary", 18),
        item(3, "Taxi & Transport", 0),
    )

    @Test
    fun blankQuery_returnsSameList() {
        assertSame(items, filterCategories(items, ""))
        assertSame(items, filterCategories(items, "   "))
    }

    @Test
    fun query_matchesIgnoringCase_andTrims() {
        assertEquals(listOf(2L), filterCategories(items, "  SAL ").map { it.category.id })
    }

    @Test
    fun query_matchesSubstring() {
        assertEquals(listOf(1L, 3L), filterCategories(items, "o").map { it.category.id })
    }

    @Test
    fun noMatch_returnsEmpty() {
        assertEquals(emptyList<CategoryWithUsage>(), filterCategories(items, "zzz"))
    }

    private fun item(id: Long, name: String, count: Int) =
        CategoryWithUsage(CategoryEntity(id = id, name = name, nameKey = name.lowercase()), count)
}
