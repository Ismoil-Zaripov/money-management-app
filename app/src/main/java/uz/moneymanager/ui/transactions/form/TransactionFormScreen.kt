package uz.moneymanager.ui.transactions.form

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.R
import uz.moneymanager.domain.RateValidator
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

    BackHandler(enabled = state.isDirty && !state.isSaving) {
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
    LaunchedEffect(state.isNew) {
        if (state.isNew && !initialFocusRequested) {
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                TypeSelector(type = state.type, onSelect = viewModel::setType)

                // Summa + valyuta chip'i
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AmountField(
                        amount = state.amountText,
                        onAmountChange = viewModel::setAmount,
                        currencyCode = state.currency?.code.orEmpty(),
                        color = typeColor,
                        errorText = state.amountErrorRes?.let { stringResource(it) },
                        modifier = Modifier
                            .focusRequester(amountFocus)
                            .onFocusLost(amountHadFocus, { amountHadFocus = true }, viewModel::onAmountBlur),
                        onNext = { categoryFocus.requestFocus() }
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    var currencyMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        AssistChip(
                            onClick = { currencyMenuExpanded = true },
                            label = { Text(state.currency?.code ?: stringResource(R.string.label_currency)) },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            shape = FieldShape,
                            modifier = Modifier.heightIn(min = 48.dp)
                        )
                        DropdownMenu(
                            expanded = currencyMenuExpanded,
                            onDismissRequest = { currencyMenuExpanded = false }
                        ) {
                            state.currencies.forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text("${curr.code} - ${curr.name}") },
                                    onClick = {
                                        viewModel.setCurrencyId(curr.id)
                                        currencyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Info (Category): yozish mumkin bo'lgan ExposedDropdownMenu + tezkor chip'lar
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
                            trailingIcon = {
                                if (state.categoryText.isNotEmpty()) {
                                    IconButton(onClick = {
                                        viewModel.setCategoryText("")
                                        categoryMenuExpanded = false
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.action_clear))
                                    }
                                } else {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuVisible)
                                }
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
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            items(state.categorySuggestions, key = { it }) { name ->
                                FilterChip(
                                    selected = name.equals(state.categoryText.trim(), ignoreCase = true),
                                    onClick = {
                                        viewModel.setCategoryText(name)
                                        categoryMenuExpanded = false
                                    },
                                    label = { Text(name, maxLines = 1) },
                                    modifier = Modifier.heightIn(min = 48.dp)
                                )
                            }
                        }
                    }
                }

                DateSection(date = state.date, onDateChange = viewModel::setDate)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeSelector(type: TransactionType, onSelect: (TransactionType) -> Unit) {
    val options = listOf(
        TransactionType.EXPENSE to R.string.type_expense,
        TransactionType.INCOME to R.string.type_income,
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (option, labelRes) ->
            val semantic = if (option == TransactionType.INCOME) IncomeColor else ExpenseColor
            // Matn rangi semantik rangning onSurface tomon biroz to'qlashtirilgan/yorqinlashtirilgan tusi:
            // 14% tint fonida ham 4.5:1 kontrast saqlanadi (light va dark sxemalarda).
            val content = lerp(semantic, MaterialTheme.colorScheme.onSurface, 0.3f)
            SegmentedButton(
                selected = type == option,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = semantic.copy(alpha = 0.14f),
                    activeContentColor = content,
                    activeBorderColor = semantic,
                ),
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(stringResource(labelRes), maxLines = 1)
            }
        }
    }
}

@Composable
private fun AmountField(
    amount: String,
    onAmountChange: (String) -> Unit,
    currencyCode: String,
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
    val decimalSeparator = remember { DecimalFormatSymbols.getInstance().decimalSeparator }
    val transformation = remember(decimalSeparator) { AmountVisualTransformation(decimalSeparator) }
    val amountLabel = stringResource(R.string.label_amount)

    // Uzun summa va katta shriftda (1.3x-2.0x) kesilmasligi uchun o'lcham kichrayadi
    val effectiveLength = field.text.length * LocalDensity.current.fontScale
    val baseStyle: TextStyle = when {
        effectiveLength <= 10f -> MaterialTheme.typography.displaySmall
        effectiveLength <= 15f -> MaterialTheme.typography.headlineMedium
        effectiveLength <= 22f -> MaterialTheme.typography.titleLarge
        else -> MaterialTheme.typography.titleMedium
    }
    val textStyle = baseStyle.copy(color = color, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { onNext() }),
                modifier = modifier
                    .semantics { contentDescription = amountLabel }
                    .weight(1f, fill = false)
                    .width(IntrinsicSize.Max)
                    .heightIn(min = 48.dp),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.Center) {
                        if (field.text.isEmpty()) {
                            Text("0", style = textStyle.copy(color = color.copy(alpha = 0.4f)))
                        }
                        inner()
                    }
                }
            )
            if (currencyCode.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currencyCode,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 48.dp),
            thickness = if (errorText != null) 1.dp else 0.5.dp,
            color = if (errorText != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
        )
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSection(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val today = LocalDate.now()
    val dateLabel = stringResource(R.string.label_date)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Butun maydon bosiladigan: readOnly field ustida shaffof clickable qatlam
        Box {
            OutlinedTextField(
                value = FormatUtils.formatDate(date),
                onValueChange = {},
                readOnly = true,
                label = { Text(dateLabel) },
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                singleLine = true,
                shape = FieldShape,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 8.dp)
                    .clickable(role = Role.Button, onClickLabel = dateLabel) { showPicker = true }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = date == today,
                onClick = { onDateChange(today) },
                label = { Text(stringResource(R.string.date_today)) },
                modifier = Modifier.heightIn(min = 48.dp)
            )
            FilterChip(
                selected = date == today.minusDays(1),
                onClick = { onDateChange(today.minusDays(1)) },
                label = { Text(stringResource(R.string.date_yesterday)) },
                modifier = Modifier.heightIn(min = 48.dp)
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
