package uz.moneymanager.feature.rates.ui.component

import uz.moneymanager.core.designsystem.theme.ScreenHorizontalPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R

/** "Import CBU" action; shows a spinner and is disabled while the import runs. */
@Composable
fun RatesImportButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.rates_import_cbu),
) {
    Button(onClick = onClick, enabled = !isLoading, modifier = modifier) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
        } else {
            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun RatesImportRow(isLoading: Boolean, onImport: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenHorizontalPadding, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        RatesImportButton(isLoading = isLoading, onClick = onImport)
    }
}
