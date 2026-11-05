package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R

@Composable
internal fun SaveBar(isSaving: Boolean, enabled: Boolean, onSave: () -> Unit) {
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
