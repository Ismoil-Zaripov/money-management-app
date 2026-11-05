package uz.moneymanager.feature.transactionform.ui.component

import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import uz.moneymanager.R

/** Pastdagi sticky "Save Transaction" paneli; xato bo'lsa tugma ostida xatolar soni haqida matn. */
@Composable
internal fun SaveBar(
    isSaving: Boolean,
    enabled: Boolean,
    errorCount: Int,
    saveLabel: String,
    onSave: () -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    val resources = LocalContext.current.resources
    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { if (!isSaving) onSave() },
                // Saqlash paytida tugma "faol" ko'rinishda qoladi (progress ko'rinishi uchun), lekin bosish bloklangan
                enabled = enabled || isSaving,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = saveLabel,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1
                    )
                }
            }
            if (errorCount > 0) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = resources.getQuantityString(R.plurals.form_errors_hint, errorCount, errorCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (onDelete != null) {
                TextButton(
                    onClick = onDelete,
                    enabled = !isSaving,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_delete_transaction))
                }
            }
        }
    }
}
