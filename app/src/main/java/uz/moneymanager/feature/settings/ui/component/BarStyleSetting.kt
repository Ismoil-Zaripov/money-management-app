package uz.moneymanager.feature.settings.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.domain.model.BarStyle

/** Settings row: current bar style ("Glass · 28%") that opens the bar style sheet. */
@Composable
internal fun BarStyleSetting(style: BarStyle, glassTransparency: Int, onClick: () -> Unit) {
    val subtitle = when (style) {
        BarStyle.TINTED -> stringResource(R.string.bar_style_tinted)
        BarStyle.GLASS ->
            "${stringResource(R.string.bar_style_glass)} · ${stringResource(R.string.bar_transparency_value, glassTransparency)}"
    }
    SettingsItem(
        icon = Icons.Default.Layers,
        title = stringResource(R.string.setting_bar_style),
        subtitle = subtitle,
        onClick = onClick,
    )
}
