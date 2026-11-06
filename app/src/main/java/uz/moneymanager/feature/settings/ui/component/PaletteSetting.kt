package uz.moneymanager.feature.settings.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import uz.moneymanager.R
import uz.moneymanager.domain.model.AppPalette

/** Settings row: current palette name; opens the palette sheet. */
@Composable
internal fun PaletteSetting(palette: AppPalette, onClick: () -> Unit) {
    SettingsItem(
        icon = Icons.Default.Palette,
        title = stringResource(R.string.setting_palette),
        subtitle = stringResource(palette.nameRes()),
        onClick = onClick,
    )
}
