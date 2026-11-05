package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.domain.MonitoringTotals

/** "Details" tabi: asosiy guruh + (kerak bo'lsa) "Others" fallback guruhi. */
@Composable
internal fun SummaryDetailsContent(
    totals: MonitoringTotals,
    displayCode: String,
    fallbackCode: String,
    showFallback: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        SummaryDetailsSection(
            title = stringResource(R.string.summary_total_title, totals.display.count),
            badge = stringResource(R.string.summary_base_badge, displayCode),
            note = {
                Text(
                    text = stringResource(R.string.summary_converted_subtitle, displayCode),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            totals = totals.display,
            currencyCode = displayCode
        )
        if (showFallback) {
            SummaryDetailsSection(
                title = stringResource(R.string.summary_fallback_title, totals.fallback.count),
                badge = stringResource(R.string.summary_direct_badge, fallbackCode),
                note = { SummaryNote(stringResource(R.string.summary_fallback_note, fallbackCode)) },
                totals = totals.fallback,
                currencyCode = fallbackCode
            )
        }
    }
}
