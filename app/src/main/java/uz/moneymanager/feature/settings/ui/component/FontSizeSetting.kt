package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.repository.SettingsRepository

@Composable
internal fun FontSizeSetting(
    savedSize: Int?,
    onSave: (Int) -> Unit,
    onReset: () -> Unit,
) {
    if (savedSize == null) return

    var sliderPosition by remember(savedSize) { mutableFloatStateOf(savedSize.toFloat()) }
    val currentSp = sliderPosition.roundToInt().coerceIn(SettingsRepository.MIN_FONT_SIZE, SettingsRepository.MAX_FONT_SIZE)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            FontSizeHeader()

            Spacer(modifier = Modifier.height(10.dp))

            val stateDesc = stringResource(R.string.font_size_value_sp, currentSp)
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = {
                    onSave(currentSp)
                },
                valueRange = SettingsRepository.MIN_FONT_SIZE.toFloat()..SettingsRepository.MAX_FONT_SIZE.toFloat(),
                steps = 7,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        stateDescription = stateDesc
                    }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.font_size_value_sp, currentSp),
                    style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                TextButton(
                    onClick = onReset,
                    enabled = currentSp != SettingsRepository.DEFAULT_FONT_SIZE
                ) {
                    Text(text = stringResource(R.string.font_size_reset))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            FontSizeSample(currentSp)
        }
    }
}
