package uz.moneymanager.feature.rates.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.ExchangeRateRow

@Composable
fun RateCard(
    item: ExchangeRateRow,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatePairText(item, Modifier.weight(1f).padding(end = 8.dp))
                    if (!item.locked) RateActions(onEdit = onEdit, onDelete = onDelete)
                }
                if (item.locked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    RateLockedBadge()
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RatePairText(item: ExchangeRateRow, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "1 ${item.baseCode}",
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "  =  ",
                style = MaterialTheme.contentText,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${formatRate(item.rate)} ${item.quoteCode}",
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RateActions(onEdit: () -> Unit, onDelete: () -> Unit) {
    IconButton(onClick = onEdit) {
        Icon(
            Icons.Default.Edit,
            contentDescription = stringResource(R.string.action_edit),
            tint = MaterialTheme.colorScheme.primary
        )
    }
    IconButton(onClick = onDelete) {
        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(R.string.action_delete),
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun RateLockedBadge() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Text(
            text = stringResource(R.string.rate_locked_badge),
            style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
