package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor
import uz.moneymanager.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionTypeSelector(type: TransactionType, onSelect: (TransactionType) -> Unit) {
    val options = listOf(
        TransactionType.EXPENSE to R.string.type_expense,
        TransactionType.INCOME to R.string.type_income,
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (option, labelRes) ->
            val selected = type == option
            val semantic = if (option == TransactionType.INCOME) IncomeColor else ExpenseColor
            SegmentedButton(
                selected = selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = semantic.copy(alpha = 0.20f),
                    activeContentColor = semantic,
                    activeBorderColor = semantic,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = stringResource(labelRes),
                    maxLines = 1,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
