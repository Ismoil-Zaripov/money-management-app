package uz.moneymanager.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
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
private const val SLIDE_MS = 300

/** Secondary screens: they rise from the bottom like a bottom sheet and leave downwards. */
private val slideRoutes = setOf(
    Screen.TransactionForm.route,
    Screen.Currencies.route,
    Screen.Rates.route,
    Screen.Categories.route,
    Screen.PaymentMethods.route,
)

private fun NavBackStackEntry.isSlideScreen() = destination.route in slideRoutes

/** A tab stays put (no fade) while a slide screen covers it or is dismissed from above it. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabExit(): ExitTransition =
    if (targetState.isSlideScreen()) ExitTransition.None else fadeOut(animationSpec = tween(FADE_OUT_MS))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabPopEnter(): EnterTransition =
    if (initialState.isSlideScreen()) EnterTransition.None else fadeIn(animationSpec = tween(FADE_IN_MS))

/** Top-level tabs fade; every secondary screen slides up from the bottom and back down on back. */
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Transactions.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(FADE_IN_MS)) },
        exitTransition = { tabExit() },
        popEnterTransition = { tabPopEnter() },
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
