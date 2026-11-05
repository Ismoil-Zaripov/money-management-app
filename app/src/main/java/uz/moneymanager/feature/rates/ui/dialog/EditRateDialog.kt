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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.ExchangeRateRow
import uz.moneymanager.domain.RateValidator
import uz.moneymanager.feature.rates.ui.component.RateDateField
import java.math.BigDecimal
import java.time.LocalDate

@Composable
fun EditRateDialog(
    item: ExchangeRateRow,
    onConfirm: (rate: BigDecimal, date: LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    var rateDate by remember(item) { mutableStateOf(item.date) }
    var rateText by remember(item) { mutableStateOf(item.rate.toPlainString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.title_exchange_rates)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "1 ${item.baseCode} = ? ${item.quoteCode}",
                    style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold)
                )
                RateDateField(date = rateDate, onDateChange = { rateDate = it })
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = RateValidator.sanitize(it) },
                    label = { Text(stringResource(R.string.label_rate)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { RateValidator.parse(rateText)?.let { onConfirm(it, rateDate) } }
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}
