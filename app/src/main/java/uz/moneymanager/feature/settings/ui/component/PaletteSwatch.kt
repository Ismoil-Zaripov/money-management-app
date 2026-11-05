package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.getPaletteSwatchColor
import uz.moneymanager.domain.model.AppPalette

@Composable
internal fun PaletteSwatch(palette: AppPalette, isDark: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    val name = when (palette) {
        AppPalette.OCEAN_TEAL -> stringResource(R.string.palette_ocean_teal)
        AppPalette.EMERALD -> stringResource(R.string.palette_emerald)
        AppPalette.ROYAL_INDIGO -> stringResource(R.string.palette_royal_indigo)
        AppPalette.WARM_CORAL -> stringResource(R.string.palette_warm_coral)
        AppPalette.GRAPHITE_MINT -> stringResource(R.string.palette_graphite_mint)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(getPaletteSwatchColor(palette, isDark))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = name }
    ) {
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}
