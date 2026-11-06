package uz.moneymanager.core.designsystem.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.LocalBarStyle
import uz.moneymanager.core.designsystem.theme.barBackground
import uz.moneymanager.domain.model.BarStyle

private val GlassBlurRadius = 24.dp

/** The screen content recorded into [layer], so bars drawn over it can show a blurred copy of what is behind them. */
@Stable
class GlassState internal constructor(internal val layer: GraphicsLayer) {
    internal var origin: LayoutCoordinates? = null
}

val LocalGlassState = compositionLocalOf<GlassState?> { null }

@Composable
fun rememberGlassState(): GlassState {
    val layer = rememberGraphicsLayer()
    return remember(layer) { GlassState(layer) }
}

/** Marks the scrolling content that glass bars blur. */
fun Modifier.glassSource(state: GlassState, enabled: Boolean): Modifier =
    if (!enabled) this else this
        .onGloballyPositioned { state.origin = it }
        .drawWithContent {
            state.layer.record { this@drawWithContent.drawContent() }
            drawLayer(state.layer)
        }

/**
 * Background of a top/bottom bar. Tinted style: opaque tint. Glass style: blurred content behind
 * the bar (Android 12+) under a translucent tint; older devices get the translucent tint only.
 */
@Composable
fun Modifier.barSurfaceBackground(): Modifier {
    val tint = MaterialTheme.colorScheme.barBackground()
    val glass = LocalGlassState.current
    if (LocalBarStyle.current != BarStyle.GLASS || glass == null) return background(tint)

    val blurLayer = rememberGraphicsLayer()
    var offset by remember { mutableStateOf(Offset.Zero) }
    val radius = with(LocalDensity.current) { GlassBlurRadius.toPx() }
    return this
        .onGloballyPositioned { bar ->
            val source = glass.origin
            if (source != null && source.isAttached && bar.isAttached) {
                offset = source.localPositionOf(bar, Offset.Zero)
            }
        }
        .drawWithContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (blurLayer.renderEffect == null) {
                    blurLayer.renderEffect = BlurEffect(radius, radius, TileMode.Clamp)
                    blurLayer.clip = true
                }
                blurLayer.record { translate(-offset.x, -offset.y) { drawLayer(glass.layer) } }
                drawLayer(blurLayer)
            }
            drawRect(tint)
            drawContent()
        }
}
