package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.contentText
import uz.moneymanager.domain.model.AppLanguage

@Composable
internal fun LanguageSelector(selected: AppLanguage, onSelect: (AppLanguage) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    val currentText = when (selected) {
        AppLanguage.EN -> stringResource(R.string.lang_en)
        AppLanguage.UZ -> stringResource(R.string.lang_uz)
        AppLanguage.RU -> stringResource(R.string.lang_ru)
    }

    SettingsItem(
        icon = Icons.Default.Language,
        title = stringResource(R.string.setting_language),
        subtitle = currentText,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.setting_language)) },
            text = {
                Column {
                    AppLanguage.entries.forEach { x ->
                        val text = when (x) {
                            AppLanguage.EN -> stringResource(R.string.lang_en)
                            AppLanguage.UZ -> stringResource(R.string.lang_uz)
                            AppLanguage.RU -> stringResource(R.string.lang_ru)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(x)
                                    showDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected == x,
                                onClick = {
                                    onSelect(x)
                                    showDialog = false
                                }
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.contentText,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.action_close))
                }
            }
        )
    }
}
