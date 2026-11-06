package uz.moneymanager.feature.settings.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.domain.model.BarStyle

@Composable
internal fun BarStyleSelector(selected: BarStyle, onSelect: (BarStyle) -> Unit) {
    val options = listOf(
        SegmentOption(BarStyle.TINTED, stringResource(R.string.bar_style_tinted), label = stringResource(R.string.bar_style_tinted)),
        SegmentOption(BarStyle.GLASS, stringResource(R.string.bar_style_glass), label = stringResource(R.string.bar_style_glass)),
    )
    SettingsChoiceRow(
        icon = Icons.Default.Layers,
        title = stringResource(R.string.setting_bar_style),
        subtitle = options.first { it.value == selected }.description,
    ) {
        SegmentedChoice(options, selected, onSelect)
    }
}
