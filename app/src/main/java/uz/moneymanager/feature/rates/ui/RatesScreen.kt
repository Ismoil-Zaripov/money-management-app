package uz.moneymanager.feature.rates.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmScaffold
import uz.moneymanager.core.designsystem.component.MmExtendedFab
import uz.moneymanager.core.designsystem.component.MmTopBar
import uz.moneymanager.data.local.ExchangeRateRow
import uz.moneymanager.feature.rates.presentation.RatesUiState
import uz.moneymanager.feature.rates.ui.component.RateCard
import uz.moneymanager.feature.rates.ui.component.RatesDateRow
import uz.moneymanager.feature.rates.ui.component.RatesEmptyState
import uz.moneymanager.feature.rates.ui.component.RatesImportRow
import uz.moneymanager.feature.rates.ui.component.RatesListHeader
import java.time.LocalDate

@Composable
fun RatesScreen(
    state: RatesUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onImport: () -> Unit,
    onAddClick: () -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onPickDate: () -> Unit,
    onEdit: (ExchangeRateRow) -> Unit,
    onDelete: (ExchangeRateRow) -> Unit,
) {
    MmScaffold(
        topBar = { MmTopBar(title = stringResource(R.string.title_exchange_rates), onBack = onBack) },
        floatingActionButton = {
            MmExtendedFab(text = stringResource(R.string.action_add_rate), onClick = onAddClick)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            RatesDateRow(date = state.date, onDateChange = onDateChange, onPickDate = onPickDate)
            RatesImportRow(isLoading = state.isLoading, onImport = onImport)
            if (state.rates.isEmpty() && !state.isLoading) {
                RatesEmptyState(date = state.date, onFetch = onImport)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = padding.calculateBottomPadding() + 88.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "header", contentType = "header") { RatesListHeader(state.rates.size) }
                    items(state.rates, key = { it.id }, contentType = { "rate" }) { item ->
                        RateCard(item = item, onEdit = { onEdit(item) }, onDelete = { onDelete(item) })
                    }
                }
            }
        }
    }
}
