package uz.moneymanager.feature.rates.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.CurrencyEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateCurrencyDropdown(
    label: String,
    selected: CurrencyEntity?,
    currencies: List<CurrencyEntity>,
    onSelect: (Long) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.code ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            currencies.forEach { c ->
                DropdownMenuItem(
                    text = { Text("${c.code} - ${c.name}", style = MaterialTheme.contentText) },
                    onClick = {
                        onSelect(c.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
