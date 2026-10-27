package uz.moneymanager.ui.transactions

import android.app.DatePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.foundation.layout.WindowInsets
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import uz.moneymanager.R
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.domain.MonitoringFilter
import uz.moneymanager.domain.MonitoringTotals
import uz.moneymanager.domain.Totals
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.ui.theme.ExpenseColor
import uz.moneymanager.ui.theme.IncomeColor
import uz.moneymanager.ui.theme.barContainer
import uz.moneymanager.ui.theme.onBarContainer
import java.math.BigDecimal
import java.time.LocalDate

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
    val dailyTotals by viewModel.dailyTotals.collectAsState()
    val items = viewModel.transactions.collectAsLazyPagingItems()

    var transactionToDelete by remember { mutableStateOf<TransactionListItemUiModel.Item?>(null) }
    var showSummarySheet by remember { mutableStateOf(false) }
    var isAmountVisible by rememberSaveable { mutableStateOf(true) }

    val displayCurrency = currencies.firstOrNull { it.id == filter.displayCurrencyId } ?: defaultCurrency
    val dCode = displayCurrency?.code ?: "UZS"
    val fCode = defaultCurrency?.code ?: "UZS"
    val showFallback = totals.fallback.count > 0 && displayCurrency?.id != defaultCurrency?.id

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                        IconButton(onClick = { viewModel.resetFilters() }) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = stringResource(R.string.action_reset)
                            )
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
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add)
                )
            }
        },
        bottomBar = {
            // Footer: Jami summa
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { showSummarySheet = true }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isAmountVisible = !isAmountVisible },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAmountVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = stringResource(R.string.action_show_footer),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.action_show_footer),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        val totalText = if (isAmountVisible) {
                            FormatUtils.formatAmount(totals.display.balance, dCode, includePositiveSign = true)
                        } else {
                            "••••••"
                        }

                        Text(
                            text = totalText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isAmountVisible) {
                                if (totals.display.balance >= BigDecimal.ZERO) IncomeColor else ExpenseColor
                            } else MaterialTheme.colorScheme.onSurface
                        )
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
                        contentPadding = PaddingValues(bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(
                            count = items.itemCount,
                            key = items.itemKey { item ->
                                when (item) {
                                    is TransactionListItemUiModel.Header -> "h_${item.date}"
                                    is TransactionListItemUiModel.Item -> "t_${item.id}"
                                }
                            },
                            contentType = items.itemContentType { item ->
                                when (item) {
                                    is TransactionListItemUiModel.Header -> "header"
                                    is TransactionListItemUiModel.Item -> "transaction_row"
                                }
                            }
                        ) { index ->
                            when (val item = items[index]) {
                                is TransactionListItemUiModel.Header -> {
                                    TransactionDateHeader(
                                        dateText = item.dateText,
                                        totals = dailyTotals[item.date],
                                        displayCode = dCode,
                                        fallbackCode = fCode,
                                    )
                                }
                                is TransactionListItemUiModel.Item -> {
                                    TransactionItemRow(
                                        item = item,
                                        onClick = { onNavigateToEdit(item.id) },
                                        onDelete = { transactionToDelete = item }
                                    )
                                }
                                null -> Unit
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
        SummaryReportBottomSheet(
            onDismiss = { showSummarySheet = false },
            totals = totals,
            displayCode = dCode,
            fallbackCode = fCode,
            showFallback = showFallback
        )
    }
}

private val TransactionRowBaseStyle: TextStyle
    @Composable get() = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp)

