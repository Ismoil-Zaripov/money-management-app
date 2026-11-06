package uz.moneymanager.feature.paymentmethods.ui

import uz.moneymanager.core.designsystem.theme.ScreenHorizontalPadding
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmScaffold
import uz.moneymanager.core.designsystem.component.MmEmptyState
import uz.moneymanager.core.designsystem.component.MmExtendedFab
import uz.moneymanager.core.designsystem.component.MmSectionHeader
import uz.moneymanager.core.designsystem.component.MmTopBar
import uz.moneymanager.data.local.PaymentMethodWithUsage
import uz.moneymanager.feature.paymentmethods.presentation.PaymentMethodsUiState
import uz.moneymanager.feature.paymentmethods.ui.component.PaymentMethodCard
import uz.moneymanager.feature.paymentmethods.ui.component.PaymentMethodsInfoCard

private val ItemPadding = Modifier.padding(horizontal = ScreenHorizontalPadding)

@Composable
fun PaymentMethodsScreen(
    state: PaymentMethodsUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onRename: (PaymentMethodWithUsage) -> Unit,
    onDelete: (PaymentMethodWithUsage) -> Unit,
) {
    val total = state.paymentMethods.size
    MmScaffold(
        topBar = { MmTopBar(title = stringResource(R.string.title_payment_methods), onBack = onBack) },
        floatingActionButton = {
            MmExtendedFab(text = stringResource(R.string.action_add_payment_method), onClick = onAddClick)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (total == 0) {
            MmEmptyState(
                title = stringResource(R.string.empty_payment_methods),
                message = stringResource(R.string.payment_methods_empty_hint),
                modifier = Modifier.padding(padding)
            )
            return@MmScaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 88.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "info", contentType = "info") { PaymentMethodsInfoCard(ItemPadding) }
            item(key = "header", contentType = "header") {
                MmSectionHeader(
                    title = stringResource(R.string.payment_methods_section_title),
                    trailing = pluralStringResource(R.plurals.payment_methods_registered_count, total, total)
                )
            }
            items(state.paymentMethods, key = { it.paymentMethod.id }, contentType = { "method" }) { item ->
                PaymentMethodCard(
                    item = item,
                    onRename = { onRename(item) },
                    onDelete = { onDelete(item) },
                    modifier = ItemPadding
                )
            }
        }
    }
}
