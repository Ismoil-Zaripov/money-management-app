package uz.moneymanager.feature.settings.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.moneymanager.R
import uz.moneymanager.core.designsystem.theme.computeContentLineHeight

/** Jonli namuna qatori: tanlangan o'lcham bilan matn. */
@Composable
internal fun FontSizeSample(currentSp: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            val sampleStyle = MaterialTheme.typography.bodySmall.copy(
                fontSize = currentSp.sp,
                lineHeight = computeContentLineHeight(currentSp.toFloat()).sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.font_size_sample),
                style = sampleStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
