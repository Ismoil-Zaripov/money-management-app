package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.contentText

@Composable
fun SummaryRowItem(
    label: String,
    amount: String,
    color: Color,
    isIncome: Boolean = false,
    isBalance: Boolean = false,
) {
    AdaptiveAmountRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        stackedIndent = 34.dp,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(color.copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isBalance) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(color, shape = CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    style = MaterialTheme.contentText.copy(fontWeight = if (isBalance) FontWeight.Bold else FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        amount = {
            Text(
                text = amount,
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                color = color,
                maxLines = 1,
                softWrap = false
            )
        }
    )
}
