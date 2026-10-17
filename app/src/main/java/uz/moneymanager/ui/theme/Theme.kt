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
import uz.moneymanager.domain.model.ThemeMode

// MUI Teal & Indigo inspired palette
val PrimaryTeal = Color(0xFF00796B)
val PrimaryTealDark = Color(0xFF4DB6AC)
val SecondaryAmber = Color(0xFFFFB300)
val IncomeGreen = Color(0xFF2E7D32)
val ExpenseRed = Color(0xFFC62828)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF004D40),
    secondary = SecondaryAmber,
    onSecondary = Color.Black,
    background = Color(0xFFFBFDFD),
    surface = Color.White,
    onBackground = Color(0xFF191C1C),
    onSurface = Color(0xFF191C1C),
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTealDark,
    onPrimary = Color(0xFF003730),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFFB2DFDB),
    secondary = SecondaryAmber,
    onSecondary = Color.Black,
    background = Color(0xFF191C1C),
    surface = Color(0xFF202525),
    onBackground = Color(0xFFE0E3E3),
    onSurface = Color(0xFFE0E3E3),
)

@Composable
fun MoneyManagerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
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
