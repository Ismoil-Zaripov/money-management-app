package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import uz.moneymanager.R

/** "Transparency  28%" label row and a 0..100 slider. Disabled for the Tinted style. */
@Composable
internal fun GlassTransparencySlider(
    percent: Int,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    val title = stringResource(R.string.bar_style_transparency)
    val valueText = stringResource(R.string.bar_transparency_value, percent)
    val contentColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = contentColor)
            Text(valueText, style = MaterialTheme.typography.bodyMedium, color = contentColor)
        }
        Slider(
            value = percent.toFloat(),
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = 0f..100f,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = title
                    stateDescription = valueText
                }
        )
    }
}
