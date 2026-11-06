package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.domain.model.AppPalette

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun PalettePicker(selected: AppPalette, onSelect: (AppPalette) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SettingsRowIcon(Icons.Default.Palette)
            Spacer(modifier = Modifier.width(12.dp))
            SettingsRowText(
                title = stringResource(R.string.setting_palette),
                subtitle = stringResource(R.string.desc_palette),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
            AppPalette.entries.forEach { p ->
                PaletteSwatch(p, isDark, isSelected = selected == p, onClick = { onSelect(p) })
            }
        }
    }
}
