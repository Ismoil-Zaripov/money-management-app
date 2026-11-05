package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.contentText

/** Legenda qatori: [dotColor] null bo'lsa nuqta o'rnida o'sha kenglikda bo'sh joy qoladi. */
@Composable
internal fun LegendRow(
    label: String,
    amount: String,
    amountColor: Color,
    dotColor: Color?,
    bold: Boolean = false,
) {
    AdaptiveAmountRow(
        modifier = Modifier.fillMaxWidth(),
        stackedIndent = LegendDotSize + LegendDotGap,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(LegendDotSize)
                        .then(if (dotColor != null) Modifier.background(dotColor, CircleShape) else Modifier)
                )
                Spacer(modifier = Modifier.width(LegendDotGap))
                Text(
                    text = label,
                    style = MaterialTheme.contentText.copy(fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        amount = {
            Text(
                text = amount,
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                color = amountColor,
                maxLines = 1,
                softWrap = false
            )
        }
    )
}

private val LegendDotSize = 10.dp
private val LegendDotGap = 8.dp
