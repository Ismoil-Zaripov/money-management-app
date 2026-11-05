package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.CategoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryDropdownField(
    text: String,
    categories: List<CategoryEntity>,
    menuVisible: Boolean,
    errorRes: Int?,
    onExpandedChange: (Boolean) -> Unit,
    onTextChange: (String) -> Unit,
    onPick: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = menuVisible,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text(stringResource(R.string.label_info)) },
            trailingIcon = if (text.isNotEmpty()) {
                {
                    IconButton(onClick = onClear) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.action_clear)
                        )
                    }
                }
            } else {
                null
            },
            isError = errorRes != null,
            supportingText = errorRes?.let { errRes ->
                { Text(stringResource(errRes)) }
            },
            singleLine = true,
            shape = FormFieldShape,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                onExpandedChange(false)
                focusManager.clearFocus()
            }),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .then(modifier)
        )
        ExposedDropdownMenu(
            expanded = menuVisible,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            categories.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.name, style = MaterialTheme.contentText) },
                    onClick = { onPick(cat.name) }
                )
            }
        }
    }
}