@Composable
fun TransactionDateHeader(
    dateText: String,
    totals: MonitoringTotals?,
    displayCode: String,
    fallbackCode: String,
) {
    val baseStyle = TransactionRowBaseStyle

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateText,
                style = baseStyle.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            if (totals != null) {
                val hasDisplay = totals.display.count > 0
                val hasFallback = totals.fallback.count > 0 && displayCode != fallbackCode

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasDisplay) {
                        val isIncome = totals.display.balance >= BigDecimal.ZERO
                        Text(
                            text = FormatUtils.formatAmount(totals.display.balance, displayCode, includePositiveSign = true),
                            style = baseStyle.copy(fontWeight = FontWeight.Bold),
                            color = if (isIncome) IncomeColor else ExpenseColor,
                            maxLines = 1
                        )
                    } else if (!hasFallback) {
                        Text(
                            text = FormatUtils.formatAmount(BigDecimal.ZERO, displayCode, includePositiveSign = false),
                            style = baseStyle.copy(fontWeight = FontWeight.Bold),
                            color = IncomeColor,
                            maxLines = 1
                        )
                    }

                    if (hasFallback) {
                        val isIncome = totals.fallback.balance >= BigDecimal.ZERO
                        Text(
                            text = FormatUtils.formatAmount(totals.fallback.balance, fallbackCode, includePositiveSign = true),
                            style = baseStyle.copy(fontWeight = FontWeight.Bold),
                            color = if (isIncome) IncomeColor else ExpenseColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItemRow(
    item: TransactionListItemUiModel.Item,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val isIncome = item.type == TransactionType.INCOME
    val amountColor = if (isIncome) IncomeColor else ExpenseColor

    val baseStyle = TransactionRowBaseStyle
    val nameStyle = baseStyle.copy(fontWeight = FontWeight.SemiBold)
    val amountStyle = baseStyle.copy(fontWeight = FontWeight.Bold)
    val originalAmountStyle = baseStyle

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
                    .heightIn(min = 36.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chap tomon: Kategoriya nomi va kerak bo'lsa asl summa
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = item.title,
                        style = nameStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.originalAmountText != null) {
                        Text(
                            text = item.originalAmountText,
                            style = originalAmountStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // O'ng tomon: summa
                Text(
                    text = item.amountText,
                    style = amountStyle,
                    color = amountColor,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
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
        // Display Currency Selector
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

        // Currency Filter
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

        // Type Filter
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

        // Date Range Filter
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
            amount = FormatUtils.formatAmount(totals.income, currencyCode, TransactionType.INCOME),
            color = IncomeColor,
            isIncome = true
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        // Chiqim qatori
        SummaryRowItem(
            label = stringResource(R.string.label_expense),
            amount = FormatUtils.formatAmount(totals.expense, currencyCode, TransactionType.EXPENSE),
            color = ExpenseColor,
            isIncome = false
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        // Balans qatori
        SummaryRowItem(
            label = stringResource(R.string.label_balance),
            amount = FormatUtils.formatAmount(totals.balance, currencyCode, includePositiveSign = true),
            color = balanceColor(totals.balance),
            isBalance = true
        )
    }
}


/** Faol bo'lmagan tab joyini egallaydi (balandlik uchun), lekin ko'rinmaydi va TalkBack'ka chiqmaydi. */
private fun Modifier.hiddenUnless(visible: Boolean): Modifier =
    if (visible) this else this.alpha(0f).clearAndSetSemantics { }

/** Balans rangi: musbat - income, manfiy - expense, nol - onSurface (Details va Chart uchun yagona qoida). */
@Composable
private fun balanceColor(balance: BigDecimal): Color = when (balance.signum()) {
    1 -> IncomeColor
    -1 -> ExpenseColor
    else -> MaterialTheme.colorScheme.onSurface
}

/**
 * Yorliq chapda, summa o'ngda. Agar ikkalasi bitta qatorga sig'masa (masalan shrift 1.3x-2.0x),
 * summa yorliq ostiga tushadi va [stackedIndent] bilan yorliq matni chegarasida tekislanadi.
 */
@Composable
private fun AdaptiveAmountRow(
    label: @Composable () -> Unit,
    amount: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    stackedIndent: Dp = 0.dp,
) {
    Layout(contents = listOf(label, amount), modifier = modifier) { measurables, constraints ->
        val width = constraints.maxWidth
        val gap = 12.dp.roundToPx()
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val amountPlaceable = measurables[1].first().measure(loose)
        val labelFitsInRow = width - amountPlaceable.width - gap
        val labelInRow = measurables[0].first()
        if (labelInRow.maxIntrinsicWidth(constraints.maxHeight) <= labelFitsInRow) {
            val labelPlaceable = labelInRow.measure(loose.copy(maxWidth = labelFitsInRow))
            val height = maxOf(labelPlaceable.height, amountPlaceable.height)
            layout(width, height) {
                labelPlaceable.place(0, (height - labelPlaceable.height) / 2)
                amountPlaceable.place(width - amountPlaceable.width, (height - amountPlaceable.height) / 2)
            }
        } else {
            val labelPlaceable = labelInRow.measure(loose)
            val indent = stackedIndent.roundToPx().coerceAtMost((width - amountPlaceable.width).coerceAtLeast(0))
            layout(width, labelPlaceable.height + amountPlaceable.height) {
                labelPlaceable.place(0, 0)
                amountPlaceable.place(indent, labelPlaceable.height)
            }
        }
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
    AdaptiveAmountRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        stackedIndent = 34.dp,
        label = {
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
                    } else {
                        Icon(
                            imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
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
        },
        amount = {
            Text(
                text = amount,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                softWrap = false
            )
        }
    )
}

/** Legenda qatori: [dotColor] null bo'lsa nuqta o'rnida o'sha kenglikda bo'sh joy qoladi. */
@Composable
private fun LegendRow(
    label: String,
    amount: String,
    amountColor: Color,
    dotColor: Color?,
    bold: Boolean = false,
) {
    AdaptiveAmountRow(
        modifier = Modifier.fillMaxWidth(),
        stackedIndent = LegendDotSize + LegendDotGap,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(LegendDotSize)
                        .then(if (dotColor != null) Modifier.background(dotColor, CircleShape) else Modifier)
                )
                Spacer(modifier = Modifier.width(LegendDotGap))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        amount = {
            Text(
                text = amount,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor,
                maxLines = 1,
                softWrap = false
            )
        }
    )
}

private val LegendDotSize = 10.dp
private val LegendDotGap = 8.dp

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
        val balanceLabel = stringResource(R.string.label_balance)
        val balanceText = FormatUtils.formatAmount(totals.balance, currencyCode, includePositiveSign = true)
        val chartSemanticsDesc = if (totalVolume <= 0f) {
            emptyText
        } else {
            "$incomeLabel $incPct%, $expenseLabel $expPct%, $balanceLabel $balanceText"
        }

        // Donut markazi ataylab bo'sh: balans legendada ko'rsatiladi
        Canvas(
            modifier = Modifier
                .size(160.dp)
                .padding(14.dp)
                .semantics { contentDescription = chartSemanticsDesc }
        ) {
            if (totalVolume <= 0f) {
                drawCircle(
                    color = outlineVariantColor,
                    style = Stroke(width = strokeWidthPx)
                )
            } else {
                val incomeAngle = (incFloat / totalVolume) * 360f
                val expenseAngle = (expFloat / totalVolume) * 360f
                val gapAngle = if (incPct > 0 && expPct > 0) 3f else 0f
                val adjInc = (incomeAngle - gapAngle).coerceAtLeast(0f)
                val adjExp = (expenseAngle - gapAngle).coerceAtLeast(0f)

                drawArc(
                    color = incColor,
                    startAngle = -90f + gapAngle / 2,
                    sweepAngle = adjInc,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
                drawArc(
                    color = expColor,
                    startAngle = -90f + incomeAngle + gapAngle / 2,
                    sweepAngle = adjExp,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legenda: Income, Expense, divider, Balance
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LegendRow(
                label = "$incomeLabel ($incPct%)",
                amount = FormatUtils.formatAmount(totals.income, currencyCode, TransactionType.INCOME),
                amountColor = incColor,
                dotColor = incColor
            )
            LegendRow(
                label = "$expenseLabel ($expPct%)",
                amount = FormatUtils.formatAmount(totals.expense, currencyCode, TransactionType.EXPENSE),
                amountColor = expColor,
                dotColor = expColor
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            LegendRow(
                label = balanceLabel,
                amount = balanceText,
                amountColor = balanceColor(totals.balance),
                dotColor = null,
                bold = true
            )
        }
    }
}
