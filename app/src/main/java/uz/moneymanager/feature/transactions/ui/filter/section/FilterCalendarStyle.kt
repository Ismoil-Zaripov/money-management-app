package uz.moneymanager.feature.transactions.ui.filter.section

import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import uz.moneymanager.core.designsystem.theme.contentText

/**
 * The M3 picker sizes day numbers, weekdays and the month label from the theme's body/label
 * styles (16 sp, unrelated to the app's font-size setting). Re-map them to the app's content text.
 */
@Composable
internal fun FilterCalendarTheme(content: @Composable () -> Unit) {
    val base = MaterialTheme.contentText
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        shapes = MaterialTheme.shapes,
        typography = MaterialTheme.typography.copy(
            bodyLarge = base.copy(fontWeight = FontWeight.Medium),
            labelLarge = base.copy(fontWeight = FontWeight.SemiBold),
        ),
        content = content,
    )
}

/** Palette-driven picker colors: transparent surface, primary endpoints, soft band between them. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun filterDatePickerColors(): DatePickerColors {
    val c = MaterialTheme.colorScheme
    return DatePickerDefaults.colors(
        containerColor = Color.Transparent,
        weekdayContentColor = c.onSurfaceVariant,
        subheadContentColor = c.onSurface,
        navigationContentColor = c.onSurfaceVariant,
        dayContentColor = c.onSurface,
        selectedDayContainerColor = c.primary,
        selectedDayContentColor = c.onPrimary,
        todayContentColor = c.primary,
        todayDateBorderColor = c.primary,
        dayInSelectionRangeContainerColor = c.primaryContainer,
        dayInSelectionRangeContentColor = c.onPrimaryContainer,
    )
}
