package uz.moneymanager.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterExitState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first

/**
 * True once this destination's enter animation has finished (latched: it stays true while the
 * page slides back out). A restored page whose transition is already idle becomes ready at once.
 */
@Composable
fun AnimatedContentScope.rememberEnterFinished(): Boolean {
    var finished by remember { mutableStateOf(false) }
    LaunchedEffect(transition) {
        snapshotFlow {
            transition.currentState == EnterExitState.Visible && transition.targetState == EnterExitState.Visible
        }.first { it }
        finished = true
    }
    return finished
}
