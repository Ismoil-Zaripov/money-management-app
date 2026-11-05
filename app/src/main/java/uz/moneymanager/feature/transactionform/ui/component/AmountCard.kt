package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

/** Summa kartasi: "EXPENSE AMOUNT" yorlig'i, katta summa, valyuta chip'lari, xato. */
@Composable
internal fun AmountCard(
    label: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    currencies: List<CurrencyEntity>,
    selectedCurrencyId: Long?,
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
        else -> Color.Transparent
    }
    val container = if (errorText != null) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = container,
            border = BorderStroke(1.5.dp, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    AmountTextField(
                        amount = amount,
                        onAmountChange = onAmountChange,
                        color = color,
                        onNext = onNext,
                        onFocusChange = { isFocused = it },
                        modifier = modifier
                    )
                }
                CurrencyChips(currencies = currencies, selectedId = selectedCurrencyId, onSelect = onSelectCurrency)
            }
        }
        if (errorText != null) FormErrorText(text = errorText)
    }
}
