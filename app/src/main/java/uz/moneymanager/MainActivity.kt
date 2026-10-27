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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import uz.moneymanager.ui.navigation.BottomNavItem
import uz.moneymanager.ui.navigation.Screen
import uz.moneymanager.ui.rates.RatesScreen
import uz.moneymanager.ui.settings.SettingsScreen
import uz.moneymanager.ui.theme.MoneyManagerTheme
import uz.moneymanager.ui.theme.SystemBarsEffect
import uz.moneymanager.ui.theme.barContainer
import uz.moneymanager.ui.theme.onBarContainer
import uz.moneymanager.ui.transactions.TransactionsScreen
import uz.moneymanager.ui.transactions.form.TransactionFormScreen

private val AppBottomNavItems = listOf(
    BottomNavItem.Transactions,
    BottomNavItem.Settings,
)

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
    val navBarColor = if (showBottomBar) barColor else MaterialTheme.colorScheme.surface
    SystemBarsEffect(statusBarBackground = barColor, navBarBackground = navBarColor)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                Column(
                    modifier = Modifier.background(barColor)
                ) {
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBarContainer.copy(alpha = 0.2f))
                    NavigationBar(
                        containerColor = barColor,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        AppBottomNavItems.forEach { item ->
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
                                label = { Text(stringResource(item.titleRes)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = barColor,
                                    selectedTextColor = MaterialTheme.colorScheme.onBarContainer,
                                    indicatorColor = MaterialTheme.colorScheme.onBarContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onBarContainer.copy(alpha = 0.7f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onBarContainer.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
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
                    onNavigateCategories = { navController.navigate(Screen.Categories.route) }
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
        }
    }
}
