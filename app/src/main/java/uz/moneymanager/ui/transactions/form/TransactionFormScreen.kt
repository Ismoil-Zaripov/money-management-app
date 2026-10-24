package uz.moneymanager.ui.transactions.form

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.R
import uz.moneymanager.data.local.CurrencyEntity
import uz.moneymanager.domain.RateValidator
import uz.moneymanager.domain.TransactionDescription
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.ui.theme.ExpenseColor
import uz.moneymanager.ui.theme.IncomeColor
import uz.moneymanager.ui.theme.barContainer
import uz.moneymanager.ui.theme.onBarContainer
import uz.moneymanager.ui.transactions.FormatUtils
import java.math.BigDecimal
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private val FieldShape = RoundedCornerShape(12.dp)
private val ChipShape = RoundedCornerShape(8.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionFormViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var missingRatesEvent by remember { mutableStateOf<FormEvent.ShowMissingRates?>(null) }
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var amountHadFocus by rememberSaveable { mutableStateOf(false) }
    var categoryHadFocus by rememberSaveable { mutableStateOf(false) }
    var initialFocusRequested by rememberSaveable { mutableStateOf(false) }

    val typeColor = if (state.type == TransactionType.INCOME) IncomeColor else ExpenseColor
    val amountFocus = remember { FocusRequester() }
    val categoryFocus = remember { FocusRequester() }

    val handleBack = {
        focusManager.clearFocus()
        keyboard?.hide()
        if (state.isDirty && !state.isSaving) {
            showDiscardDialog = true
        } else {
            onNavigateBack()
        }
    }

    BackHandler(enabled = state.isDirty && !state.isSaving && !state.showNoInternetDialog) {
        handleBack()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FormEvent.Saved -> onNavigateBack()
                is FormEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
                    snackbarHostState.showSnackbar(msg)
                }
                is FormEvent.ShowMissingRates -> missingRatesEvent = event
            }
        }
    }

    // Yangi tranzaksiyada summa avtomatik fokusda va klaviatura ochiq (tahrirlashda emas)
    LaunchedEffect(state.isNew, state.isLoading) {
        if (state.isNew && !state.isLoading && !initialFocusRequested) {
            initialFocusRequested = true
            amountFocus.requestFocus()
            keyboard?.show()
        }
    }

    val onSave = {
        focusManager.clearFocus()
        keyboard?.hide()
        viewModel.save()
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(
                                if (state.isNew) R.string.title_new_transaction else R.string.title_edit_transaction
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = handleBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
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
        bottomBar = {
            SaveBar(isSaving = state.isSaving, enabled = state.canSave, onSave = onSave)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TypeSelector(type = state.type, onSelect = viewModel::setType)

                // Summa + biriktirilgan valyuta tanlagichi (faqat bitta joyda)
                AmountField(
                    amount = state.amountText,
                    onAmountChange = viewModel::setAmount,
                    currencyCode = state.currency?.code.orEmpty(),
                    currencies = state.currencies,
                    onSelectCurrency = viewModel::setCurrencyId,
                    color = typeColor,
                    errorText = state.amountErrorRes?.let { stringResource(it) },
                    modifier = Modifier
                        .focusRequester(amountFocus)
                        .onFocusLost(amountHadFocus, { amountHadFocus = true }, viewModel::onAmountBlur),
                    onNext = { categoryFocus.requestFocus() }
                )

                // Kategoriya: yozish mumkin bo'lgan ExposedDropdownMenu + tezkor chip'lar
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    var categoryMenuExpanded by remember { mutableStateOf(false) }
                    val filteredCategories = remember(state.categoryText, state.allCategories) {
                        if (state.categoryText.isBlank()) state.allCategories
                        else state.allCategories.filter { it.name.contains(state.categoryText.trim(), ignoreCase = true) }
                    }
                    val menuVisible = categoryMenuExpanded && filteredCategories.isNotEmpty()
                    val showCategoryError = state.categoryErrorRes != null

                    ExposedDropdownMenuBox(
                        expanded = menuVisible,
                        onExpandedChange = { categoryMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.categoryText,
                            onValueChange = {
                                viewModel.setCategoryText(it)
                                categoryMenuExpanded = true
                            },
                            label = { Text(stringResource(R.string.label_info)) },
                            trailingIcon = if (state.categoryText.isNotEmpty()) {
                                {
                                    IconButton(onClick = {
                                        viewModel.setCategoryText("")
                                        categoryMenuExpanded = false
                                    }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = stringResource(R.string.action_clear)
                                        )
                                    }
                                }
                            } else {
                                null
                            },
                            isError = showCategoryError,
                            supportingText = state.categoryErrorRes?.let { errRes ->
                                { Text(stringResource(errRes)) }
                            },
                            singleLine = true,
                            shape = FieldShape,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                categoryMenuExpanded = false
                                focusManager.clearFocus()
                            }),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryEditable)
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                                .focusRequester(categoryFocus)
                                .onFocusLost(categoryHadFocus, { categoryHadFocus = true }, viewModel::onCategoryBlur)
                        )
                        ExposedDropdownMenu(
                            expanded = menuVisible,
                            onDismissRequest = { categoryMenuExpanded = false }
                        ) {
                            filteredCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        viewModel.setCategoryText(cat.name)
                                        categoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (state.categorySuggestions.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.categorySuggestions, key = { it }) { name ->
                                val isSelected = name.equals(state.categoryText.trim(), ignoreCase = true)
                                ThemedFilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.setCategoryText(name)
                                        categoryMenuExpanded = false
                                    },
                                    label = name
                                )
                            }
                        }
                    }
                }

                DateSection(date = state.date, onDateChange = viewModel::setDate)

                // Tavsif: ixtiyoriy, xato faqat uzunlik chegarasidan oshganda ko'rinadi
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::setDescription,
                    label = {
                        Text(stringResource(R.string.label_optional, stringResource(R.string.label_description)))
                    },
                    isError = state.descriptionErrorRes != null,
                    supportingText = state.descriptionErrorRes?.let { errRes ->
                        { Text(stringResource(errRes, TransactionDescription.MAX_LENGTH)) }
                    },
                    maxLines = 4,
                    shape = FieldShape,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                )
            }
        }
    }

    // Discard Confirmation Dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.discard_changes_title)) },
            text = { Text(stringResource(R.string.discard_changes_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.action_discard),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.action_keep_editing))
                }
            }
        )
    }

    // No Internet Dialog
    if (state.showNoInternetDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissNoInternetDialog,
            title = { Text(stringResource(R.string.dialog_no_internet_title)) },
            text = { Text(stringResource(R.string.dialog_no_internet_desc)) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissNoInternetDialog) {
                    Text(stringResource(R.string.action_understood))
                }
            }
        )
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        stringResource(
                            R.string.dialog_missing_rates_desc,
                            event.base.code,
                            FormatUtils.formatDate(event.date)
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
                            shape = FieldShape,
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
                            missingRatesEvent = null
                            viewModel.save(parsedRates)
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

/** Maydon fokusda bo'lgan bo'lsa va fokusni yo'qotsa, [onLost] chaqiriladi ([hadFocus] holati caller'da saqlanadi). */
private fun Modifier.onFocusLost(hadFocus: Boolean, onFocused: () -> Unit, onLost: () -> Unit): Modifier =
    onFocusChanged { state ->
        if (state.isFocused) onFocused()
        else if (hadFocus) onLost()
    }

@Composable
private fun ThemedFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                maxLines = 1,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        shape = ChipShape,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            selectedBorderWidth = 1.dp,
        ),
        modifier = modifier.heightIn(min = 48.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeSelector(type: TransactionType, onSelect: (TransactionType) -> Unit) {
    val options = listOf(
        TransactionType.EXPENSE to R.string.type_expense,
        TransactionType.INCOME to R.string.type_income,
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (option, labelRes) ->
            val selected = type == option
            val semantic = if (option == TransactionType.INCOME) IncomeColor else ExpenseColor
            SegmentedButton(
                selected = selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = semantic.copy(alpha = 0.20f),
                    activeContentColor = semantic,
                    activeBorderColor = semantic,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = stringResource(labelRes),
                    maxLines = 1,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AmountField(
    amount: String,
    onAmountChange: (String) -> Unit,
    currencyCode: String,
    currencies: List<CurrencyEntity>,
    onSelectCurrency: (Long) -> Unit,
    color: Color,
    errorText: String?,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Mahalliy TextFieldValue: kursor/selection saqlanadi, ViewModel'ga faqat tozalangan matn yuboriladi
    var field by remember { mutableStateOf(TextFieldValue(amount, TextRange(amount.length))) }
    LaunchedEffect(amount) {
        if (amount != field.text) field = TextFieldValue(amount, TextRange(amount.length))
    }
    var isFocused by remember { mutableStateOf(false) }
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    val decimalSeparator = remember { DecimalFormatSymbols.getInstance().decimalSeparator }
    val transformation = remember(decimalSeparator) { AmountVisualTransformation(decimalSeparator) }
    val amountLabel = stringResource(R.string.label_amount)
    val currencyLabel = stringResource(R.string.label_currency)

    val borderColor = when {
        errorText != null -> MaterialTheme.colorScheme.error
        isFocused -> color
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val borderWidth = if (isFocused || errorText != null) 1.5.dp else 1.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = FieldShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(borderWidth, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoxWithConstraints(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val formattedLength = remember(field.text) {
                        val len = field.text.length
                        (len + (len - 1).coerceAtLeast(0) / 3).coerceAtLeast(1)
                    }
                    val density = LocalDensity.current
                    val maxWidthDp = with(density) { constraints.maxWidth.toDp().value }
                    val fontSizeSp = remember(formattedLength, maxWidthDp, density.fontScale) {
                        val preferred = when {
                            formattedLength <= 7 -> 32f
                            formattedLength <= 10 -> 26f
                            formattedLength <= 14 -> 22f
                            formattedLength <= 18 -> 18f
                            else -> 15f
                        }
                        val maxFit = (maxWidthDp / (formattedLength * 0.62f * density.fontScale)).coerceAtLeast(13f)
                        minOf(preferred, maxFit)
                    }

                    val textStyle = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = fontSizeSp.sp,
                        lineHeight = (fontSizeSp * 1.2f).sp,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    BasicTextField(
                        value = field,
                        onValueChange = { new ->
                            val sanitized = AmountInput.sanitize(new.text, isPaste = new.text.length - field.text.length > 1)
                            field = when (sanitized) {
                                new.text -> new
                                field.text -> field // rad etilgan belgi: matn va kursor o'zgarmaydi
                                else -> {
                                    val removed = new.text.length - sanitized.length
                                    val cursor = (new.selection.end - removed).coerceIn(0, sanitized.length)
                                    TextFieldValue(sanitized, TextRange(cursor))
                                }
                            }
                            if (field.text != amount) onAmountChange(field.text)
                        },
                        textStyle = textStyle,
                        singleLine = true,
                        cursorBrush = SolidColor(color),
                        visualTransformation = transformation,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { onNext() }),
                        modifier = modifier
                            .onFocusChanged { isFocused = it.isFocused }
                            .semantics { contentDescription = amountLabel }
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        decorationBox = { inner ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (field.text.isEmpty()) {
                                    Text(
                                        text = "0",
                                        style = textStyle.copy(color = color.copy(alpha = 0.38f)),
                                    )
                                }
                                inner()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    Surface(
                        onClick = { currencyMenuExpanded = true },
                        shape = ChipShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currencyCode.ifEmpty { currencyLabel },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = currencyLabel,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = currencyMenuExpanded,
                        onDismissRequest = { currencyMenuExpanded = false }
                    ) {
                        currencies.forEach { curr ->
                            DropdownMenuItem(
                                text = { Text("${curr.code} - ${curr.name}") },
                                onClick = {
                                    onSelectCurrency(curr.id)
                                    currencyMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSection(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val dateLabel = stringResource(R.string.label_date)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Butun maydon bosiladigan: readOnly field ustida shaffof clickable qatlam
        Box {
            OutlinedTextField(
                value = FormatUtils.formatDate(date),
                onValueChange = {},
                readOnly = true,
                label = { Text(dateLabel) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = dateLabel
                    )
                },
                singleLine = true,
                shape = FieldShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 8.dp)
                    .heightIn(min = 48.dp)
                    .clickable(role = Role.Button, onClickLabel = dateLabel) { showPicker = true }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemedFilterChip(
                selected = date == today,
                onClick = { onDateChange(today) },
                label = stringResource(R.string.date_today)
            )
            ThemedFilterChip(
                selected = date == yesterday,
                onClick = { onDateChange(yesterday) },
                label = stringResource(R.string.date_yesterday)
            )
        }
    }

    if (showPicker) {
        val todayUtcMillis = today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = remember(todayUtcMillis) {
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayUtcMillis
                    override fun isSelectableYear(year: Int) = year <= today.year
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        onDateChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    showPicker = false
                }) { Text(stringResource(R.string.action_apply)) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun SaveBar(isSaving: Boolean, enabled: Boolean, onSave: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
        ) {
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Button(
                onClick = { if (!isSaving) onSave() },
                // Saqlash paytida tugma "faol" ko'rinishda qoladi (progress ko'rinishi uchun), lekin bosish bloklangan
                enabled = enabled || isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 52.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current
                    )
                } else {
                    Text(
                        text = stringResource(R.string.action_save),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
