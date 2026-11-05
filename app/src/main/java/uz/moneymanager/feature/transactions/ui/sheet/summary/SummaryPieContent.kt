package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.moneymanager.domain.MonitoringTotals

/** "Pie Chart" tabi: donut + legend (Balance 3-qator) + (kerak bo'lsa) Others bloki. */
@Composable
internal fun SummaryPieContent(
    totals: MonitoringTotals,
    displayCode: String,
    fallbackCode: String,
    showFallback: Boolean,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            SummaryDonutChartView(
                totals = totals.display,
                currencyCode = displayCode,
                active = active,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        SummaryLegendCard(totals = totals.display, currencyCode = displayCode)
        if (showFallback) {
            SummaryOthersCard(totals = totals.fallback, currencyCode = fallbackCode)
        }
    }
}
