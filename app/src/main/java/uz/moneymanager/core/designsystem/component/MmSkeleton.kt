package uz.moneymanager.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer

/** One shared pulse for all skeleton blocks on a screen (cheaper than one transition per block). */
@Composable
fun rememberSkeletonAlpha(): State<Float> =
    rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 800), RepeatMode.Reverse),
        label = "skeletonAlpha",
    )

/** Placeholder block: neutral surface color pulsing via [alpha] (read in the layer, no recomposition). */
@Composable
fun Modifier.skeleton(alpha: State<Float>, shape: Shape = MaterialTheme.shapes.small): Modifier =
    this
        .graphicsLayer { this.alpha = alpha.value }
        .background(MaterialTheme.colorScheme.surfaceContainerHighest, shape)
