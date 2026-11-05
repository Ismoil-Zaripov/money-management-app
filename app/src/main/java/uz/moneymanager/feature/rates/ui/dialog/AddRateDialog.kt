package uz.moneymanager.feature.rates.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.domain.RateValidator
import uz.moneymanager.feature.rates.ui.component.RateCurrencyDropdown
import uz.moneymanager.feature.rates.ui.component.RateDateField
import java.math.BigDecimal
import java.time.LocalDate

@Composable
fun AddRateDialog(
    currencies: List<CurrencyEntity>,
    onConfirm: (baseId: Long, quoteId: Long, rate: BigDecimal, date: LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    var baseId by remember { mutableStateOf(currencies.firstOrNull { it.isDefault }?.id ?: currencies.firstOrNull()?.id) }
    var quoteId by remember { mutableStateOf(currencies.firstOrNull { !it.isDefault }?.id ?: currencies.lastOrNull()?.id) }
    var rateDate by remember { mutableStateOf(LocalDate.now()) }
    var rateText by remember { mutableStateOf("") }
    val baseCurr = currencies.firstOrNull { it.id == baseId }
    val quoteCurr = currencies.firstOrNull { it.id == quoteId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.action_add_rate)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RateDateField(date = rateDate, onDateChange = { rateDate = it })
                RateCurrencyDropdown(
                    label = stringResource(R.string.label_base_currency),
                    selected = baseCurr,
                    currencies = currencies,
                    onSelect = { baseId = it }
                )
                RateCurrencyDropdown(
                    label = stringResource(R.string.label_quote_currency),
                    selected = quoteCurr,
                    currencies = currencies,
                    onSelect = { quoteId = it }
                )
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = RateValidator.sanitize(it) },
                    label = {
                        Text("1 ${baseCurr?.code ?: ""} = ? ${quoteCurr?.code ?: ""}", style = MaterialTheme.contentText)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(
                        R.string.rate_inverse_hint,
                        quoteCurr?.code ?: "",
                        "...",
                        baseCurr?.code ?: ""
                    ),
                    style = MaterialTheme.contentText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsed = RateValidator.parse(rateText)
                    val b = baseId
                    val q = quoteId
                    if (parsed != null && b != null && q != null) onConfirm(b, q, parsed, rateDate)
                }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}
