package uz.moneymanager.feature.transactions.ui.filter.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.core.format.FormatUtils

/** Picked range as text ("01.11.2026 – 15.11.2026") with a "15 days" chip; a hint while nothing is picked. */
@Composable
internal fun DateRangeHeadline(from: LocalDate?, to: LocalDate?, modifier: Modifier = Modifier) {
    val end = to ?: from
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = when {
                from == null || end == null -> stringResource(R.string.filter_range_empty)
                from == end -> FormatUtils.formatDate(from)
                else -> "${FormatUtils.formatDate(from)} – ${FormatUtils.formatDate(end)}"
            },
            style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold),
            color = if (from == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (from != null && end != null) {
            val days = (ChronoUnit.DAYS.between(from, end) + 1).toInt()
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.extraLarge) {
                Text(
                    text = pluralStringResource(R.plurals.filter_range_days, days, days),
                    style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}
