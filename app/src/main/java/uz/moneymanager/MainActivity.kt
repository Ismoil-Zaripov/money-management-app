package uz.moneymanager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import uz.moneymanager.data.repository.SettingsRepository
import uz.moneymanager.domain.model.ThemeMode
import uz.moneymanager.ui.categories.CategoriesScreen
import uz.moneymanager.ui.currencies.CurrenciesScreen
import uz.moneymanager.ui.monitoring.MonitoringScreen
import uz.moneymanager.ui.navigation.BottomNavItem
import uz.moneymanager.ui.navigation.Screen
import uz.moneymanager.ui.rates.RatesScreen
import uz.moneymanager.ui.settings.SettingsScreen
import uz.moneymanager.ui.theme.MoneyManagerTheme
import uz.moneymanager.ui.transactions.TransactionsScreen
import uz.moneymanager.ui.transactions.form.TransactionFormScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by settingsRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            MoneyManagerTheme(themeMode = themeMode) {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Transactions,
        BottomNavItem.Monitoring,
        BottomNavItem.Settings,
    )

    val showBottomBar = bottomNavItems.any { it.screen.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = stringResource(item.titleRes)) },
                            label = { Text(stringResource(item.titleRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Transactions.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    onNavigateToCreate = { navController.navigate(Screen.TransactionForm.createRoute()) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.TransactionForm.createRoute(id)) }
                )
            }

            composable(Screen.Monitoring.route) {
                MonitoringScreen(
                    onNavigateToEdit = { id -> navController.navigate(Screen.TransactionForm.createRoute(id)) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateCurrencies = { navController.navigate(Screen.Currencies.route) },
                    onNavigateRates = { navController.navigate(Screen.Rates.route) },
                    onNavigateCategories = { navController.navigate(Screen.Categories.route) }
                )
            }

            composable(
                route = Screen.TransactionForm.route,
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) {
                TransactionFormScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Currencies.route) {
                CurrenciesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Rates.route) {
                RatesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Categories.route) {
                CategoriesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
