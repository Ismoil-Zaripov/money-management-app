package uz.moneymanager.feature.categories.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.moneymanager.data.local.CategoryWithUsage
import uz.moneymanager.feature.categories.presentation.CategoriesViewModel
import uz.moneymanager.feature.categories.presentation.CategoryEvent
import uz.moneymanager.feature.categories.ui.dialog.DeleteCategoryDialog
import uz.moneymanager.feature.categories.ui.dialog.RenameCategoryDialog

@Composable
fun CategoriesRoute(
    onNavigateBack: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var query by rememberSaveable { mutableStateOf("") }
    var categoryToRename by remember { mutableStateOf<CategoryWithUsage?>(null) }
    var categoryToDelete by remember { mutableStateOf<CategoryWithUsage?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CategoryEvent.Success -> {}
                is CategoryEvent.Error -> {
                    val msg = context.getString(event.messageRes, *event.args.toTypedArray())
                    snackbarHostState.showSnackbar(msg)
                }
            }
        }
    }

    CategoriesScreen(
        state = state,
        query = query,
        onQueryChange = { query = it },
        snackbarHostState = snackbarHostState,
        onBack = onNavigateBack,
        onRename = { categoryToRename = it },
        onDelete = { categoryToDelete = it }
    )

    categoryToRename?.let { item ->
        RenameCategoryDialog(
            item = item,
            onConfirm = { name ->
                viewModel.renameCategory(item.category.id, name)
                categoryToRename = null
            },
            onDismiss = { categoryToRename = null }
        )
    }
    categoryToDelete?.let { item ->
        DeleteCategoryDialog(
            item = item,
            onConfirm = {
                viewModel.deleteCategory(item.category.id)
                categoryToDelete = null
            },
            onDismiss = { categoryToDelete = null }
        )
    }
}
