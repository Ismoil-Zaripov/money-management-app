package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.component.rememberSkeletonAlpha
import uz.moneymanager.core.designsystem.component.skeleton
import uz.moneymanager.core.designsystem.theme.ScreenHorizontalPadding

/** Loading placeholder for the transaction form: an amount card and four input fields. */
@Composable
fun TransactionFormSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberSkeletonAlpha()
    Column(
        modifier = modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(96.dp).skeleton(alpha, MaterialTheme.shapes.large))
        repeat(4) {
            Box(Modifier.fillMaxWidth().height(56.dp).skeleton(alpha, MaterialTheme.shapes.medium))
        }
    }
}
