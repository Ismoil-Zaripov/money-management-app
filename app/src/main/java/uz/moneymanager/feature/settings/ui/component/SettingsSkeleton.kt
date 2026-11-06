package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.component.MmCard
import uz.moneymanager.core.designsystem.component.rememberSkeletonAlpha
import uz.moneymanager.core.designsystem.component.skeleton
import uz.moneymanager.core.designsystem.theme.ScreenHorizontalPadding

/** Loading placeholder for the Settings page: two section headers, each with a card of four rows. */
@Composable
fun SettingsSkeleton(topPadding: Dp, modifier: Modifier = Modifier) {
    val alpha = rememberSkeletonAlpha()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = topPadding + 12.dp)
            .padding(horizontal = ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(2) {
            Box(Modifier.width(120.dp).height(14.dp).skeleton(alpha))
            MmCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    repeat(4) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(Modifier.size(24.dp).skeleton(alpha, CircleShape))
                            Box(Modifier.weight(1f).height(14.dp).skeleton(alpha))
                            Box(Modifier.width(48.dp).height(14.dp).skeleton(alpha))
                        }
                    }
                }
            }
        }
    }
}
