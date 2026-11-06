package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.moneymanager.core.designsystem.theme.contentText

/** Large tappable action card: icon tile, title, hint and a chevron. */
@Composable
fun MoreActionCard(
    icon: ImageVector,
    title: String,
    hint: String,
    containerColor: Color,
    contentColor: Color,
    tileColor: Color,
    tileContentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.large
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.heightIn(min = 72.dp).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = tileColor, contentColor = tileContentColor, shape = CircleShape, modifier = Modifier.size(44.dp)) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.contentText.copy(fontWeight = FontWeight.SemiBold), color = contentColor)
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.78f)
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = contentColor.copy(alpha = 0.7f))
        }
    }
}
