package uz.moneymanager.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import uz.moneymanager.core.designsystem.theme.MoneyManagerTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class MmPreviews

/** Wraps design-system previews in the app theme (light/dark follows the preview uiMode). */
@Composable
internal fun MmPreviewHost(content: @Composable () -> Unit) {
    MoneyManagerTheme {
        Surface(color = MaterialTheme.colorScheme.surface, content = content)
    }
}
