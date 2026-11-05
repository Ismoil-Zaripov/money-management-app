package uz.moneymanager.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantic income/expense colors, independent of the selected palette. */
@Immutable
data class MoneyColors(val income: Color, val expense: Color)

val LightMoneyColors = MoneyColors(income = IncomeLight, expense = ExpenseLight)
val DarkMoneyColors = MoneyColors(income = IncomeDark, expense = ExpenseDark)

val LocalMoneyColors = staticCompositionLocalOf { LightMoneyColors }

val MaterialTheme.moneyColors: MoneyColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMoneyColors.current
