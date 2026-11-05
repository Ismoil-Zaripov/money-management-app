package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.domain.MonitoringTotals

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryReportBottomSheet(
    onDismiss: () -> Unit,
    totals: MonitoringTotals,
    displayCode: String,
    fallbackCode: String,
    showFallback: Boolean,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title_summary_report),
                style = MaterialTheme.contentText.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_numbers),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    icon = { Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_chart),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ikkala tab bitta Box'da o'lchanadi: sheet balandligi tablar orasida sakramaydi
            Box {
                // Details (Flat 3-row layout)
                Column(modifier = Modifier.hiddenUnless(selectedTab == 0)) {
                    FlatSummarySection(
                        title = stringResource(R.string.summary_total_title, totals.display.count),
                        subtitle = stringResource(R.string.summary_converted_subtitle, displayCode),
                        totals = totals.display,
                        currencyCode = displayCode
                    )

                    if (showFallback) {
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        FlatSummarySection(
                            title = stringResource(R.string.summary_fallback_title, totals.fallback.count),
                            subtitle = null,
                            totals = totals.fallback,
                            currencyCode = fallbackCode
                        )
                    }
                }
                // Donut Chart
                Column(modifier = Modifier.hiddenUnless(selectedTab == 1)) {
                    SummaryDonutChartView(
                        totals = totals.display,
                        currencyCode = displayCode,
                        title = stringResource(R.string.summary_total_title, totals.display.count)
                    )

                    if (showFallback) {
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        SummaryDonutChartView(
                            totals = totals.fallback,
                            currencyCode = fallbackCode,
                            title = stringResource(R.string.summary_fallback_title, totals.fallback.count)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

/** Faol bo'lmagan tab joyini egallaydi (balandlik uchun), lekin ko'rinmaydi va TalkBack'ka chiqmaydi. */
private fun Modifier.hiddenUnless(visible: Boolean): Modifier =
    if (visible) this else this.alpha(0f).clearAndSetSemantics { }
