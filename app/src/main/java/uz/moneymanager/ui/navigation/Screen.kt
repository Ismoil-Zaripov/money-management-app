package uz.moneymanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import uz.moneymanager.R

sealed class Screen(val route: String) {
    data object Transactions : Screen("transactions")
    data object Monitoring : Screen("monitoring")
    data object Settings : Screen("settings")
    data object TransactionForm : Screen("transaction_form?id={id}") {
        fun createRoute(id: Long? = null) = if (id != null) "transaction_form?id=$id" else "transaction_form"
    }
    data object Currencies : Screen("currencies")
    data object Rates : Screen("rates")
    data object Categories : Screen("categories")
}

sealed class BottomNavItem(
    val screen: Screen,
    val titleRes: Int,
    val icon: ImageVector,
) {
    data object Transactions : BottomNavItem(Screen.Transactions, R.string.nav_transactions, Icons.Default.List)
    data object Monitoring : BottomNavItem(Screen.Monitoring, R.string.nav_monitoring, Icons.Default.BarChart)
    data object Settings : BottomNavItem(Screen.Settings, R.string.nav_settings, Icons.Default.Settings)
}
