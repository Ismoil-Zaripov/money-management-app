package uz.moneymanager.feature.categories.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.component.MmScaffold
import uz.moneymanager.core.designsystem.component.MmPage
import uz.moneymanager.core.designsystem.component.MmCardListSkeleton
import uz.moneymanager.core.designsystem.component.MmEmptyState
import uz.moneymanager.core.designsystem.component.MmSectionHeader
import uz.moneymanager.core.designsystem.component.MmTopBar
import uz.moneymanager.data.local.CategoryWithUsage
import uz.moneymanager.feature.categories.presentation.CategoriesUiState
import uz.moneymanager.feature.categories.presentation.filterCategories
import uz.moneymanager.feature.categories.ui.component.CategoriesInfoBanner
import uz.moneymanager.feature.categories.ui.component.CategoryCard
import uz.moneymanager.feature.categories.ui.component.CategorySearchField

private val ItemPadding = Modifier.padding(horizontal = ScreenHorizontalPadding)

@Composable
fun CategoriesScreen(
    state: CategoriesUiState,
    query: String,
    snackbarHostState: SnackbarHostState,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onRename: (CategoryWithUsage) -> Unit,
    onDelete: (CategoryWithUsage) -> Unit,
) {
    val visible = remember(state.categories, query) { filterCategories(state.categories, query) }
    val total = state.categories.size

    MmScaffold(
        topBar = { MmTopBar(title = stringResource(R.string.title_categories), onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        MmPage(
            loaded = state.loaded,
            skeleton = { MmCardListSkeleton(PaddingValues(top = padding.calculateTopPadding() + 8.dp)) },
        ) {
            if (total == 0) {
                MmEmptyState(
                    title = stringResource(R.string.empty_categories),
                    message = stringResource(R.string.categories_empty_hint),
                    modifier = Modifier.padding(padding)
                )
                return@MmPage
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "info", contentType = "info") { CategoriesInfoBanner(ItemPadding) }
                item(key = "search", contentType = "search") {
                    CategorySearchField(query = query, onQueryChange = onQueryChange, modifier = ItemPadding)
                }
                item(key = "header", contentType = "header") {
                    MmSectionHeader(
                        title = stringResource(R.string.categories_section_title),
                        trailing = pluralStringResource(R.plurals.categories_active_count, total, total)
                    )
                }
                if (visible.isEmpty()) {
                    item(key = "no_results", contentType = "no_results") {
                        MmEmptyState(title = stringResource(R.string.sheet_no_results), modifier = Modifier.padding(vertical = 24.dp))
                    }
                }
                items(visible, key = { it.category.id }, contentType = { "category" }) { item ->
                    CategoryCard(
                        item = item,
                        onRename = { onRename(item) },
                        onDelete = { onDelete(item) },
                        modifier = ItemPadding
                    )
                }
            }
        }
    }
}
