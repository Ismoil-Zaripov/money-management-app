package uz.moneymanager.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.component.MmPreviewHost
import uz.moneymanager.core.designsystem.component.MmPreviews
import uz.moneymanager.core.designsystem.theme.barBackground

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
    val container = MaterialTheme.colorScheme.barBackground()
    Column(modifier = modifier) {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(
            containerColor = container,
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
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
        // Gesture/nav-bar inset below the bar is painted with the bar color, so no gap shows under it.
        Spacer(Modifier.background(container).windowInsetsBottomHeight(WindowInsets.navigationBars).fillMaxWidth())
    }
}

@MmPreviews
@Composable
private fun BottomBarPreview() = MmPreviewHost {
    BottomBar(currentRoute = Screen.Transactions.route, onNavigate = {})
}
