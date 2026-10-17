package uz.moneymanager.ui.theme

import android.app.Activity
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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

/**
 * Bar colors: in light mode bar = primary and content = onPrimary.
 * In dark mode bar = primaryContainer (deep tonal shade) and content = onPrimaryContainer.
 */
val ColorScheme.barContainer: Color
    get() = if (surface.luminance() < 0.5f) primaryContainer else primary

val ColorScheme.onBarContainer: Color
    get() = if (surface.luminance() < 0.5f) onPrimaryContainer else onPrimary

@Composable
fun SystemBarsEffect(statusBarBackground: Color, navBarBackground: Color) {
    val view = LocalView.current
    if (view.isInEditMode) return

    val context = view.context
    val activity = remember(context) {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return@remember ctx
            ctx = ctx.baseContext
        }
        null
    } ?: return

    val darkStatusIcons = statusBarBackground.luminance() > 0.5f
    val darkNavIcons = navBarBackground.luminance() > 0.5f

    SideEffect {
        val window = activity.window
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = darkStatusIcons
        insetsController.isAppearanceLightNavigationBars = darkNavIcons
    }
}

@Composable
fun MoneyManagerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    palette: AppPalette = AppPalette.EMERALD,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = remember(palette, darkTheme, dynamicColor) {
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            getPaletteColorScheme(palette, darkTheme)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
