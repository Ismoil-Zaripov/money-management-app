package uz.moneymanager.feature.transactions.ui.component

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
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.component.MmCard
import uz.moneymanager.core.designsystem.component.rememberSkeletonAlpha
import uz.moneymanager.core.designsystem.component.skeleton

/** Loading placeholder: card rows with pulsing blocks, same geometry as [TransactionRow]. */
@Composable
fun TransactionSkeletonList(rows: Int = 6) {
    val alpha = rememberSkeletonAlpha()
    Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp)) {
        repeat(rows) {
            MmCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.size(44.dp).skeleton(alpha, CircleShape))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.width(120.dp).height(14.dp).skeleton(alpha))
                        Box(modifier = Modifier.width(96.dp).height(12.dp).skeleton(alpha))
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.End) {
                        Box(modifier = Modifier.width(80.dp).height(14.dp).skeleton(alpha))
                        Box(modifier = Modifier.width(48.dp).height(10.dp).skeleton(alpha))
                    }
                }
            }
        }
    }
}
