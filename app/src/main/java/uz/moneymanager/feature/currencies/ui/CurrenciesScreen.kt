package uz.moneymanager.feature.currencies.ui

import androidx.compose.foundation.layout.Arrangement
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
import uz.moneymanager.data.local.CurrencyWithUsage
import uz.moneymanager.feature.currencies.presentation.CurrenciesUiState
import uz.moneymanager.feature.currencies.ui.component.CurrencyCard
import uz.moneymanager.feature.currencies.ui.component.CurrencyIntegrityNote

@Composable
fun CurrenciesScreen(
    state: CurrenciesUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEdit: (CurrencyWithUsage) -> Unit,
    onDelete: (CurrencyWithUsage) -> Unit,
) {
    MmScaffold(
        topBar = { MmTopBar(title = stringResource(R.string.title_currencies), onBack = onBack) },
        floatingActionButton = {
            MmExtendedFab(text = stringResource(R.string.action_add_currency), onClick = onAddClick)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                end = 16.dp,
                bottom = padding.calculateBottomPadding() + 88.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "integrity_note") { CurrencyIntegrityNote() }
            items(state.currencies, key = { it.currency.id }) { item ->
                CurrencyCard(
                    item = item,
                    onEdit = { onEdit(item) },
                    onDelete = { onDelete(item) }
                )
            }
        }
    }
}
