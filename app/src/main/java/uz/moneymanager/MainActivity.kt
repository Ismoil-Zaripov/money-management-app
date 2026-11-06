package uz.moneymanager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.moneymanager.ui.navigation.AppBottomNavItems
import uz.moneymanager.ui.navigation.AppNavHost
import uz.moneymanager.ui.navigation.BottomBar
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = AppBottomNavItems.any { it.screen.route == currentRoute }

    // Dynamic system bars color
    val barColor = MaterialTheme.colorScheme.barSurface
    val navBarColor = if (showBottomBar) MaterialTheme.colorScheme.barSurface else MaterialTheme.colorScheme.surface
    SystemBarsEffect(statusBarBackground = barColor, navBarBackground = navBarColor)

    MmScaffold(
        modifier = Modifier.fillMaxSize(),
        provideBottomInset = true,
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
    ) { _ ->
        AppNavHost(navController = navController)
    }
}
