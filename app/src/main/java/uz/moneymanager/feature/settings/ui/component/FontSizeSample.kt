package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.computeContentLineHeight

/** Jonli namuna qatori (tanlangan o'lcham bilan) va ixcham "Reset" tugmasi. */
@Composable
internal fun FontSizeSample(currentSp: Int, canReset: Boolean, onReset: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.font_size_sample),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = currentSp.sp,
                    lineHeight = computeContentLineHeight(currentSp.toFloat()).sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
            )
            TextButton(onClick = onReset, enabled = canReset) {
                Text(stringResource(R.string.font_size_reset_short), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
