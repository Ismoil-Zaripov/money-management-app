package uz.moneymanager.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import uz.moneymanager.feature.settings.ui.SettingsRoute
import uz.moneymanager.feature.transactionform.ui.TransactionFormRoute
import uz.moneymanager.feature.transactions.ui.TransactionsRoute

private const val FADE_IN_MS = 120
private const val FADE_OUT_MS = 90
private const val SLIDE_MS = 180

/** Top-level tabs fade; every secondary screen slides in from the end. */
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Transactions.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(FADE_IN_MS)) },
        exitTransition = { fadeOut(animationSpec = tween(FADE_OUT_MS)) },
        popEnterTransition = { fadeIn(animationSpec = tween(FADE_IN_MS)) },
        popExitTransition = { fadeOut(animationSpec = tween(FADE_OUT_MS)) },
    ) {
        composable(Screen.Transactions.route) {
            TransactionsRoute(
                onNavigateToCreate = { navController.navigate(Screen.TransactionForm.createRoute()) },
                onNavigateToEdit = { id -> navController.navigate(Screen.TransactionForm.createRoute(id)) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsRoute(
                onNavigateCurrencies = { navController.navigate(Screen.Currencies.route) },
                onNavigateRates = { navController.navigate(Screen.Rates.route) },
                onNavigateCategories = { navController.navigate(Screen.Categories.route) },
                onNavigatePaymentMethods = { navController.navigate(Screen.PaymentMethods.route) }
            )
        }
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
    val start = AnimatedContentTransitionScope.SlideDirection.Start
    val end = AnimatedContentTransitionScope.SlideDirection.End
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { slideIntoContainer(start, animationSpec = tween(SLIDE_MS)) },
        exitTransition = { slideOutOfContainer(start, animationSpec = tween(SLIDE_MS)) },
        popEnterTransition = { slideIntoContainer(end, animationSpec = tween(SLIDE_MS)) },
        popExitTransition = { slideOutOfContainer(end, animationSpec = tween(SLIDE_MS)) },
    ) { content() }
}
