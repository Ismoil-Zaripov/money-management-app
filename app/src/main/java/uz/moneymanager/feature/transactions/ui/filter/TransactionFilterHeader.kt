package uz.moneymanager.feature.transactions.ui.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText

/** Close (X), bold title with the active-filter count, and "Clear all" on the right. */
@Composable
internal fun TransactionFilterHeader(
    activeCount: Int,
    onClose: () -> Unit,
    onClearAll: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close))
        }
        Text(
            text = stringResource(R.string.title_filter),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (activeCount > 0) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = activeCount.toString(),
                    style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onClearAll, enabled = activeCount > 0) {
            Text(stringResource(R.string.filter_clear_all), style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}
