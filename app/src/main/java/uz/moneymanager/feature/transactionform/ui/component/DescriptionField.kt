package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.domain.TransactionDescription

/** Tavsif: ixtiyoriy; hisoblagich "n / MAX"; xato faqat uzunlik chegarasidan oshganda ko'rinadi. */
@Composable
internal fun DescriptionField(
    description: String,
    errorRes: Int?,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FormSectionLabel(
            title = stringResource(R.string.label_optional, stringResource(R.string.label_description)),
            trailing = stringResource(R.string.counter_format, description.length, TransactionDescription.MAX_LENGTH),
            trailingColor = if (errorRes != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = description,
            onValueChange = onChange,
            placeholder = { Text(stringResource(R.string.hint_description)) },
            isError = errorRes != null,
            supportingText = errorRes?.let { errRes ->
                { FormErrorText(text = stringResource(errRes, TransactionDescription.MAX_LENGTH)) }
            },
            minLines = 3,
            maxLines = 5,
            shape = FormFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                unfocusedBorderColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
