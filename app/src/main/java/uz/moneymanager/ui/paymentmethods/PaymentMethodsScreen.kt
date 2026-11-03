package uz.moneymanager.ui.paymentmethods

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.R
import uz.moneymanager.data.local.PaymentMethodWithUsage
import uz.moneymanager.ui.theme.barContainer
import uz.moneymanager.ui.theme.contentText
import uz.moneymanager.ui.theme.onBarContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PaymentMethodsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val paymentMethods by viewModel.paymentMethods.collectAsStateWithLifecycle()

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var renameTargetId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deleteTargetId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PaymentMethodEvent.Success -> {}
                is PaymentMethodEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
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
                            text = stringResource(R.string.title_payment_methods),
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
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add_payment_method))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (paymentMethods.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_payment_methods),
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
                    items(paymentMethods, key = { it.paymentMethod.id }) { item ->
                        PaymentMethodCard(
                            item = item,
                            onRename = { renameTargetId = item.paymentMethod.id },
                            onDelete = {
                                if (item.transactionCount > 0) {
                                    viewModel.deletePaymentMethod(item.paymentMethod.id)
                                } else {
                                    deleteTargetId = item.paymentMethod.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        var name by rememberSaveable { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.action_add_payment_method)) },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.label_payment_method_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = name.trim()
                        if (trimmed.isNotEmpty()) {
                            viewModel.addPaymentMethod(trimmed)
                            showAddDialog = false
                        }
                    },
                    enabled = name.isNotBlank()
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

    // Rename Dialog
    val paymentMethodToRename = paymentMethods.firstOrNull { it.paymentMethod.id == renameTargetId }
    paymentMethodToRename?.let { item ->
        var name by rememberSaveable(item.paymentMethod.id) { mutableStateOf(item.paymentMethod.name) }

        AlertDialog(
            onDismissRequest = { renameTargetId = null },
            title = { Text(stringResource(R.string.action_rename_payment_method)) },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.label_payment_method_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = name.trim()
                        if (trimmed.isNotEmpty()) {
                            viewModel.renamePaymentMethod(item.paymentMethod.id, trimmed)
                            renameTargetId = null
                        }
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTargetId = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // Delete Confirmation
    val paymentMethodToDelete = paymentMethods.firstOrNull { it.paymentMethod.id == deleteTargetId }
    paymentMethodToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteTargetId = null },
            title = { Text(stringResource(R.string.action_delete)) },
            text = {
                Text(
                    text = stringResource(R.string.delete_payment_method_confirm, item.paymentMethod.name),
                    style = MaterialTheme.contentText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePaymentMethod(item.paymentMethod.id)
                        deleteTargetId = null
                    }
                ) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTargetId = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun PaymentMethodCard(
    item: PaymentMethodWithUsage,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.paymentMethod.name,
                            style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (item.paymentMethod.isDefault) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Text(
                                    text = stringResource(R.string.payment_method_default_badge),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.payment_method_tx_count, item.transactionCount),
                        style = MaterialTheme.contentText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onRename) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.action_edit))
                }

                IconButton(
                    onClick = onDelete,
                    enabled = !item.paymentMethod.isDefault
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = if (item.transactionCount > 0 || item.paymentMethod.isDefault) {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
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
