package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.barContainer
import uz.moneymanager.core.designsystem.theme.onBarContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsTopBar() {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.title_settings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
