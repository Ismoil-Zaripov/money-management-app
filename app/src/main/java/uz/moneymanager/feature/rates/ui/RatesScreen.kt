package uz.moneymanager.feature.rates.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.data.local.ExchangeRateRow
import uz.moneymanager.feature.rates.presentation.RatesUiState
import uz.moneymanager.feature.rates.ui.component.RateCard
import uz.moneymanager.feature.rates.ui.component.RatesDateRow
import uz.moneymanager.feature.rates.ui.component.RatesTopBar

@Composable
fun RatesScreen(
    state: RatesUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onImport: () -> Unit,
    onAddClick: () -> Unit,
    onDateClick: () -> Unit,
    onEdit: (ExchangeRateRow) -> Unit,
    onDelete: (ExchangeRateRow) -> Unit,
) {
    Scaffold(
        topBar = { RatesTopBar(isLoading = state.isLoading, onBack = onBack, onImport = onImport) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add_rate))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            RatesDateRow(date = state.date, onClick = onDateClick)
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.rates.isEmpty() && !state.isLoading) {
                    Text(
                        text = stringResource(R.string.empty_rates),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.contentText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(state.rates, key = { it.id }) { item ->
                            RateCard(
                                item = item,
                                onEdit = { onEdit(item) },
                                onDelete = { onDelete(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}
