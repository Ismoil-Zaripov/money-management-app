package uz.moneymanager.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Small pill label, e.g. "Income" or "Default". */
@Composable
fun MmBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Text(
        text = text,
        modifier = modifier
            .background(containerColor, MaterialTheme.shapes.small)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        color = contentColor,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
    )
}

@MmPreviews
@Composable
private fun MmBadgePreview() = MmPreviewHost { MmBadge("Income") }
