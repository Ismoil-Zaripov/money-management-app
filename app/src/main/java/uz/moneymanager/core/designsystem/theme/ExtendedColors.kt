package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * One semantic color in two roles: [fill] for icons/badges (UI, >= 3:1),
 * [text] for readable text (>= 4.5:1 on the theme surface).
 */
@Immutable
data class SemanticColor(val fill: Color, val text: Color)

/** Palette-independent status colors that Material3 ColorScheme has no slot for. */
@Immutable
data class ExtendedColors(
    val success: SemanticColor,
    val warning: SemanticColor,
    val info: SemanticColor,
)

private val SuccessFill = Color(0xFF16A34A)
private val WarningFill = Color(0xFFF59E0B)
private val InfoFill = Color(0xFF0284C7)

val LightExtendedColors = ExtendedColors(
    success = SemanticColor(fill = SuccessFill, text = Color(0xFF15803D)),
    warning = SemanticColor(fill = WarningFill, text = Color(0xFFB45309)),
    info = SemanticColor(fill = InfoFill, text = Color(0xFF0369A1)),
)

val DarkExtendedColors = ExtendedColors(
    success = SemanticColor(fill = SuccessFill, text = Color(0xFF4ADE80)),
    warning = SemanticColor(fill = WarningFill, text = Color(0xFFFBBF24)),
    info = SemanticColor(fill = InfoFill, text = Color(0xFF38BDF8)),
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
