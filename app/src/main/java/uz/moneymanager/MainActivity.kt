package uz.moneymanager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.moneymanager.ui.navigation.AppNavHost
import uz.moneymanager.ui.navigation.BottomBar
import uz.moneymanager.ui.navigation.Screen
import uz.moneymanager.ui.navigation.TabsActions
import uz.moneymanager.ui.navigation.TabsHost
import uz.moneymanager.core.designsystem.component.MmScaffold
import uz.moneymanager.core.designsystem.theme.MoneyManagerTheme
import uz.moneymanager.core.designsystem.theme.SystemBarsEffect
import uz.moneymanager.core.designsystem.theme.barSurface

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
                    contentFontSize = s.contentFontSize,
                    barStyle = s.barStyle,
                    glassTransparency = s.glassTransparency,
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
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    // An overlay page (anything except the empty start destination) covers the tabs and the bottom bar.
    val overlayOpen = currentRoute != null && currentRoute != Screen.Tabs.route
    var selectedTab by rememberSaveable { mutableStateOf(Screen.Transactions.route) }

    BackHandler(enabled = !overlayOpen && selectedTab != Screen.Transactions.route) {
        selectedTab = Screen.Transactions.route
    }

    // Dynamic system bars color
    val barColor = MaterialTheme.colorScheme.barSurface
    val navBarColor = if (overlayOpen) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.barSurface
    SystemBarsEffect(statusBarBackground = barColor, navBarBackground = navBarColor)

    val actions = remember(navController) {
        TabsActions(
            onCreateTransaction = { navController.navigate(Screen.TransactionForm.createRoute()) },
            onEditTransaction = { id -> navController.navigate(Screen.TransactionForm.createRoute(id)) },
            onCurrencies = { navController.navigate(Screen.Currencies.route) },
            onRates = { navController.navigate(Screen.Rates.route) },
            onCategories = { navController.navigate(Screen.Categories.route) },
            onPaymentMethods = { navController.navigate(Screen.PaymentMethods.route) },
        )
    }

    Box(Modifier.fillMaxSize()) {
        MmScaffold(
            modifier = Modifier.fillMaxSize(),
            provideBottomInset = true,
            bottomBar = {
                BottomBar(currentRoute = selectedTab, onNavigate = { screen -> selectedTab = screen.route })
            }
        ) { _ ->
            TabsHost(selectedRoute = selectedTab, actions = actions)
        }
        AppNavHost(navController = navController, modifier = Modifier.fillMaxSize())
    }
}
