package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
internal fun FontSizeSetting(
    savedSize: Int?,
    onSave: (Int) -> Unit,
    onReset: () -> Unit,
) {
    if (savedSize == null) return

    var sliderPosition by remember(savedSize) { mutableFloatStateOf(savedSize.toFloat()) }
    val currentSp = sliderPosition.roundToInt().coerceIn(SettingsRepository.MIN_FONT_SIZE, SettingsRepository.MAX_FONT_SIZE)
    val stateDesc = stringResource(R.string.font_size_value_sp, currentSp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FontSizeHeader(currentSp)
        FontSizeSample(currentSp, canReset = currentSp != SettingsRepository.DEFAULT_FONT_SIZE, onReset = onReset)
        FontSizeSlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = { onSave(currentSp) },
            stateDesc = stateDesc
        )
    }
}
