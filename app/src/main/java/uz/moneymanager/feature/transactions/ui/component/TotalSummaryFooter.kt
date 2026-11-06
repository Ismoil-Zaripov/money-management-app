package uz.moneymanager.feature.transactions.ui.component

import uz.moneymanager.core.designsystem.theme.ScreenHorizontalPadding
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.designsystem.theme.moneyColors
import uz.moneymanager.core.format.FormatUtils

/** Rounded "Total Summary" pill above the bottom bar. Tap opens the Summary Report sheet. */
@Composable
fun TotalSummaryFooter(
    balance: BigDecimal,
    displayCode: String,
    isAmountVisible: Boolean,
    onToggleAmountVisibility: () -> Unit,
    onClick: () -> Unit,
) {
    val shape = MaterialTheme.shapes.extraLarge
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = shape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp)
            .heightIn(min = 52.dp)
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleAmountVisibility) {
                Icon(
                    imageVector = if (isAmountVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = stringResource(R.string.action_show_footer),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.action_show_footer),
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (isAmountVisible) {
                    FormatUtils.formatAmount(balance, displayCode, includePositiveSign = true)
                } else {
                    "••••••"
                },
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.End,
                maxLines = 1,
                color = when {
                    !isAmountVisible -> MaterialTheme.colorScheme.onSurface
                    balance >= BigDecimal.ZERO -> MaterialTheme.moneyColors.income
                    else -> MaterialTheme.moneyColors.expense
                }
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
