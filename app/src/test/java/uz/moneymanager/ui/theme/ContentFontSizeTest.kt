package uz.moneymanager.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test
import uz.moneymanager.data.repository.SettingsRepository

class ContentFontSizeTest {

    @Test
    fun testClampFontSize_limitsBetweenMinAndMax() {
        // (a) clampFontSize: 5 -> 10, 25 -> 18, 14 -> 14, 10 -> 10, 18 -> 18
        assertEquals(10, SettingsRepository.clampFontSize(5))
        assertEquals(18, SettingsRepository.clampFontSize(25))
        assertEquals(14, SettingsRepository.clampFontSize(14))
        assertEquals(10, SettingsRepository.clampFontSize(10))
        assertEquals(18, SettingsRepository.clampFontSize(18))
    }

    @Test
    fun testDefaultFontSize_isTwelve() {
        // (b) saqlangan qiymat yo'q bo'lsa default 12
        assertEquals(12, SettingsRepository.DEFAULT_FONT_SIZE)
        assertEquals(10, SettingsRepository.MIN_FONT_SIZE)
        assertEquals(18, SettingsRepository.MAX_FONT_SIZE)
        assertEquals(12, SettingsRepository.clampFontSize(SettingsRepository.DEFAULT_FONT_SIZE))
    }

    @Test
    fun testComputeContentLineHeight_proportionalFourThirds() {
        // (c) computeContentLineHeight: 12 -> 16, 18 -> 24, 10 -> 13.333334f
        assertEquals(16f, computeContentLineHeight(12f), 0.001f)
        assertEquals(24f, computeContentLineHeight(18f), 0.001f)
        assertEquals(13.333334f, computeContentLineHeight(10f), 0.01f)
        assertEquals(18.666668f, computeContentLineHeight(14f), 0.01f)
    }
}
