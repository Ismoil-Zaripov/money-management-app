package uz.moneymanager.feature.settings.ui.component

import androidx.annotation.StringRes
import uz.moneymanager.R
import uz.moneymanager.domain.model.AppPalette

/** Display name of a palette. */
@StringRes
internal fun AppPalette.nameRes(): Int = when (this) {
    AppPalette.CORPORATE_BLUE -> R.string.palette_corporate_blue
    AppPalette.NAVY_PROFESSIONAL -> R.string.palette_navy_professional
    AppPalette.SLATE_NEUTRAL -> R.string.palette_slate_neutral
    AppPalette.TEAL_FINANCE -> R.string.palette_teal_finance
    AppPalette.FOREST_GREEN -> R.string.palette_forest_green
    AppPalette.AUBERGINE -> R.string.palette_aubergine
    AppPalette.DARK_SLATE -> R.string.palette_dark_slate
    AppPalette.HIGH_CONTRAST -> R.string.palette_high_contrast
    AppPalette.BURGUNDY -> R.string.palette_burgundy
    AppPalette.GRAPHITE_RED -> R.string.palette_graphite_red
}
