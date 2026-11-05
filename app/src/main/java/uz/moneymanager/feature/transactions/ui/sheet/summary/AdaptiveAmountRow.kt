package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Yorliq chapda, summa o'ngda. Agar ikkalasi bitta qatorga sig'masa (masalan shrift 1.3x-2.0x),
 * summa yorliq ostiga tushadi va [stackedIndent] bilan yorliq matni chegarasida tekislanadi.
 */
@Composable
internal fun AdaptiveAmountRow(
    label: @Composable () -> Unit,
    amount: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    stackedIndent: Dp = 0.dp,
) {
    Layout(contents = listOf(label, amount), modifier = modifier) { measurables, constraints ->
        val width = constraints.maxWidth
        val gap = 12.dp.roundToPx()
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val amountPlaceable = measurables[1].first().measure(loose)
        val labelFitsInRow = width - amountPlaceable.width - gap
        val labelInRow = measurables[0].first()
        if (labelInRow.maxIntrinsicWidth(constraints.maxHeight) <= labelFitsInRow) {
            val labelPlaceable = labelInRow.measure(loose.copy(maxWidth = labelFitsInRow))
            val height = maxOf(labelPlaceable.height, amountPlaceable.height)
            layout(width, height) {
                labelPlaceable.place(0, (height - labelPlaceable.height) / 2)
                amountPlaceable.place(width - amountPlaceable.width, (height - amountPlaceable.height) / 2)
            }
        } else {
            val labelPlaceable = labelInRow.measure(loose)
            val indent = stackedIndent.roundToPx().coerceAtMost((width - amountPlaceable.width).coerceAtLeast(0))
            layout(width, labelPlaceable.height + amountPlaceable.height) {
                labelPlaceable.place(0, 0)
                amountPlaceable.place(indent, labelPlaceable.height)
            }
        }
    }
}
