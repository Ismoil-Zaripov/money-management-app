package uz.moneymanager.ui.transactions

import android.app.DatePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import uz.moneymanager.R
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.data.local.TransactionListRow
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.MonitoringTotals
import uz.moneymanager.domain.Totals
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.ui.theme.ExpenseColor
import uz.moneymanager.ui.theme.IncomeColor
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatAmount(
    amount: BigDecimal,
    currencyCode: String,
    type: TransactionType? = null
): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '
    }
    val df = DecimalFormat("#,##0.##", symbols)
    val sign = when (type) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "−"
        null -> ""
    }
    return "$sign${df.format(amount)} $currencyCode"
}

object NumberFormatter {
    private val wholeDf = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' })
    private val decimalDf = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' })

    fun format(value: BigDecimal): String {
        return if (value.stripTrailingZeros().scale() <= 0) {
            wholeDf.format(value)
        } else {
            decimalDf.format(value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val filter by viewModel.filter.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val defaultCurrency by viewModel.defaultCurrency.collectAsState()
    val totals by viewModel.totals.collectAsState()
    val items = viewModel.transactions.collectAsLazyPagingItems()

    var transactionToDelete by remember { mutableStateOf<TransactionListRow?>(null) }
    var showSummarySheet by remember { mutableStateOf(false) }

    val displayCurrency = currencies.firstOrNull { it.id == filter.displayCurrencyId } ?: defaultCurrency

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.title_transactions),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = { showSummarySheet = true }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = stringResource(R.string.action_show_summary)
                            )
                        }
                        IconButton(onClick = { viewModel.resetFilters() }) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = stringResource(R.string.action_reset)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Bar
            TransactionFilterBar(
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

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Transaction List
            Box(modifier = Modifier.fillMaxSize()) {
                if (items.loadState.refresh is LoadState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (items.itemCount == 0 && items.loadState.refresh !is LoadState.Loading) {
                    Text(
                        text = stringResource(R.string.empty_transactions),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(count = items.itemCount) { index ->
                            val item = items[index]
                            if (item != null) {
                                TransactionItem(
                                    item = item,
                                    onClick = { onNavigateToEdit(item.id) },
                                    onDelete = { transactionToDelete = item }
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
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(
    item: TransactionListRow,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val isIncome = item.type == TransactionType.INCOME
    val amountColor = if (isIncome) IncomeColor else ExpenseColor

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onDelete
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chap tomon: 1-qator nom (kategoriya), 2-qator sana
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = item.categoryName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.date.format(dateFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // O'ng tomon: summa (bold, kirim/chiqim belgisi va rangi bilan)
                Text(
                    text = formatAmount(item.amount, item.currencyCode, item.type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
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

@Composable
fun TransactionFilterBar(
    filter: MonitoringFilter,
    currencies: List<CurrencyEntity>,
    categories: List<CategoryEntity>,
    displayCurrency: CurrencyEntity?,
    onCategorySelect: (Long?) -> Unit,
    onCurrencyFilterSelect: (Long?) -> Unit,
    onDisplayCurrencySelect: (Long) -> Unit,
    onTypeSelect: (TransactionType?) -> Unit,
    onDateRangeSelect: (LocalDate?, LocalDate?) -> Unit,
) {
    val context = LocalContext.current
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showCurrencyFilterMenu by remember { mutableStateOf(false) }
    var showDisplayCurrencyMenu by remember { mutableStateOf(false) }
    var showTypeMenu by remember { mutableStateOf(false) }

    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        containerColor = MaterialTheme.colorScheme.surface,
        labelColor = MaterialTheme.colorScheme.onSurface,
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display Currency Selector (Which currency to convert to)
        item {
            Box {
                FilterChip(
                    selected = true,
                    onClick = { showDisplayCurrencyMenu = true },
                    label = { Text("${stringResource(R.string.display_currency)}: ${displayCurrency?.code ?: "UZS"}") },
                    colors = chipColors
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
        }

        // Category Filter
        item {
            val selectedCategory = categories.firstOrNull { it.id == filter.categoryId }
            Box {
                FilterChip(
                    selected = filter.categoryId != null,
                    onClick = { showCategoryMenu = true },
                    label = { Text("${stringResource(R.string.filter_category)}: ${selectedCategory?.name ?: stringResource(R.string.filter_all)}") },
                    colors = chipColors
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
        }

        // Currency Filter (Only show transactions made in this currency)
        item {
            val selectedCurrencyFilter = currencies.firstOrNull { it.id == filter.currencyId }
            Box {
                FilterChip(
                    selected = filter.currencyId != null,
                    onClick = { showCurrencyFilterMenu = true },
                    label = { Text("${stringResource(R.string.filter_currency)}: ${selectedCurrencyFilter?.code ?: stringResource(R.string.filter_all)}") },
                    colors = chipColors
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
        }

        // Type Filter (Income / Expense / All)
        item {
            Box {
                val typeLabel = when (filter.type) {
                    TransactionType.INCOME -> stringResource(R.string.type_income)
                    TransactionType.EXPENSE -> stringResource(R.string.type_expense)
                    null -> stringResource(R.string.filter_all)
                }
                FilterChip(
                    selected = filter.type != null,
                    onClick = { showTypeMenu = true },
                    label = { Text("${stringResource(R.string.filter_type)}: $typeLabel") },
                    colors = chipColors
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
        }

        // Date Range Filter (From / To)
        item {
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
                colors = chipColors,
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
}

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
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Rows, 1 = Donut Chart

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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
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

            if (selectedTab == 0) {
                // Flat 3-row layout without nested cards
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
            } else {
                // Aylana diagramma (Donut Chart)
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

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun FlatSummarySection(
    title: String,
    subtitle: String?,
    totals: Totals,
    currencyCode: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Kirim qatori
        SummaryRowItem(
            label = stringResource(R.string.label_income),
            amount = "+${NumberFormatter.format(totals.income)} $currencyCode",
            color = IncomeColor,
            isIncome = true
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        // Chiqim qatori
        SummaryRowItem(
            label = stringResource(R.string.label_expense),
            amount = "−${NumberFormatter.format(totals.expense)} $currencyCode",
            color = ExpenseColor,
            isIncome = false
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        // Balans qatori
        val balColor = if (totals.balance >= BigDecimal.ZERO) IncomeColor else ExpenseColor
        val balSign = if (totals.balance > BigDecimal.ZERO) "+" else if (totals.balance < BigDecimal.ZERO) "−" else ""
        SummaryRowItem(
            label = stringResource(R.string.label_balance),
            amount = "$balSign${NumberFormatter.format(totals.balance.abs())} $currencyCode",
            color = balColor,
            isBalance = true
        )
    }
}

@Composable
fun SummaryRowItem(
    label: String,
    amount: String,
    color: Color,
    isIncome: Boolean = false,
    isBalance: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isBalance) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, shape = CircleShape)
                    )
                } else if (isIncome) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isBalance) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun SummaryDonutChartView(
    totals: Totals,
    currencyCode: String,
    title: String? = null,
) {
    val density = LocalDensity.current
    val strokeWidthPx = remember(density) { with(density) { 26.dp.toPx() } }
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val incColor = IncomeColor
    val expColor = ExpenseColor

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val incFloat = totals.income.toFloat()
        val expFloat = totals.expense.toFloat()
        val totalVolume = incFloat + expFloat

        val incPct = if (totalVolume > 0f) (incFloat / totalVolume * 100).toInt() else 0
        val expPct = if (totalVolume > 0f) 100 - incPct else 0

        val emptyText = stringResource(R.string.empty_transactions)
        val incomeLabel = stringResource(R.string.label_income)
        val expenseLabel = stringResource(R.string.label_expense)
        val chartSemanticsDesc = if (totalVolume <= 0f) {
            emptyText
        } else {
            "$incomeLabel: $incPct%, $expenseLabel: $expPct%"
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(160.dp)
                .semantics { contentDescription = chartSemanticsDesc }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                if (totalVolume <= 0f) {
                    drawCircle(
                        color = outlineVariantColor,
                        style = Stroke(width = strokeWidthPx)
                    )
                } else {
                    val incomeAngle = (incFloat / totalVolume) * 360f
                    val expenseAngle = (expFloat / totalVolume) * 360f

                    drawArc(
                        color = incColor,
                        startAngle = -90f,
                        sweepAngle = incomeAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                    )
                    drawArc(
                        color = expColor,
                        startAngle = -90f + incomeAngle,
                        sweepAngle = expenseAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                    )
                }
            }

            // Halqaning o'rtasi: Balans summasi va valyuta
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                if (totalVolume <= 0f) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val balSign = if (totals.balance > BigDecimal.ZERO) "+" else if (totals.balance < BigDecimal.ZERO) "−" else ""
                    val balColor = if (totals.balance >= BigDecimal.ZERO) incColor else expColor
                    Text(
                        text = "$balSign${NumberFormatter.format(totals.balance.abs())}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = balColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currencyCode,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Legend: Kirim va chiqim foizlari va summalari
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(incColor, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$incomeLabel ($incPct%): +${NumberFormatter.format(totals.income)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(expColor, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$expenseLabel ($expPct%): −${NumberFormatter.format(totals.expense)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
