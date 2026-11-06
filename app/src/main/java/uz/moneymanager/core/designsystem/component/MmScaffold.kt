package uz.moneymanager.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Height of the app-level bottom navigation bar that overlays the current screen (0 on screens without it). */
private val LocalOuterBottomInset = compositionLocalOf { 0.dp }

/**
 * Scaffold whose content fills the whole area and scrolls under the bars; use the passed padding as
 * `contentPadding`.
 *
 * @param provideBottomInset true for the app-level scaffold: its bottom bar height is added to the
 *   bottom padding of every screen scaffold inside it.
 */
@Composable
fun MmScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    provideBottomInset: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    val outerBottom: Dp = LocalOuterBottomInset.current
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = topBar,
        bottomBar = { Box(Modifier.padding(bottom = outerBottom)) { bottomBar() } },
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
    ) { padding ->
        val inset = if (provideBottomInset) padding.calculateBottomPadding() else 0.dp
        CompositionLocalProvider(LocalOuterBottomInset provides inset) {
            Box(Modifier.fillMaxSize()) { content(padding) }
        }
    }
}
