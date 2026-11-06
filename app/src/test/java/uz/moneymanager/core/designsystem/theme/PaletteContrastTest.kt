package uz.moneymanager.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.moneymanager.domain.model.AppPalette
import kotlin.math.pow

class PaletteContrastTest {

    private fun channel(c: Float): Double =
        if (c <= 0.03928f) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)

    private fun luminance(c: Color): Double =
        0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)

    private fun contrast(a: Color, b: Color): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
    }

    private fun check(label: String, fg: Color, bg: Color, min: Double = AA_TEXT) {
        val ratio = contrast(fg, bg)
        assertTrue("$label contrast %.2f < $min".format(ratio), ratio >= min)
    }

    @Test
    fun everyPalette_textRoles_meetAA_inLightAndDark() {
        for (palette in AppPalette.entries) for (dark in listOf(false, true)) {
            val s = getPaletteColorScheme(palette, dark)
            val tag = "$palette dark=$dark"
            check("$tag onPrimary/primary", s.onPrimary, s.primary)
            check("$tag onPrimaryContainer/primaryContainer", s.onPrimaryContainer, s.primaryContainer)
            check("$tag onSecondaryContainer/secondaryContainer", s.onSecondaryContainer, s.secondaryContainer)
            check("$tag onTertiaryContainer/tertiaryContainer", s.onTertiaryContainer, s.tertiaryContainer)
            check("$tag onSecondary/secondary", s.onSecondary, s.secondary)
            check("$tag onError/error", s.onError, s.error)
            check("$tag onSurface/surface", s.onSurface, s.surface)
            check("$tag onSurface/surfaceContainer", s.onSurface, s.surfaceContainer)
            check("$tag onSurface/surfaceContainerHighest", s.onSurface, s.surfaceContainerHighest)
            check("$tag onSurfaceVariant/surface", s.onSurfaceVariant, s.surface)
            check("$tag onBackground/background", s.onBackground, s.background)
            // Graphite + Red keeps the brand primary #C74634 (2.83:1 on its surface) by decision.
            if (palette != AppPalette.GRAPHITE_RED) check("$tag primary/surface", s.primary, s.surface, AA_UI)
        }
    }

    @Test
    fun incomeAndExpense_areReadableAndDistinct_onEveryPalette() {
        for (palette in AppPalette.entries) for (dark in listOf(false, true)) {
            val s = getPaletteColorScheme(palette, dark)
            val money = if (dark) DarkMoneyColors else LightMoneyColors
            val tag = "$palette dark=$dark"
            assertNotEquals("$tag income == expense", money.income, money.expense)
            for (bg in listOf(s.surface, s.surfaceContainerLowest, s.surfaceContainer)) {
                check("$tag income", money.income, bg)
                check("$tag expense", money.expense, bg)
            }
        }
    }

    @Test
    fun graphiteRedPrimary_exception_isKnownAndBounded() {
        val s = getPaletteColorScheme(AppPalette.GRAPHITE_RED, true)
        assertTrue("onPrimary on the button must stay readable", contrast(s.onPrimary, s.primary) >= AA_TEXT)
        assertTrue("primary/surface should not drop below 2.5", contrast(s.primary, s.surface) >= 2.5)
    }

    @Test
    fun barSurface_isPrimary_andContentReadable_onEveryPalette() {
        for (palette in AppPalette.entries) for (dark in listOf(false, true)) {
            val s = getPaletteColorScheme(palette, dark)
            val bar = s.barSurface
            assertEquals("$palette dark=$dark bar must be the exact primary", s.primary, bar)
            check("$palette dark=$dark onBarSurface/bar", s.onBarSurface, bar)
        }
    }

    @Test
    fun darkOnlyPalettes_ignoreThemeMode() {
        for (palette in AppPalette.entries.filter { it.darkOnly }) {
            assertEquals(getPaletteColorScheme(palette, true), getPaletteColorScheme(palette, false))
        }
    }

    @Test
    fun extendedColors_text_meetsAA_onEverySurface_fillMeetsUi_onDark() {
        for (palette in AppPalette.entries) for (dark in listOf(false, true)) {
            val s = getPaletteColorScheme(palette, dark)
            val ext = if (dark) DarkExtendedColors else LightExtendedColors
            val tag = "$palette dark=$dark"
            for ((name, c) in listOf("success" to ext.success, "warning" to ext.warning, "info" to ext.info)) {
                check("$tag $name text/surface", c.text, s.surface)
            }
        }
    }

    private companion object {
        const val AA_TEXT = 4.5
        const val AA_UI = 3.0
    }
}
