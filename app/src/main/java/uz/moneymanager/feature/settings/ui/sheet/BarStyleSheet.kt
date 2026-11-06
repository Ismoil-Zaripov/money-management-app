package uz.moneymanager.feature.settings.ui.sheet

import android.os.Build
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import uz.moneymanager.R
import uz.moneymanager.data.repository.SettingsRepository
import uz.moneymanager.domain.model.BarStyle
import uz.moneymanager.feature.settings.ui.component.BarStylePreview
import uz.moneymanager.feature.settings.ui.component.GlassTransparencySlider
import uz.moneymanager.feature.settings.ui.component.SegmentOption
import uz.moneymanager.feature.settings.ui.component.SegmentedChoice

/**
 * Bar style picker. The style applies immediately; the slider keeps a local value while dragging
 * (live preview) and saves to DataStore only when the drag ends.
 */
@Composable
internal fun BarStyleSheet(
    style: BarStyle,
    glassTransparency: Int,
    onStyleChange: (BarStyle) -> Unit,
    onTransparencyChange: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var dragValue by remember(glassTransparency) { mutableFloatStateOf(glassTransparency.toFloat()) }
    val percent = SettingsRepository.clampGlassTransparency(dragValue.roundToInt())
    val options = listOf(
        SegmentOption(BarStyle.TINTED, stringResource(R.string.bar_style_tinted), label = stringResource(R.string.bar_style_tinted)),
        SegmentOption(BarStyle.GLASS, stringResource(R.string.bar_style_glass), label = stringResource(R.string.bar_style_glass)),
    )

    SettingsSheetContainer(title = stringResource(R.string.setting_bar_style), onDismiss = onDismiss) {
        SegmentedChoice(options = options, selected = style, onSelect = onStyleChange)
        Spacer(modifier = Modifier.height(16.dp))
        BarStylePreview(style = style, transparencyPercent = percent)
        Spacer(modifier = Modifier.height(12.dp))
        GlassTransparencySlider(
            percent = percent,
            enabled = style == BarStyle.GLASS,
            onValueChange = { dragValue = it },
            onValueChangeFinished = { onTransparencyChange(percent) }
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Text(
                text = stringResource(R.string.bar_style_blur_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
