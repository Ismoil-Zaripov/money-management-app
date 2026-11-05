package uz.moneymanager.feature.transactions.ui.sheet

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.math.BigDecimal
import uz.moneymanager.core.designsystem.theme.ExpenseColor
import uz.moneymanager.core.designsystem.theme.IncomeColor

/** Balans rangi: musbat - income, manfiy - expense, nol - onSurface (Details va Chart uchun yagona qoida). */
@Composable
internal fun balanceColor(balance: BigDecimal): Color = when (balance.signum()) {
    1 -> IncomeColor
    -1 -> ExpenseColor
    else -> MaterialTheme.colorScheme.onSurface
}
