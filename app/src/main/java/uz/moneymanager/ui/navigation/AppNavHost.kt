package uz.moneymanager.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.moneymanager.feature.categories.ui.CategoriesRoute
import uz.moneymanager.feature.currencies.ui.CurrenciesRoute
import uz.moneymanager.feature.paymentmethods.ui.PaymentMethodsRoute
import uz.moneymanager.feature.rates.ui.RatesRoute
import uz.moneymanager.feature.transactionform.ui.TransactionFormRoute

private const val SLIDE_MS = 300

/**
 * Overlay layer drawn above the always-alive tabs. Its start destination is empty (the tabs show
 * through); every other page rises from the bottom like a bottom sheet and leaves downwards.
 * The NavHost still owns the back stack and the per-page ViewModel scope and arguments.
 */
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Tabs.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(Screen.Tabs.route) { /* tabs are drawn by TabsHost beneath this NavHost */ }
        slideComposable(
            route = Screen.TransactionForm.route,
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }),
        ) { TransactionFormRoute(onNavigateBack = { navController.popBackStack() }) }
        slideComposable(Screen.Currencies.route) { CurrenciesRoute(onNavigateBack = { navController.popBackStack() }) }
        slideComposable(Screen.Rates.route) { RatesRoute(onNavigateBack = { navController.popBackStack() }) }
        slideComposable(Screen.Categories.route) { CategoriesRoute(onNavigateBack = { navController.popBackStack() }) }
        slideComposable(Screen.PaymentMethods.route) { PaymentMethodsRoute(onNavigateBack = { navController.popBackStack() }) }
    }
}

private fun NavGraphBuilder.slideComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable () -> Unit,
) {
    val up = AnimatedContentTransitionScope.SlideDirection.Up
    val down = AnimatedContentTransitionScope.SlideDirection.Down
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { slideIntoContainer(up, animationSpec = tween(SLIDE_MS)) },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { slideOutOfContainer(down, animationSpec = tween(SLIDE_MS)) },
    ) { content() }
}
