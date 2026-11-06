package uz.moneymanager.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.ScreenHorizontalPadding

/** Loading placeholder for card-list pages (categories, currencies, payment methods, rates). */
@Composable
fun MmCardListSkeleton(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    rows: Int = 5,
    showBanner: Boolean = true,
) {
    val alpha = rememberSkeletonAlpha()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showBanner) {
            Box(Modifier.fillMaxWidth().height(72.dp).skeleton(alpha, MaterialTheme.shapes.large))
        }
        Box(Modifier.width(140.dp).height(14.dp).skeleton(alpha))
        repeat(rows) {
            MmCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(Modifier.size(44.dp).skeleton(alpha, CircleShape))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                        Box(Modifier.width(120.dp).height(14.dp).skeleton(alpha))
                        Box(Modifier.width(88.dp).height(12.dp).skeleton(alpha))
                    }
                    Box(Modifier.width(40.dp).height(20.dp).skeleton(alpha))
                }
            }
        }
    }
}
