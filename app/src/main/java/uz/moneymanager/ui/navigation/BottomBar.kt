package uz.moneymanager.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.component.MmPreviewHost
import uz.moneymanager.core.designsystem.component.MmPreviews
import uz.moneymanager.core.designsystem.component.barSurfaceBackground
import uz.moneymanager.core.designsystem.theme.barSurface
import uz.moneymanager.core.designsystem.theme.onBarSurface

val AppBottomNavItems = listOf(
    BottomNavItem.Transactions,
    BottomNavItem.Settings,
)

/** Two-tab bottom bar; the selected tab gets a filled pill indicator. */
@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.barSurfaceBackground()) {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBarSurface.copy(alpha = 0.2f))
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            AppBottomNavItems.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.screen.route,
                    onClick = { onNavigate(item.screen) },
                    icon = { Icon(item.icon, contentDescription = stringResource(item.titleRes)) },
                    label = { Text(stringResource(item.titleRes)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.barSurface,
                        selectedTextColor = MaterialTheme.colorScheme.onBarSurface,
                        indicatorColor = MaterialTheme.colorScheme.onBarSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onBarSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onBarSurface,
                    ),
                )
            }
        }
        // Gesture/nav-bar inset below the items; the Column background paints it, so no gap shows.
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@MmPreviews
@Composable
private fun BottomBarPreview() = MmPreviewHost {
    BottomBar(currentRoute = Screen.Transactions.route, onNavigate = {})
}
