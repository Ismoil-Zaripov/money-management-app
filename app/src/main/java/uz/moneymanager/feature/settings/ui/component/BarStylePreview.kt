package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.barSurface
import uz.moneymanager.core.designsystem.theme.glassTintAlpha
import uz.moneymanager.domain.model.BarStyle

/** Mini preview: colorful content blocks under a bar strip drawn with the chosen style (no real blur). */
@Composable
internal fun BarStylePreview(style: BarStyle, transparencyPercent: Int, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val barAlpha = if (style == BarStyle.GLASS) glassTintAlpha(transparencyPercent) else 1f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(scheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(scheme.primary, scheme.tertiary, scheme.secondary, scheme.error).forEach { color ->
                Box(modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(8.dp)).background(color))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(scheme.barSurface.copy(alpha = barAlpha))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = 56.dp, height = 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(scheme.onSurface.copy(alpha = 0.6f))
            )
        }
    }
}
