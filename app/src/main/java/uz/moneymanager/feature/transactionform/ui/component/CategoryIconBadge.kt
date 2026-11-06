package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.icon.categoryIcon

/** Kategoriya ikonkasi (nameKey kalit so'zlari xaritasi bo'yicha) yumaloq fonda. */
@Composable
internal fun CategoryIconBadge(categoryKey: String, modifier: Modifier = Modifier) {
    val icon = remember(categoryKey) { categoryIcon(categoryKey) }
    Box(
        modifier = modifier
            .size(32.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(18.dp)
        )
    }
}
