package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormatSymbols
import uz.moneymanager.R
import uz.moneymanager.feature.transactionform.presentation.AmountInput

/** Summa kiritish maydoni: shrift o'lchami uzunlikka moslashadi, kursor pozitsiyasi saqlanadi. */
@Composable
internal fun RowScope.AmountTextField(
    amount: String,
    onAmountChange: (String) -> Unit,
    color: Color,
    onNext: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Mahalliy TextFieldValue: kursor/selection saqlanadi, ViewModel'ga faqat tozalangan matn yuboriladi
    var field by remember { mutableStateOf(TextFieldValue(amount, TextRange(amount.length))) }
    LaunchedEffect(amount) {
        if (amount != field.text) field = TextFieldValue(amount, TextRange(amount.length))
    }
    val decimalSeparator = remember { DecimalFormatSymbols.getInstance().decimalSeparator }
    val transformation = remember(decimalSeparator) { AmountVisualTransformation(decimalSeparator) }
    val amountLabel = stringResource(R.string.label_amount)

    BoxWithConstraints(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val maxWidthDp = with(density) { constraints.maxWidth.toDp().value }
        val fontSizeSp = rememberAmountFontSizeSp(field.text, maxWidthDp)
        val textStyle = MaterialTheme.typography.headlineLarge.copy(
            fontSize = fontSizeSp.sp,
            lineHeight = (fontSizeSp * 1.2f).sp,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        BasicTextField(
            value = field,
            onValueChange = { new ->
                field = sanitizeAmountValue(field, new)
                if (field.text != amount) onAmountChange(field.text)
            },
            textStyle = textStyle,
            singleLine = true,
            cursorBrush = SolidColor(color),
            visualTransformation = transformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { onNext() }),
            modifier = modifier
                .onFocusChanged { onFocusChange(it.isFocused) }
                .semantics { contentDescription = amountLabel }
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (field.text.isEmpty()) {
                        Text(
                            text = "0",
                            style = textStyle.copy(color = color.copy(alpha = 0.38f)),
                        )
                    }
                    inner()
                }
            }
        )
    }
}

@Composable
private fun rememberAmountFontSizeSp(text: String, maxWidthDp: Float): Float {
    val density = LocalDensity.current
    val formattedLength = remember(text) {
        val len = text.length
        (len + (len - 1).coerceAtLeast(0) / 3).coerceAtLeast(1)
    }
    return remember(formattedLength, maxWidthDp, density.fontScale) {
        val preferred = when {
            formattedLength <= 7 -> 32f
            formattedLength <= 10 -> 26f
            formattedLength <= 14 -> 22f
            formattedLength <= 18 -> 18f
            else -> 15f
        }
        val maxFit = (maxWidthDp / (formattedLength * 0.62f * density.fontScale)).coerceAtLeast(13f)
        minOf(preferred, maxFit)
    }
}

private fun sanitizeAmountValue(current: TextFieldValue, new: TextFieldValue): TextFieldValue {
    val sanitized = AmountInput.sanitize(new.text, isPaste = new.text.length - current.text.length > 1)
    return when (sanitized) {
        new.text -> new
        current.text -> current // rad etilgan belgi: matn va kursor o'zgarmaydi
        else -> {
            val removed = new.text.length - sanitized.length
            val cursor = (new.selection.end - removed).coerceIn(0, sanitized.length)
            TextFieldValue(sanitized, TextRange(cursor))
        }
    }
}
