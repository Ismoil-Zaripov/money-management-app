package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.moneymanager.data.local.CurrencyEntity

/** Summa kartasi: katta summa maydoni + valyuta tanlagichi + xato matni. */
@Composable
internal fun AmountCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    currencyCode: String,
    currencies: List<CurrencyEntity>,
    onSelectCurrency: (Long) -> Unit,
    color: Color,
    errorText: String?,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = when {
        errorText != null -> MaterialTheme.colorScheme.error
        isFocused -> color
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val borderWidth = if (isFocused || errorText != null) 1.5.dp else 1.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = FormFieldShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(borderWidth, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AmountTextField(
                    amount = amount,
                    onAmountChange = onAmountChange,
                    color = color,
                    onNext = onNext,
                    onFocusChange = { isFocused = it },
                    modifier = modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
                CurrencyDropdown(
                    currencyCode = currencyCode,
                    currencies = currencies,
                    onSelect = onSelectCurrency
                )
            }
        }

        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
