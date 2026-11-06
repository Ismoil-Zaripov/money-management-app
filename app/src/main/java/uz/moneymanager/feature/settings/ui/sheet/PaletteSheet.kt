package uz.moneymanager.feature.settings.ui.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.feature.settings.ui.component.PaletteCard

/** Palette picker: a preview card per theme. A tap applies the theme immediately. */
@Composable
internal fun PaletteSheet(selected: AppPalette, onSelect: (AppPalette) -> Unit, onDismiss: () -> Unit) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    SettingsSheetContainer(title = stringResource(R.string.setting_palette), onDismiss = onDismiss) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppPalette.entries.forEach { palette ->
                PaletteCard(palette, isDark, isSelected = palette == selected, onClick = { onSelect(palette) })
            }
        }
    }
}
