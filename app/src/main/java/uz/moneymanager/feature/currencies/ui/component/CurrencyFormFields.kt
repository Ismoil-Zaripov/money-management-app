package uz.moneymanager.feature.currencies.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R

@Composable
internal fun CurrencyFormFields(
    code: String,
    name: String,
    onCodeChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    codeEnabled: Boolean = true,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = code,
            onValueChange = { onCodeChange(it.uppercase()) },
            label = { Text(stringResource(R.string.label_currency_code)) },
            singleLine = true,
            enabled = codeEnabled,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.label_currency_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
