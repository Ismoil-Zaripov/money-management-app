package uz.moneymanager.feature.transactions.ui.filter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R

/** Sticky "Show N Results" button. [count] is null until the first count arrives. */
@Composable
internal fun TransactionFilterFooter(count: Int?, onShow: () -> Unit) {
    Button(
        onClick = onShow,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            if (count == null) {
                stringResource(R.string.filter_show_results_plain)
            } else {
                pluralStringResource(R.plurals.filter_show_results, count, count)
            }
        )
    }
}
