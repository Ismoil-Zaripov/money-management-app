package uz.moneymanager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import uz.moneymanager.ui.categories.CategoriesScreen
import uz.moneymanager.ui.currencies.CurrenciesScreen
import uz.moneymanager.ui.navigation.AppBottomNavItems
import uz.moneymanager.ui.navigation.BottomBar
import uz.moneymanager.ui.navigation.Screen
import uz.moneymanager.ui.paymentmethods.PaymentMethodsScreen
import uz.moneymanager.ui.rates.RatesScreen
import uz.moneymanager.ui.settings.SettingsScreen
import uz.moneymanager.core.designsystem.theme.MoneyManagerTheme
import uz.moneymanager.core.designsystem.theme.SystemBarsEffect
import uz.moneymanager.core.designsystem.theme.barContainer
import uz.moneymanager.ui.transactions.TransactionsScreen
import uz.moneymanager.ui.transactions.form.TransactionFormScreen

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { viewModel.settings.value == null }
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            settings?.let { s ->
                MoneyManagerTheme(
                    themeMode = s.themeMode,
                    palette = s.palette,
                    contentFontSize = s.contentFontSize
                ) {
                    MainAppContent()
                }
            }
        }
    }
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = AppBottomNavItems.any { it.screen.route == currentRoute }

    // Dynamic system bars color
    val barColor = MaterialTheme.colorScheme.barContainer
    val navBarColor = if (showBottomBar) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface
    SystemBarsEffect(statusBarBackground = barColor, navBarBackground = navBarColor)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Transactions.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(120)) },
            exitTransition = { fadeOut(animationSpec = tween(90)) },
            popEnterTransition = { fadeIn(animationSpec = tween(120)) },
            popExitTransition = { fadeOut(animationSpec = tween(90)) },
        ) {
            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    onNavigateToCreate = { navController.navigate(Screen.TransactionForm.createRoute()) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.TransactionForm.createRoute(id)) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateCurrencies = { navController.navigate(Screen.Currencies.route) },
                    onNavigateRates = { navController.navigate(Screen.Rates.route) },
                    onNavigateCategories = { navController.navigate(Screen.Categories.route) },
                    onNavigatePaymentMethods = { navController.navigate(Screen.PaymentMethods.route) }
                )
            }

            composable(
                route = Screen.TransactionForm.route,
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }),
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
            ) {
                TransactionFormScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Currencies.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
            ) {
                CurrenciesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Rates.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
            ) {
                RatesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Categories.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
            ) {
                CategoriesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.PaymentMethods.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, animationSpec = tween(180)) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(180)) },
            ) {
                PaymentMethodsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
