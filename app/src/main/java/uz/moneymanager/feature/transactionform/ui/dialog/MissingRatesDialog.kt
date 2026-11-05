package uz.moneymanager.feature.transactionform.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmDialog
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.format.FormatUtils
import uz.moneymanager.domain.RateValidator
import uz.moneymanager.feature.transactionform.presentation.FormEvent
import uz.moneymanager.feature.transactionform.ui.component.FormFieldShape

/** Kurs topilmaganda: foydalanuvchi qo'lda kurs kiritadi; [onSave] tasdiqlangan kurslar bilan chaqiriladi. */
@Composable
internal fun MissingRatesDialog(
    event: FormEvent.ShowMissingRates,
    onSave: (Map<Long, BigDecimal>) -> Unit,
    onDismiss: () -> Unit,
) {
    val rateInputs = remember(event) {
        mutableStateMapOf<Long, String>().apply {
            event.missing.forEach { quote ->
                put(quote.id, event.suggestedRates[quote.id] ?: "")
            }
        }
    }

    MmDialog(
        title = stringResource(R.string.dialog_missing_rates_title),
        confirmText = stringResource(R.string.action_save),
        dismissText = stringResource(R.string.action_cancel),
        onConfirm = {
            val parsedRates = mutableMapOf<Long, BigDecimal>()
            var valid = true
            for (quote in event.missing) {
                val r = RateValidator.parse(rateInputs[quote.id] ?: "")
                if (r == null) {
                    valid = false
                    break
                }
                parsedRates[quote.id] = r
            }
            if (valid) onSave(parsedRates)
        },
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(
                    R.string.dialog_missing_rates_desc,
                    event.base.code,
                    FormatUtils.formatDate(event.date)
                ),
                style = MaterialTheme.contentText
            )
            event.missing.forEach { quote ->
                OutlinedTextField(
                    value = rateInputs[quote.id] ?: "",
                    onValueChange = { str -> rateInputs[quote.id] = RateValidator.sanitize(str) },
                    label = { Text("1 ${event.base.code} = ? ${quote.code}", style = MaterialTheme.contentText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = FormFieldShape,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
