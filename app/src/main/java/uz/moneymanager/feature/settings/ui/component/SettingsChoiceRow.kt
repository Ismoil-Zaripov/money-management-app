package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** Ikonka + sarlavha/izoh + o'ngda ixcham tanlagich ([control]). */
@Composable
internal fun SettingsChoiceRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    control: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsRowIcon(icon)
        Spacer(modifier = Modifier.width(12.dp))
        SettingsRowText(title, subtitle, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        control()
    }
}
