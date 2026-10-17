package uz.moneymanager.ui.monitoring

import android.app.DatePickerDialog
import java.math.BigDecimal
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import uz.moneymanager.R
import uz.moneymanager.data.local.TransactionListRow
import uz.moneymanager.domain.Totals
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.ui.theme.ExpenseRed
import uz.moneymanager.ui.theme.IncomeGreen
import uz.moneymanager.ui.transactions.NumberFormatter
import uz.moneymanager.ui.transactions.TransactionItem
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreen(
    onNavigateToEdit: (Long) -> Unit,
    viewModel: MonitoringViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val filter by viewModel.filter.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val defaultCurrency by viewModel.defaultCurrency.collectAsState()
    val totals by viewModel.totals.collectAsState()
    val items = viewModel.transactions.collectAsLazyPagingItems()

    var transactionToDelete by remember { mutableStateOf<TransactionListRow?>(null) }

    val displayCurrency = currencies.firstOrNull { it.id == filter.displayCurrencyId } ?: defaultCurrency
    val fallbackCurrency = defaultCurrency
    var showSummarySheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_monitoring)) },
                actions = {
                    IconButton(onClick = { viewModel.resetFilters() }) {
                        Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.action_reset))
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { showSummarySheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.action_show_footer))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter bar
            MonitoringFilterBar(
                filter = filter,
                currencies = currencies,
                categories = categories,
                displayCurrency = displayCurrency,
                onCategorySelect = { viewModel.setCategory(it) },
                onCurrencyFilterSelect = { viewModel.setCurrencyFilter(it) },
                onDisplayCurrencySelect = { viewModel.setDisplayCurrency(it) },
                onTypeSelect = { viewModel.setType(it) },
                onDateRangeSelect = { from, to -> viewModel.setDateRange(from, to) }
            )

            HorizontalDivider()

            // Transactions list
            Box(modifier = Modifier.fillMaxSize()) {
                if (items.loadState.refresh is LoadState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (items.itemCount == 0 && items.loadState.refresh !is LoadState.Loading) {
                    Text(
                        text = stringResource(R.string.empty_transactions),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(count = items.itemCount) { index ->
                            val item = items[index]
                            if (item != null) {
                                val converted: Pair<BigDecimal, String>? = when {
                                    item.displayRate != null && displayCurrency != null ->
                                        item.amount.multiply(item.displayRate) to displayCurrency.code
                                    item.fallbackRate != null && fallbackCurrency != null ->
                                        item.amount.multiply(item.fallbackRate) to fallbackCurrency.code
                                    else -> null
                                }

                                TransactionItem(
                                    item = item,
                                    onClick = { onNavigateToEdit(item.id) },
                                    onDelete = { transactionToDelete = item },
                                    convertedAmount = converted?.first,
                                    convertedCode = converted?.second
                                )
                            }
                        }

                        if (items.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    transactionToDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text(stringResource(R.string.action_delete)) },
            text = { Text(stringResource(R.string.delete_transaction_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(tx.id)
                        transactionToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (showSummarySheet) {
        val dCode = displayCurrency?.code ?: "UZS"
        val fCode = defaultCurrency?.code ?: "UZS"
        val showFallback = totals.fallback.count > 0 && displayCurrency?.id != defaultCurrency?.id

        SummaryReportBottomSheet(
            onDismiss = { showSummarySheet = false },
            totals = totals,
            displayCode = dCode,
            fallbackCode = fCode,
            showFallback = showFallback
        )
    }
}

@Composable
fun TotalsSummaryRow(
    title: String,
    totals: Totals,
    currencyCode: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Income card/col
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_income),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "+${NumberFormatter.format(totals.income)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = IncomeGreen,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Expense card/col
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_expense),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "-${NumberFormatter.format(totals.expense)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ExpenseRed,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            // Balance card/col
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.label_balance),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                val balanceColor = if (totals.balance >= java.math.BigDecimal.ZERO) IncomeGreen else ExpenseRed
                val prefix = if (totals.balance > java.math.BigDecimal.ZERO) "+" else ""
                Text(
                    text = "$prefix${NumberFormatter.format(totals.balance)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = balanceColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryReportBottomSheet(
    onDismiss: () -> Unit,
    totals: uz.moneymanager.domain.MonitoringTotals,
    displayCode: String,
    fallbackCode: String,
    showFallback: Boolean,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableStateOf(0) } // 0 = Rows, 1 = Pie Chart

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            TabRow(selectedTabIndex = selectedTab) {
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

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Type View 1: Har biri alohida qatordan to'liq va ixcham MUI uslubida
                SummaryGroupCard(
                    title = stringResource(R.string.footer_title_display, displayCode, totals.display.count),
                    totals = totals.display,
                    currencyCode = displayCode
                )

                if (showFallback) {
                    Spacer(modifier = Modifier.height(10.dp))
                    SummaryGroupCard(
                        title = stringResource(R.string.footer_title_fallback, fallbackCode, totals.fallback.count),
                        totals = totals.fallback,
                        currencyCode = fallbackCode
                    )
                }
            } else {
                // Type View 2: Aylana diagramma (Pie Chart)
                SummaryPieChartView(
                    totals = totals.display,
                    currencyCode = displayCode
                )

                if (showFallback) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    SummaryPieChartView(
                        totals = totals.fallback,
                        currencyCode = fallbackCode,
                        title = stringResource(R.string.footer_title_fallback, fallbackCode, totals.fallback.count)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SummaryGroupCard(
    title: String,
    totals: Totals,
    currencyCode: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()

            // Kirim MUI field (label kichik bolib amount ustida turadi)
            MuiSummaryField(
                label = stringResource(R.string.label_income),
                amount = "+${NumberFormatter.format(totals.income)} $currencyCode",
                amountColor = IncomeGreen
            )

            // Chiqim MUI field (label kichik bolib amount ustida turadi)
            MuiSummaryField(
                label = stringResource(R.string.label_expense),
                amount = "-${NumberFormatter.format(totals.expense)} $currencyCode",
                amountColor = ExpenseRed
            )

            // Balans MUI field (label kichik bolib amount ustida turadi)
            val balColor = if (totals.balance >= java.math.BigDecimal.ZERO) IncomeGreen else ExpenseRed
            val prefix = if (totals.balance > java.math.BigDecimal.ZERO) "+" else ""
            MuiSummaryField(
                label = stringResource(R.string.label_balance),
                amount = "$prefix${NumberFormatter.format(totals.balance)} $currencyCode",
                amountColor = balColor
            )
        }
    }
}

@Composable
fun MuiSummaryField(
    label: String,
    amount: String,
    amountColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
        }
    }
}

@Composable
fun SummaryPieChartView(
    totals: Totals,
    currencyCode: String,
    title: String? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        val incFloat = totals.income.toFloat()
        val expFloat = totals.expense.toFloat()
        val totalVolume = incFloat + expFloat

        if (totalVolume <= 0f) {
            Text(
                text = stringResource(R.string.empty_transactions),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val incomeAngle = (incFloat / totalVolume) * 360f
            val expenseAngle = (expFloat / totalVolume) * 360f

            val incPct = (incFloat / totalVolume * 100).toInt()
            val expPct = 100 - incPct

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .padding(4.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw Income Arc
                    drawArc(
                        color = IncomeGreen,
                        startAngle = -90f,
                        sweepAngle = incomeAngle,
                        useCenter = true
                    )
                    // Draw Expense Arc
                    drawArc(
                        color = ExpenseRed,
                        startAngle = -90f + incomeAngle,
                        sweepAngle = expenseAngle,
                        useCenter = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(IncomeGreen, shape = MaterialTheme.shapes.extraSmall)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stringResource(R.string.label_income)}: $incPct%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(ExpenseRed, shape = MaterialTheme.shapes.extraSmall)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stringResource(R.string.label_expense)}: $expPct%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val balColor = if (totals.balance >= java.math.BigDecimal.ZERO) IncomeGreen else ExpenseRed
            val prefix = if (totals.balance > java.math.BigDecimal.ZERO) "+" else ""
            Text(
                text = "${stringResource(R.string.label_balance)}: $prefix${NumberFormatter.format(totals.balance)} $currencyCode",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = balColor
            )
        }
    }
}

@Composable
fun MonitoringFilterBar(
    filter: uz.moneymanager.domain.MonitoringFilter,
    currencies: List<uz.moneymanager.data.local.CurrencyEntity>,
    categories: List<uz.moneymanager.data.local.CategoryEntity>,
    displayCurrency: uz.moneymanager.data.local.CurrencyEntity?,
    onCategorySelect: (Long?) -> Unit,
    onCurrencyFilterSelect: (Long?) -> Unit,
    onDisplayCurrencySelect: (Long) -> Unit,
    onTypeSelect: (TransactionType?) -> Unit,
    onDateRangeSelect: (LocalDate?, LocalDate?) -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showCategoryMenu by remember { mutableStateOf(false) }
    var showCurrencyFilterMenu by remember { mutableStateOf(false) }
    var showDisplayCurrencyMenu by remember { mutableStateOf(false) }
    var showTypeMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display Currency Selector (Which currency to convert to)
        Box {
            FilterChip(
                selected = true,
                onClick = { showDisplayCurrencyMenu = true },
                label = { Text("${stringResource(R.string.display_currency)}: ${displayCurrency?.code ?: "UZS"}") }
            )
            DropdownMenu(
                expanded = showDisplayCurrencyMenu,
                onDismissRequest = { showDisplayCurrencyMenu = false }
            ) {
                currencies.forEach { c ->
                    DropdownMenuItem(
                        text = { Text("${c.code} - ${c.name}") },
                        onClick = {
                            onDisplayCurrencySelect(c.id)
                            showDisplayCurrencyMenu = false
                        }
                    )
                }
            }
        }

        // Info / Category Filter
        val selectedCategory = categories.firstOrNull { it.id == filter.categoryId }
        Box {
            FilterChip(
                selected = filter.categoryId != null,
                onClick = { showCategoryMenu = true },
                label = { Text("${stringResource(R.string.filter_category)}: ${selectedCategory?.name ?: stringResource(R.string.filter_all)}") }
            )
            DropdownMenu(
                expanded = showCategoryMenu,
                onDismissRequest = { showCategoryMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.filter_all)) },
                    onClick = {
                        onCategorySelect(null)
                        showCategoryMenu = false
                    }
                )
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            onCategorySelect(cat.id)
                            showCategoryMenu = false
                        }
                    )
                }
            }
        }

        // Currency Filter (Only show transactions made in this currency)
        val selectedCurrencyFilter = currencies.firstOrNull { it.id == filter.currencyId }
        Box {
            FilterChip(
                selected = filter.currencyId != null,
                onClick = { showCurrencyFilterMenu = true },
                label = { Text("${stringResource(R.string.filter_currency)}: ${selectedCurrencyFilter?.code ?: stringResource(R.string.filter_all)}") }
            )
            DropdownMenu(
                expanded = showCurrencyFilterMenu,
                onDismissRequest = { showCurrencyFilterMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.filter_all)) },
                    onClick = {
                        onCurrencyFilterSelect(null)
                        showCurrencyFilterMenu = false
                    }
                )
                currencies.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.code) },
                        onClick = {
                            onCurrencyFilterSelect(c.id)
                            showCurrencyFilterMenu = false
                        }
                    )
                }
            }
        }

        // Type Filter (Income / Expense / All)
        Box {
            val typeLabel = when (filter.type) {
                TransactionType.INCOME -> stringResource(R.string.type_income)
                TransactionType.EXPENSE -> stringResource(R.string.type_expense)
                null -> stringResource(R.string.filter_all)
            }
            FilterChip(
                selected = filter.type != null,
                onClick = { showTypeMenu = true },
                label = { Text("${stringResource(R.string.filter_type)}: $typeLabel") }
            )
            DropdownMenu(
                expanded = showTypeMenu,
                onDismissRequest = { showTypeMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.filter_all)) },
                    onClick = {
                        onTypeSelect(null)
                        showTypeMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.type_income)) },
                    onClick = {
                        onTypeSelect(TransactionType.INCOME)
                        showTypeMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.type_expense)) },
                    onClick = {
                        onTypeSelect(TransactionType.EXPENSE)
                        showTypeMenu = false
                    }
                )
            }
        }

        // Date Range Filter (From / To)
        val dateLabel = if (filter.from != null || filter.to != null) {
            "${filter.from?.toString() ?: "..."} - ${filter.to?.toString() ?: "..."}"
        } else {
            stringResource(R.string.filter_date_range)
        }

        FilterChip(
            selected = filter.from != null || filter.to != null,
            onClick = {
                val now = LocalDate.now()
                val currentFrom = filter.from ?: now
                DatePickerDialog(
                    context,
                    { _, y1, m1, d1 ->
                        val fromDate = LocalDate.of(y1, m1 + 1, d1)
                        DatePickerDialog(
                            context,
                            { _, y2, m2, d2 ->
                                val toDate = LocalDate.of(y2, m2 + 1, d2)
                                onDateRangeSelect(fromDate, toDate)
                            },
                            fromDate.year,
                            fromDate.monthValue - 1,
                            fromDate.dayOfMonth
                        ).show()
                    },
                    currentFrom.year,
                    currentFrom.monthValue - 1,
                    currentFrom.dayOfMonth
                ).show()
            },
            label = { Text(dateLabel) },
            trailingIcon = {
                if (filter.from != null || filter.to != null) {
                    IconButton(
                        onClick = { onDateRangeSelect(null, null) },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            }
        )
    }
}
