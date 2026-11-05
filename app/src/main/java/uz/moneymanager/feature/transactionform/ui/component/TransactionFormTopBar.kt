package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.barContainer
import uz.moneymanager.core.designsystem.theme.onBarContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionFormTopBar(isNew: Boolean, onBack: () -> Unit) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(
                        if (isNew) R.string.title_new_transaction else R.string.title_edit_transaction
                    ),
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
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.barContainer,
                titleContentColor = MaterialTheme.colorScheme.onBarContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onBarContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onBarContainer
            )
        )
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBarContainer.copy(alpha = 0.2f))
    }
}
