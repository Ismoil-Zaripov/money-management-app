package uz.moneymanager.feature.transactions.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.barContainer
import uz.moneymanager.core.designsystem.theme.onBarContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsTopBar(onReset: () -> Unit) {
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
                IconButton(onClick = onReset) {
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
}
