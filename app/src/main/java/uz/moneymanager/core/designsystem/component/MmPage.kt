package uz.moneymanager.core.designsystem.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

private const val REVEAL_MS = 150

/**
 * False while an overlay page is still sliding in: its content must not be composed yet.
 * Provided by the navigation host; everywhere else (tabs, previews) pages are ready at once.
 */
val LocalPageReady = compositionLocalOf { true }

/**
 * Page loading rule: only the cheap [skeleton] is composed while the page is sliding in
 * ([LocalPageReady]) or its data has not arrived ([loaded] is false). Then the real [content]
 * replaces it with a short crossfade. Pages decide Empty vs Content inside [content], so an
 * empty state never shows before the first data has arrived.
 */
@Composable
fun MmPage(
    loaded: Boolean,
    skeleton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val showContent = loaded && LocalPageReady.current
    Crossfade(
        targetState = showContent,
        modifier = modifier,
        animationSpec = tween(REVEAL_MS),
        label = "pageReveal",
    ) { isShown ->
        if (isShown) content() else skeleton()
    }
}
