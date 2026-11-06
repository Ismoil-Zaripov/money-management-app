package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.getPaletteColorScheme
import uz.moneymanager.domain.model.AppPalette

/** One selectable palette: miniature preview, name, check mark when selected. */
@Composable
internal fun PaletteCard(palette: AppPalette, isDark: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    val scheme = getPaletteColorScheme(palette, isDark || palette.darkOnly)
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colors.primary else colors.outlineVariant,
        ),
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            PalettePreview(scheme)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(palette.nameRes()),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.primary)
        }
    }
}
