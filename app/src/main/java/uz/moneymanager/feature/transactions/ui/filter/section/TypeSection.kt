package uz.moneymanager.feature.transactions.ui.filter.section

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.domain.model.TransactionType

private val typeOptions = listOf<TransactionType?>(null, TransactionType.INCOME, TransactionType.EXPENSE)

/** All / Income / Expense as one segmented control (single choice). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSection(
    selected: TransactionType?,
    onSelect: (TransactionType?) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        typeOptions.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selected == option,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = typeOptions.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = colors.primaryContainer,
                    activeContentColor = colors.onPrimaryContainer,
                ),
                label = { Text(typeLabel(option), style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold)) }
            )
        }
    }
}

@Composable
private fun typeLabel(type: TransactionType?): String = when (type) {
    TransactionType.INCOME -> stringResource(R.string.type_income)
    TransactionType.EXPENSE -> stringResource(R.string.type_expense)
    null -> stringResource(R.string.filter_all)
}
