package uz.moneymanager.feature.transactions.ui.sheet.summary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SummaryHeader(onClose = { scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() } })
            Spacer(Modifier.height(12.dp))
            SummaryTabs(selected = selectedTab, onSelect = { selectedTab = it })
            Spacer(Modifier.height(16.dp))

            // Ikkala tab bitta Box'da o'lchanadi: sheet balandligi tablar orasida sakramaydi
            Box {
                SummaryDetailsContent(
                    totals = totals,
                    displayCode = displayCode,
                    fallbackCode = fallbackCode,
                    showFallback = showFallback,
                    modifier = Modifier.hiddenUnless(selectedTab == 0)
                )
                SummaryPieContent(
                    totals = totals,
                    displayCode = displayCode,
                    fallbackCode = fallbackCode,
                    showFallback = showFallback,
                    active = selectedTab == 1,
                    modifier = Modifier.hiddenUnless(selectedTab == 1)
                )
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

/** Faol bo'lmagan tab joyini egallaydi (balandlik uchun), lekin ko'rinmaydi va TalkBack'ka chiqmaydi. */
private fun Modifier.hiddenUnless(visible: Boolean): Modifier =
    if (visible) this else this.alpha(0f).clearAndSetSemantics { }
