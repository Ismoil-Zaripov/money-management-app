package uz.moneymanager.feature.categories.presentation

import uz.moneymanager.data.local.CategoryWithUsage

/** Case-insensitive "contains" search over the category name; blank query keeps everything. */
fun filterCategories(items: List<CategoryWithUsage>, query: String): List<CategoryWithUsage> {
    val q = query.trim()
    if (q.isEmpty()) return items
    return items.filter { it.category.name.contains(q, ignoreCase = true) }
}
