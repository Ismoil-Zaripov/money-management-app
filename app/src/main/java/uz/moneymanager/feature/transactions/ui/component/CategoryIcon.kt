package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Locale

private class IconRule(val icon: ImageVector, val keywords: List<String>)

/** Category has no icon field in the DB: pick a small icon from its normalized name key (uz/en/ru keywords). */
private val iconRules = listOf(
    IconRule(Icons.Default.Payments, listOf("salary", "maosh", "oylik", "зарплат", "bonus", "freelance", "income", "daromad", "доход")),
    IconRule(Icons.Default.Restaurant, listOf("food", "ovqat", "oziq", "restoran", "cafe", "kafe", "еда", "питани", "кафе")),
    IconRule(Icons.Default.ShoppingCart, listOf("grocer", "market", "produkt", "продукт", "магазин", "korzinka")),
    IconRule(Icons.Default.DirectionsCar, listOf("taxi", "taksi", "такси", "transport", "транспорт", "avto", "fuel", "benzin", "бензин")),
    IconRule(Icons.Default.Home, listOf("rent", "ijara", "аренд", "home", "kommunal", "коммунал", "дом")),
    IconRule(Icons.Default.MedicalServices, listOf("health", "salomat", "dori", "здоров", "аптек", "pharm", "medic")),
    IconRule(Icons.Default.School, listOf("educat", "talim", "ta'lim", "образован", "kurs", "kitob", "book")),
    IconRule(Icons.Default.Movie, listOf("entertain", "kino", "cinema", "развлеч", "subscri", "obuna", "подписк")),
    IconRule(Icons.Default.ShoppingBag, listOf("shop", "kiyim", "одежд", "cloth", "gift", "sovg", "подар")),
    IconRule(Icons.Default.Wifi, listOf("internet", "mobile", "telefon", "aloqa", "связ")),
)

fun categoryIcon(categoryKey: String): ImageVector {
    val key = categoryKey.lowercase(Locale.ROOT)
    return iconRules.firstOrNull { rule -> rule.keywords.any { key.contains(it) } }?.icon ?: Icons.Default.Category
}
