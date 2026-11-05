package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmBadge

@Composable
internal fun FontSizeHeader(currentSp: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SettingsRowIcon(Icons.Default.TextFields)
        Spacer(modifier = Modifier.width(12.dp))
        SettingsRowText(
            title = stringResource(R.string.setting_font_size),
            subtitle = stringResource(R.string.desc_font_size),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        MmBadge(stringResource(R.string.font_size_value_sp, currentSp))
    }
}
