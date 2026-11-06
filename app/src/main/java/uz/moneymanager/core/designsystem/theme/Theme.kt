package uz.moneymanager.core.designsystem.theme

import android.app.Activity
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import uz.moneymanager.data.repository.SettingsRepository
import uz.moneymanager.domain.model.AppPalette
import uz.moneymanager.domain.model.ThemeMode

// Income / Expense colors
val IncomeLight = Color(0xFF2A782E)
val IncomeDark = Color(0xFF66BB6A)
val ExpenseLight = Color(0xFFC62828)
val ExpenseDark = Color(0xFFF58886)

val IncomeColor: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalMoneyColors.current.income

val ExpenseColor: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalMoneyColors.current.expense

/** Top app bar and bottom navigation bar are painted with the palette primary itself. */
val ColorScheme.barSurface: Color
    get() = primary

/** Title, icon and label color on [barSurface]. */
val ColorScheme.onBarSurface: Color
    get() = onPrimary

val LocalContentFontSize = staticCompositionLocalOf { SettingsRepository.DEFAULT_FONT_SIZE }

fun computeContentLineHeight(fontSizeSp: Float): Float = fontSizeSp * 4f / 3f

val MaterialTheme.contentText: TextStyle
    @Composable
    @ReadOnlyComposable
    get() {
        val size = LocalContentFontSize.current
        return typography.bodySmall.copy(
            fontSize = size.sp,
            lineHeight = computeContentLineHeight(size.toFloat()).sp,
        )
    }

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
    palette: AppPalette = AppPalette.DEFAULT,
    contentFontSize: Int = SettingsRepository.DEFAULT_FONT_SIZE,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = palette.darkOnly || when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
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
    ) {
        CompositionLocalProvider(
            LocalContentFontSize provides contentFontSize,
            LocalMoneyColors provides if (darkTheme) DarkMoneyColors else LightMoneyColors,
            LocalExtendedColors provides if (darkTheme) DarkExtendedColors else LightExtendedColors,
            content = content,
        )
    }
}
