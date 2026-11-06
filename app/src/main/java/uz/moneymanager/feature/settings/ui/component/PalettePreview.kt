package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.barSurface
import uz.moneymanager.core.designsystem.theme.onBarSurface

/** Miniature screen drawn with the palette's own colors: bar, background, card, primary and accent. */
@Composable
internal fun PalettePreview(scheme: ColorScheme, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .size(width = 72.dp, height = 52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(scheme.background)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(scheme.barSurface)) {
            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(width = 20.dp, height = 3.dp)
                    .background(scheme.onBarSurface, RoundedCornerShape(2.dp))
                    .align(Alignment.CenterStart)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .fillMaxWidth()
                .height(22.dp)
                .background(scheme.surface, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).background(scheme.primary, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.size(10.dp).background(scheme.secondary, CircleShape))
        }
    }
}
