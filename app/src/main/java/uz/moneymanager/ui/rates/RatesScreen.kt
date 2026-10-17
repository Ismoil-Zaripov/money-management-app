package uz.moneymanager.ui.rates

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.R
import uz.moneymanager.data.local.ExchangeRateRow
import uz.moneymanager.domain.RateValidator
import uz.moneymanager.ui.theme.barContainer
import uz.moneymanager.ui.theme.onBarContainer
import uz.moneymanager.ui.transactions.FormatUtils
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesScreen(
    onNavigateBack: () -> Unit,
    viewModel: RatesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val date by viewModel.selectedDate.collectAsStateWithLifecycle()
    val currencies by viewModel.currencies.collectAsStateWithLifecycle()
    val rates by viewModel.rates.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var rateToEdit by remember { mutableStateOf<ExchangeRateRow?>(null) }
    var rateToDelete by remember { mutableStateOf<ExchangeRateRow?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RateEvent.Success -> {}
                is RateEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
                    snackbarHostState.showSnackbar(msg)
                }
                is RateEvent.CbuResult -> {
                    val msg = buildString {
                        append(context.getString(R.string.cbu_import_success, event.added, event.existing))
                        if (event.notFound.isNotEmpty()) {
                            append(" ")
                            append(context.getString(R.string.cbu_import_not_found, event.notFound.joinToString(", ")))
                        }
                    }
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.title_exchange_rates),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    },
                    actions = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(22.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onBarContainer
                            )
                        } else {
                            IconButton(onClick = { viewModel.importFromCbu() }) {
                                Icon(Icons.Default.CloudDownload, contentDescription = stringResource(R.string.action_import_cbu))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.barContainer,
                        titleContentColor = MaterialTheme.colorScheme.onBarContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBarContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onBarContainer
                    )
                )
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBarContainer.copy(alpha = 0.2f))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add_rate))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Date picker row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _, y, m, d -> viewModel.setDate(LocalDate.of(y, m + 1, d)) },
                            date.year,
                            date.monthValue - 1,
                            date.dayOfMonth
                        ).apply {
                            datePicker.maxDate = System.currentTimeMillis()
                        }.show()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stringResource(R.string.label_date)}: ${FormatUtils.formatDate(date)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Rates list
            Box(modifier = Modifier.fillMaxSize()) {
                if (rates.isEmpty() && !isLoading) {
                    Text(
                        text = stringResource(R.string.empty_rates),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(rates, key = { it.id }) { item ->
                            RateCard(
                                item = item,
                                onEdit = { rateToEdit = item },
                                onDelete = { rateToDelete = item }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Rate Dialog
    if (showAddDialog) {
        var baseId by remember { mutableStateOf(currencies.firstOrNull { it.isDefault }?.id ?: currencies.firstOrNull()?.id) }
        var quoteId by remember { mutableStateOf(currencies.firstOrNull { !it.isDefault }?.id ?: currencies.lastOrNull()?.id) }
        var rateText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.action_add_rate)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Base dropdown
                    var baseExpanded by remember { mutableStateOf(false) }
                    val baseCurr = currencies.firstOrNull { it.id == baseId }
                    ExposedDropdownMenuBox(
                        expanded = baseExpanded,
                        onExpandedChange = { baseExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = baseCurr?.code ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.label_base_currency)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = baseExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = baseExpanded,
                            onDismissRequest = { baseExpanded = false }
                        ) {
                            currencies.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.code} - ${c.name}") },
                                    onClick = {
                                        baseId = c.id
                                        baseExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Quote dropdown
                    var quoteExpanded by remember { mutableStateOf(false) }
                    val quoteCurr = currencies.firstOrNull { it.id == quoteId }
                    ExposedDropdownMenuBox(
                        expanded = quoteExpanded,
                        onExpandedChange = { quoteExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = quoteCurr?.code ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.label_quote_currency)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = quoteExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = quoteExpanded,
                            onDismissRequest = { quoteExpanded = false }
                        ) {
                            currencies.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.code} - ${c.name}") },
                                    onClick = {
                                        quoteId = c.id
                                        quoteExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = RateValidator.sanitize(it) },
                        label = { Text("1 ${baseCurr?.code ?: ""} = ? ${quoteCurr?.code ?: ""}") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = stringResource(
                            R.string.rate_inverse_hint,
                            quoteCurr?.code ?: "",
                            "...",
                            baseCurr?.code ?: ""
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = RateValidator.parse(rateText)
                        val b = baseId
                        val q = quoteId
                        if (parsed != null && b != null && q != null) {
                            viewModel.saveRate(b, q, parsed)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Edit Rate Dialog
    rateToEdit?.let { item ->
        var rateText by remember { mutableStateOf(item.rate.toPlainString()) }

        AlertDialog(
            onDismissRequest = { rateToEdit = null },
            title = { Text(stringResource(R.string.title_exchange_rates)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1 ${item.baseCode} = ? ${item.quoteCode}")
                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = RateValidator.sanitize(it) },
                        label = { Text(stringResource(R.string.label_rate)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = RateValidator.parse(rateText)
                        if (parsed != null) {
                            viewModel.saveRate(item.baseCurrencyId, item.quoteCurrencyId, parsed)
                            rateToEdit = null
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { rateToEdit = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Delete Confirmation
    rateToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { rateToDelete = null },
            title = { Text(stringResource(R.string.action_delete)) },
            text = { Text(stringResource(R.string.delete_rate_confirm, item.baseCode, item.quoteCode)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRate(item.baseCurrencyId, item.quoteCurrencyId)
                        rateToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { rateToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun RateCard(
    item: ExchangeRateRow,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rate text
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "1 ${item.baseCode}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "  =  ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${formatRate(item.rate)} ${item.quoteCode}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Actions: if locked, show a clean lock badge; if not locked, show edit/delete buttons
                    if (!item.locked) {
                        IconButton(onClick = onEdit) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.action_edit),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.action_delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                if (item.locked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Badge(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Text(
                            text = stringResource(R.string.rate_locked_badge),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}

private fun formatRate(rate: BigDecimal): String {
    val stripped = rate.stripTrailingZeros()
    return if (stripped.scale() <= 4) {
        val df = java.text.DecimalFormat("#,##0.####")
        df.format(stripped)
    } else {
        val df = java.text.DecimalFormat("#,##0.######")
        df.format(stripped)
    }
}
