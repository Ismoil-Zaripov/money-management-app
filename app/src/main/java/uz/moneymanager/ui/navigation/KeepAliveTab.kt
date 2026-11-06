package uz.moneymanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

/**
 * Keeps [content] in the composition (ViewModel flows, scroll position, remembered state) while
 * hidden: a hidden tab is neither measured, drawn nor hit-tested, so it costs almost nothing.
 */
@Composable
fun KeepAliveTab(visible: Boolean, content: @Composable () -> Unit) {
    Layout(content = content, modifier = Modifier) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        if (!visible) {
            layout(width, height) {}
        } else {
            val placeables = measurables.map { it.measure(constraints) }
            layout(width, height) { placeables.forEach { it.place(0, 0) } }
        }
    }
}
