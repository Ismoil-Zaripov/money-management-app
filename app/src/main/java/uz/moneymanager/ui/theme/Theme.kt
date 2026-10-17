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
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.domain.model.ThemeMode

// Income / Expense colors
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

@Composable
fun MoneyManagerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    palette: AppPalette = AppPalette.OCEAN,
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
        else -> getPaletteColorScheme(palette, darkTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
