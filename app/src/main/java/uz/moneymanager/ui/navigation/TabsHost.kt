package uz.moneymanager.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import uz.moneymanager.feature.settings.ui.SettingsRoute
import uz.moneymanager.feature.transactions.ui.TransactionsRoute

private val RouteSetSaver = Saver<Set<String>, ArrayList<String>>(
    save = { ArrayList(it) },
    restore = { it.toSet() },
)

/** Where the tab screens send the user; every target is an overlay page above the tabs. */
class TabsActions(
    val onCreateTransaction: () -> Unit,
    val onEditTransaction: (Long) -> Unit,
    val onCurrencies: () -> Unit,
    val onRates: () -> Unit,
    val onCategories: () -> Unit,
    val onPaymentMethods: () -> Unit,
)

/**
 * Top-level tabs. A tab is composed the first time it is selected and then stays alive, so
 * switching back (and returning from an overlay page) never reloads or recomposes it.
 */
@Composable
fun TabsHost(selectedRoute: String, actions: TabsActions, modifier: Modifier = Modifier) {
    var visited by rememberSaveable(stateSaver = RouteSetSaver) { mutableStateOf(setOf(selectedRoute)) }
    val composed = visited + selectedRoute
    SideEffect { if (composed != visited) visited = composed }

    Box(modifier.fillMaxSize()) {
        if (Screen.Transactions.route in composed) {
            KeepAliveTab(visible = selectedRoute == Screen.Transactions.route) {
                TransactionsRoute(
                    onNavigateToCreate = actions.onCreateTransaction,
                    onNavigateToEdit = actions.onEditTransaction,
                )
            }
        }
        if (Screen.Settings.route in composed) {
            KeepAliveTab(visible = selectedRoute == Screen.Settings.route) {
                SettingsRoute(
                    onNavigateCurrencies = actions.onCurrencies,
                    onNavigateRates = actions.onRates,
                    onNavigateCategories = actions.onCategories,
                    onNavigatePaymentMethods = actions.onPaymentMethods,
                )
            }
        }
    }
}
