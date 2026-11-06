package uz.moneymanager.core.designsystem.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.moneymanager.data.repository.SettingsRepository

class GlassTransparencyTest {

    @Test
    fun defaultTransparency_keepsFormerAlpha() {
        // 28% transparency == the former fixed tint alpha 0.72
        assertEquals(0.72f, glassTintAlpha(SettingsRepository.DEFAULT_GLASS_TRANSPARENCY), 1e-6f)
    }

    @Test
    fun boundaries_zeroIsOpaque_hundredIsFullyTransparent() {
        assertEquals(1f, glassTintAlpha(0), 0f)
        assertEquals(0f, glassTintAlpha(100), 0f)
    }

    @Test
    fun outOfRangeValues_areClamped() {
        assertEquals(1f, glassTintAlpha(-20), 0f)
        assertEquals(0f, glassTintAlpha(250), 0f)
        assertEquals(0, SettingsRepository.clampGlassTransparency(-1))
        assertEquals(100, SettingsRepository.clampGlassTransparency(101))
        assertEquals(42, SettingsRepository.clampGlassTransparency(42))
    }

    @Test
    fun alpha_decreasesAsTransparencyGrows() {
        assertEquals(0.5f, glassTintAlpha(50), 1e-6f)
        assertTrue(glassTintAlpha(30) > glassTintAlpha(31))
    }
}
