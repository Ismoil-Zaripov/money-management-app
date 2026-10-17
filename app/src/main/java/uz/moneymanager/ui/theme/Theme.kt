package uz.moneymanager.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.luminance
import uz.moneymanager.domain.model.ThemeMode

// Official Material-UI (MUI) Default Palette
val MuiPrimary = Color(0xFF1976D2)
val MuiPrimaryDark = Color(0xFF1565C0)
val SecondaryAmber = Color(0xFFFFB300)

val IncomeLight = Color(0xFF2E7D32)
val IncomeDark = Color(0xFF66BB6A)
val ExpenseLight = Color(0xFFC62828)
val ExpenseDark = Color(0xFFEF5350)

val IncomeColor: Color
    @Composable
    get() = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) IncomeDark else IncomeLight

val ExpenseColor: Color
    @Composable
    get() = if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) ExpenseDark else ExpenseLight

val IncomeGreen = IncomeLight
val ExpenseRed = ExpenseLight

private val LightColorScheme = lightColorScheme(
    primary = MuiPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = SecondaryAmber,
    onSecondary = Color.Black,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onBackground = Color(0xFF191C1C),
    onSurface = Color(0xFF191C1C),
)

private val DarkColorScheme = darkColorScheme(
    primary = MuiPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = SecondaryAmber,
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E3E3),
    onSurface = Color(0xFFE0E3E3),
)

@Composable
fun MoneyManagerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
