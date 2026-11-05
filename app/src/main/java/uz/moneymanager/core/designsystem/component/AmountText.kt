package uz.moneymanager.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import uz.moneymanager.core.designsystem.theme.moneyColors

enum class AmountKind { Income, Expense, Neutral }

/**
 * Money amount: income green with "+", expense red with "−", tabular digits.
 * [text] is already formatted by the caller, e.g. "1 250 000 UZS".
 */
@Composable
fun AmountText(
    text: String,
    kind: AmountKind,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    textAlign: TextAlign? = null,
) {
    val colors = MaterialTheme.moneyColors
    val color: Color = when (kind) {
        AmountKind.Income -> colors.income
        AmountKind.Expense -> colors.expense
        AmountKind.Neutral -> MaterialTheme.colorScheme.onSurface
    }
    val sign = when (kind) {
        AmountKind.Income -> "+ "
        AmountKind.Expense -> "− "
        AmountKind.Neutral -> ""
    }
    Text(
        text = sign + text,
        modifier = modifier,
        color = color,
        style = style.copy(fontFeatureSettings = "tnum"),
        fontWeight = FontWeight.SemiBold,
        textAlign = textAlign,
        maxLines = 1,
    )
}

@MmPreviews
@Composable
private fun AmountTextPreview() = MmPreviewHost {
    androidx.compose.foundation.layout.Column {
        AmountText("12 000 000 UZS", AmountKind.Income)
        AmountText("285 000 UZS", AmountKind.Expense)
        AmountText("1 250 000 UZS", AmountKind.Neutral)
    }
}
