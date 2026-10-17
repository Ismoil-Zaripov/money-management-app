package uz.moneymanager.ui.transactions.form

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import uz.moneymanager.R
import uz.moneymanager.ui.theme.barContainer
import uz.moneymanager.ui.theme.onBarContainer
import uz.moneymanager.domain.CategoryNames
import uz.moneymanager.domain.RateValidator
import uz.moneymanager.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionFormViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val type by viewModel.type.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val currencyId by viewModel.currencyId.collectAsState()
    val categoryText by viewModel.categoryText.collectAsState()
    val date by viewModel.date.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var missingRatesEvent by remember { mutableStateOf<FormEvent.ShowMissingRates?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FormEvent.Saved -> onNavigateBack()
                is FormEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
                    snackbarHostState.showSnackbar(msg)
                }
                is FormEvent.ShowMissingRates -> {
                    missingRatesEvent = event
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
                            text = stringResource(
                                if (viewModel.transactionId == null) R.string.title_new_transaction
                                else R.string.title_edit_transaction
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_cancel))
                        }
                    },
                    actions = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            IconButton(onClick = { viewModel.save() }) {
                                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.action_save))
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Segmented / Chips
            Text(stringResource(R.string.label_type), style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = type == TransactionType.EXPENSE,
                    onClick = { viewModel.setType(TransactionType.EXPENSE) },
                    label = { Text(stringResource(R.string.type_expense)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = type == TransactionType.INCOME,
                    onClick = { viewModel.setType(TransactionType.INCOME) },
                    label = { Text(stringResource(R.string.type_income)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Amount Input (Only positive allowed, no negative sign or zero)
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.setAmount(it) },
                label = { Text(stringResource(R.string.label_amount)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Currency Selector Dropdown
            var currencyDropdownExpanded by remember { mutableStateOf(false) }
            val selectedCurrency = currencies.firstOrNull { it.id == currencyId }

            ExposedDropdownMenuBox(
                expanded = currencyDropdownExpanded,
                onExpandedChange = { currencyDropdownExpanded = !currencyDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCurrency?.let { "${it.code} - ${it.name}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.label_currency)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = currencyDropdownExpanded,
                    onDismissRequest = { currencyDropdownExpanded = false }
                ) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(
                            text = { Text("${curr.code} - ${curr.name}") },
                            onClick = {
                                viewModel.setCurrencyId(curr.id)
                                currencyDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Info (Category) Editable Combobox / Autocomplete
            var categoryDropdownExpanded by remember { mutableStateOf(false) }
            val filteredCategories = remember(categoryText, allCategories) {
                if (categoryText.isBlank()) allCategories
                else allCategories.filter {
                    it.name.contains(categoryText, ignoreCase = true)
                }
            }

            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded && filteredCategories.isNotEmpty(),
                onExpandedChange = { categoryDropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = categoryText,
                    onValueChange = {
                        viewModel.setCategoryText(it)
                        categoryDropdownExpanded = true
                    },
                    label = { Text(stringResource(R.string.label_info)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    singleLine = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded && filteredCategories.isNotEmpty(),
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    filteredCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                viewModel.setCategoryText(cat.name)
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Date Picker Field
            OutlinedTextField(
                value = date.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_date)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    viewModel.setDate(LocalDate.of(year, month + 1, dayOfMonth))
                                },
                                date.year,
                                date.monthValue - 1,
                                date.dayOfMonth
                            ).apply {
                                datePicker.maxDate = System.currentTimeMillis()
                            }.show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                viewModel.setDate(LocalDate.of(year, month + 1, dayOfMonth))
                            },
                            date.year,
                            date.monthValue - 1,
                            date.dayOfMonth
                        ).apply {
                            datePicker.maxDate = System.currentTimeMillis()
                        }.show()
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }

    // Missing Rates Dialog
    missingRatesEvent?.let { event ->
        val rateInputs = remember(event) {
            mutableStateMapOf<Long, String>().apply {
                event.missing.forEach { quote ->
                    put(quote.id, event.suggestedRates[quote.id] ?: "")
                }
            }
        }

        AlertDialog(
            onDismissRequest = { missingRatesEvent = null },
            title = { Text(stringResource(R.string.dialog_missing_rates_title)) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(
                            R.string.dialog_missing_rates_desc,
                            event.base.code,
                            event.date.toString()
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    event.missing.forEach { quote ->
                        val currentVal = rateInputs[quote.id] ?: ""
                        OutlinedTextField(
                            value = currentVal,
                            onValueChange = { str ->
                                rateInputs[quote.id] = RateValidator.sanitize(str)
                            },
                            label = { Text("1 ${event.base.code} = ? ${quote.code}") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsedRates = mutableMapOf<Long, BigDecimal>()
                        var valid = true
                        for (quote in event.missing) {
                            val r = RateValidator.parse(rateInputs[quote.id] ?: "")
                            if (r == null) {
                                valid = false
                                break
                            }
                            parsedRates[quote.id] = r
                        }
                        if (valid) {
                            val toSave = missingRatesEvent
                            missingRatesEvent = null
                            if (toSave != null) {
                                viewModel.save(parsedRates)
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { missingRatesEvent = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}
