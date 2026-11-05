package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.designsystem.theme.moneyColors
import uz.moneymanager.domain.model.TransactionType

private val typeOptions = listOf<TransactionType?>(null, TransactionType.INCOME, TransactionType.EXPENSE)

/** All / Income / Expense: segmented control plus a radio list (single choice). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeFilterSheet(
    selected: TransactionType?,
    onApply: (TransactionType?) -> Unit,
    onDismiss: () -> Unit,
) {
    var pending by remember { mutableStateOf(selected) }

    FilterSheetContainer(title = stringResource(R.string.filter_type), onDismiss = onDismiss) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            typeOptions.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = pending == option,
                    onClick = { pending = option },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = typeOptions.size),
                    label = { Text(typeLabel(option)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        typeOptions.forEach { option ->
            TypeRow(type = option, selected = pending == option, onClick = { pending = option })
        }
        FilterSheetFooter(
            onReset = { pending = null },
            onApply = { onApply(pending) },
            applyLabel = applyLabel(if (pending != null) 1 else 0)
        )
    }
}

@Composable
private fun typeLabel(type: TransactionType?): String = when (type) {
    TransactionType.INCOME -> stringResource(R.string.type_income)
    TransactionType.EXPENSE -> stringResource(R.string.type_expense)
    null -> stringResource(R.string.filter_all)
}

@Composable
private fun TypeRow(type: TransactionType?, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.moneyColors
    val tint = when (type) {
        TransactionType.INCOME -> colors.income
        TransactionType.EXPENSE -> colors.expense
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val icon = when (type) {
        TransactionType.INCOME -> Icons.Default.ArrowDownward
        TransactionType.EXPENSE -> Icons.Default.ArrowUpward
        null -> Icons.Default.SwapVert
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(tint.copy(alpha = 0.16f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Text(
            text = typeLabel(type),
            style = MaterialTheme.contentText,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        RadioButton(selected = selected, onClick = null)
    }
}
