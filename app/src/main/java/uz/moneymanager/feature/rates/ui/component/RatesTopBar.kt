package uz.moneymanager.feature.rates.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.barContainer
import uz.moneymanager.core.designsystem.theme.onBarContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatesTopBar(
    isLoading: Boolean,
    onBack: () -> Unit,
    onImport: () -> Unit,
) {
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
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            },
            actions = { RatesImportAction(isLoading = isLoading, onImport = onImport) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.barContainer,
                titleContentColor = MaterialTheme.colorScheme.onBarContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onBarContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onBarContainer
            )
        )
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onBarContainer.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun RatesImportAction(isLoading: Boolean, onImport: () -> Unit) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(22.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.onBarContainer
        )
    } else {
        IconButton(onClick = onImport) {
            Icon(
                Icons.Default.CloudDownload,
                contentDescription = stringResource(R.string.action_import_cbu)
            )
        }
    }
}
