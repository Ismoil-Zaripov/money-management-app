package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.domain.TransactionDescription

/** Tavsif: ixtiyoriy, xato faqat uzunlik chegarasidan oshganda ko'rinadi. */
@Composable
internal fun DescriptionField(
    description: String,
    errorRes: Int?,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = description,
        onValueChange = onChange,
        label = {
            Text(stringResource(R.string.label_optional, stringResource(R.string.label_description)))
        },
        isError = errorRes != null,
        supportingText = errorRes?.let { errRes ->
            { Text(stringResource(errRes, TransactionDescription.MAX_LENGTH)) }
        },
        maxLines = 4,
        shape = FormFieldShape,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}
