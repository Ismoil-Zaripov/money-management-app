package uz.moneymanager.feature.settings.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.domain.model.ThemeMode

@Composable
internal fun ThemeSelector(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    val options = listOf(
        SegmentOption(ThemeMode.LIGHT, stringResource(R.string.theme_light), icon = Icons.Default.LightMode),
        SegmentOption(ThemeMode.DARK, stringResource(R.string.theme_dark), icon = Icons.Default.DarkMode),
        SegmentOption(ThemeMode.SYSTEM, stringResource(R.string.theme_system), icon = Icons.Default.SettingsBrightness),
    )
    SettingsChoiceRow(
        icon = Icons.Default.BrightnessMedium,
        title = stringResource(R.string.setting_theme),
        subtitle = options.first { it.value == selected }.description
    ) {
        SegmentedChoice(options, selected, onSelect)
    }
}
