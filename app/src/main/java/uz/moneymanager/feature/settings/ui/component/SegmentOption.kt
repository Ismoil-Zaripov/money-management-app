package uz.moneymanager.feature.settings.ui.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
internal class SegmentOption<T>(
    val value: T,
    /** Accessibility description; also used as the visible label's content description. */
    val description: String,
    val icon: ImageVector? = null,
    val label: String? = null,
)
